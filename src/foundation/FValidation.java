package foundation;

import java.util.HashMap;
import java.util.Map;

import model.MFeatureVersion;
import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class FValidation {
	
	int dbgLevel = 100;
	protected FTripleStore triplestore;
	
	public FValidation() {
		try {
			this.triplestore = (FTripleStore) Class.forName("foundation."+UConfig.triplestoreConnectionClass).newInstance();
		} catch (ClassNotFoundException e) {
			UDebug.print("triplestoreConnectionClass NOT FOUND. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded", 1);
			this.triplestore = new FParliament();
		} catch (InstantiationException e) {
			UDebug.print("Cannot instantiate triplestoreConnectionClass. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded", 1);
			this.triplestore = new FParliament();
		} catch (IllegalAccessException e) {
			UDebug.print("Cannot access to triplestoreConnectionClass. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded", 1);
		}	
	}

	public Map<String,Map<String,String>> getIntersectedFV(String wktAuthority) {
		return this.getIntersectedFV(wktAuthority, "");
	}
	
	public Map<String,Map<String,String>> getIntersectedFV(String wktAuthority, String graphUri) {
		
		Map<String,Map<String,String>> geometries = new HashMap<String,Map<String,String>>();
		
		String queryString = ""
				+ "\tSELECT ?featureVersionUri ?featureUri (STR(?wktGeometry) AS ?wktString) \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		
		queryString += ""
				+ "\t\t   ?featureVersionUri geosparql:hasGeometry ?geometry .\n"
				+ "\t\t   ?featureVersionUri hvgi:isVersionOf ?featureUri    .\n"
				+ "\t\t   ?geometry geosparql:asWKT ?wktGeometry             .\n"
		;
				
		if (!graphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""				
				+ "\t  FILTER                                                                     \n"
				+ "\t\t  (                                                      			      \n"
				+ "\t\t   geof:sfIntersects(?wktGeometry, \""+ wktAuthority +"\"^^sf:wktLiteral ) \n"
				+ "\t\t  )                                                                        \n"
				+ "\t}																              \n"
				;
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode featureUri = generalQueryResults.getResource("featureUri");
			RDFNode featureVersionUri = generalQueryResults.getResource("featureVersionUri");
			RDFNode geometry = generalQueryResults.getLiteral("wktString");
			
			if ( geometries.containsKey(featureUri.toString()) )
				geometries.get(featureUri.toString()).put(featureVersionUri.toString(), geometry.toString());
			else {
				HashMap<String,String> fvMap = new HashMap<String,String>();
				fvMap.put(featureVersionUri.toString(), geometry.toString());
				geometries.put(featureUri.toString(), fvMap);
			}
//			geometries.put(uri.toString(), geometry.toString());
		}
		
		return geometries;
	}
	
	public String retrieveHighestTrustworthyFVUri(String featureUri, String hvgiGraphUri, String tandrGraphUri) {
		
		String queryString = ""
				+ "\tSELECT (<" + featureUri + "> AS ?fUri) ?fvUri ?trust (str(?maxValue) AS ?value) \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!hvgiGraphUri.equals("")) queryString += "\t GRAPH " +hvgiGraphUri+ "\n\t {\n";
		queryString += "\t\t  <" + featureUri + "> hvgi:hasVersion ?fvUri .\n";
		if (!hvgiGraphUri.equals("")) queryString += "\t }\n";
		
		if (!tandrGraphUri.equals("")) queryString += "\t GRAPH " +tandrGraphUri+ "\n\t {\n";
		queryString += ""
				+ "\t\t   ?trust tandr:refersToFeatureVersion  ?fvUri    .\n"
				+ "\t\t   ?trust tandr:hasTrustworthinessValue ?tValue   .\n"
				+ "\t\t   ?tValue tandr:trustworthinessValueIs ?maxValue .\n"
		;
		
		queryString += ""
				+ "\t\t   { \n"
				+ "\t\t    SELECT (MAX(?value) as ?maxValue) \n"
				+ "\t\t    WHERE \n"
				+ "\t\t    {     \n"
				+ "\t\t     ?trust tandr:refersToFeatureVersion  ?fvUri.   \n"
				+ "\t\t     ?trust tandr:hasTrustworthinessValue ?tValue . \n"
				+ "\t\t     ?tValue tandr:trustworthinessValueIs ?value    \n"
				+ "\t\t    } \n"
				+ "\t\t   }  \n"
				;
		
		
		if (!tandrGraphUri.equals("")) queryString += "\t }\n";
		queryString += "\t} \n"
				+ "ORDER BY DESC(?value) \n"
				+ "LIMIT 1 \n"
				;
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		String fvUri = "";
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("fvUri");
			fvUri = uri.toString();
		}
		
		return fvUri;
	}
	
	public String retrieveAverageTrustworthyFVUri(String featureUri, String hvgiGraphUri, String tandrGraphUri) {
		String queryString = ""
			+ "\tSELECT (<" + featureUri + "> AS ?fUri) ?fvUri ?trustUri ?value ?averageValue (fn:abs( ?value-?averageValue ) AS ?minimumDistance) \n"
			+ "\tWHERE \n"
			+ "\t{ \n";
				
		if (!hvgiGraphUri.equals("")) queryString += "\t GRAPH " +hvgiGraphUri+ "\n\t {\n";
		queryString += "\t\t  <" + featureUri + "> hvgi:hasVersion ?fvUri .\n";
		if (!hvgiGraphUri.equals("")) queryString += "\t }\n";
		
		if (!tandrGraphUri.equals("")) queryString += "\t GRAPH " +tandrGraphUri+ "\n\t {\n";
		queryString += ""
			+ "\t\t   ?trustUri tandr:refersToFeatureVersion  ?fvUri   .\n"
			+ "\t\t   ?trustUri tandr:hasTrustworthinessValue ?trust   .\n"
			+ "\t\t   ?trust    tandr:trustworthinessValueIs  ?value   .\n"
			;
		if (!tandrGraphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""
			+ "\t { \n"
			+ "\t  SELECT (AVG(?value) AS ?averageValue) \n"
			+ "\t  WHERE \n"
			+ "\t  {     \n"
			;
		if (!hvgiGraphUri.equals("")) queryString += "\t   GRAPH " +hvgiGraphUri+ "\n\t   {\n";
		queryString += "\t\t    <" + featureUri + "> hvgi:hasVersion ?fvUri .\n";
		if (!hvgiGraphUri.equals("")) queryString += "\t   }\n";

		if (!tandrGraphUri.equals("")) queryString += "\t GRAPH " +tandrGraphUri+ "\n\t {\n";
		queryString += ""		
			+ "\t\t   ?trustUri tandr:refersToFeatureVersion  ?fvUri .\n"
			+ "\t\t   ?trustUri tandr:hasTrustworthinessValue ?trust .\n"
			+ "\t\t   ?trust    tandr:trustworthinessValueIs  ?value  \n"
			;
		if (!tandrGraphUri.equals("")) queryString += "\t }\n";
		queryString += ""
			+ "\t  } \n"
			+ "\t }  \n"
			+ "\t} \n"
			+ "ORDER BY ASC(?minimumDistance) \n"
			+ "LIMIT 1 \n"
			;
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		String fvUri = "";
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("fvUri");
			fvUri = uri.toString();
		}
		
		return fvUri;
	}
	
	public String retrieveLowestTrustworthyFVUri(String featureUri, String hvgiGraphUri, String tandrGraphUri) {
		String queryString = ""
				+ "\tSELECT (<" + featureUri + "> AS ?fUri) ?fvUri ?trust (str(?minValue) AS ?value) \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!hvgiGraphUri.equals("")) queryString += "\t GRAPH " +hvgiGraphUri+ "\n\t {\n";
		queryString += "\t\t  <" + featureUri + "> hvgi:hasVersion ?fvUri .\n";
		if (!hvgiGraphUri.equals("")) queryString += "\t }\n";
		
		if (!tandrGraphUri.equals("")) queryString += "\t GRAPH " +tandrGraphUri+ "\n\t {\n";
		queryString += ""
				+ "\t\t   ?trust tandr:refersToFeatureVersion  ?fvUri    .\n"
				+ "\t\t   ?trust tandr:hasTrustworthinessValue ?tValue   .\n"
				+ "\t\t   ?tValue tandr:trustworthinessValueIs ?maxValue .\n"
		;
		
		queryString += ""
				+ "\t\t   { \n"
				+ "\t\t    SELECT (MIN(?value) as ?minValue) \n"
				+ "\t\t    WHERE \n"
				+ "\t\t    {     \n"
				+ "\t\t     ?trust tandr:refersToFeatureVersion  ?fvUri.   \n"
				+ "\t\t     ?trust tandr:hasTrustworthinessValue ?tValue . \n"
				+ "\t\t     ?tValue tandr:trustworthinessValueIs ?value    \n"
				+ "\t\t    } \n"
				+ "\t\t   }  \n"
				;
		
		
		if (!tandrGraphUri.equals("")) queryString += "\t }\n";
		queryString += "\t} \n"
				+ "ORDER BY ASC(?value) \n"
				+ "LIMIT 1 \n"
				;
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		String fvUri = "";
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("fvUri");
			fvUri = uri.toString();
		}
		
		return fvUri;
	}
	
	public MFeatureVersion retrieveLowestTrustworthyFV(String featureUri, String hvgiGraphUri, String tandrGraphUri) {
		FFoundationFacade facade = new FFoundationFacade();
		String featureVersionUri = this.retrieveLowestTrustworthyFVUri(featureUri, hvgiGraphUri, tandrGraphUri);
		return (MFeatureVersion) facade.retrieveByUri(featureVersionUri, hvgiGraphUri, 1, MFeatureVersion.class);
	}
	public MFeatureVersion retrieveAverageTrustworthyFV(String featureUri, String hvgiGraphUri, String tandrGraphUri) {
		FFoundationFacade facade = new FFoundationFacade();
		String featureVersionUri = this.retrieveAverageTrustworthyFVUri(featureUri, hvgiGraphUri, tandrGraphUri);
		return (MFeatureVersion) facade.retrieveByUri(featureVersionUri, hvgiGraphUri, 1, MFeatureVersion.class);
	}
	public MFeatureVersion retrieveHighestTrustworthyFV(String featureUri, String hvgiGraphUri, String tandrGraphUri) {
		FFoundationFacade facade = new FFoundationFacade();
		String featureVersionUri = this.retrieveHighestTrustworthyFVUri(featureUri, hvgiGraphUri, tandrGraphUri);
		return (MFeatureVersion) facade.retrieveByUri(featureVersionUri, hvgiGraphUri, 1, MFeatureVersion.class);
	}

}
