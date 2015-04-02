package foundation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom2.Document;

import model.MAuthor;
import model.MEdit;
import model.MFeature;
import model.MFeatureVersion;
import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import foundation.RDFconverter.xml.FFeatureVersion2XML;

class FFeatureVersion extends FFoundationAbstract{
	
	int dbgLevel = 100;
	
	public FFeatureVersion()	{
		super();
	}
	
	@Override
	protected String getClassUri(){
		return "osp:FeatureState";
	}
	
	@Override
	public MFeatureVersion retrieveByURI(String fversionURI, String graphUri, int lazyDepth)
	{	
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:isVersionOf      ?isVersionOf } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:hasVersion       _:ver         .\n"
				+ "\t\t          		_:ver      hvgi:versionNo     ?versionNo                } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " prv:precededBy        ?precededBy  } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " osp:createdBy         ?createdBy   } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " dcterms:contributor   ?contributor } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:valid            ?valid       .\n"
				+ "\t\t\tOPTIONAL { 	?valid     hvgi:validFrom     _:timeFrom                 .\n"
				+ "\t		        	_:timeFrom time:inXSDDateTime ?validFrom                 }\n"
				+ "\t\t\tOPTIONAL {     ?valid     hvgi:validTo       _:timeTo                   .\n"
				+ "\t			        _:timeTo   time:inXSDDateTime ?validTo		   	         }\n"
				+ "\t\t         }\n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:isDeleted        ?isDeleted   } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " osp:hasTag            ?hasTag      } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " geosparql:hasGeometry _:geom        .\n"
				+ "\t\t\t                 _:geom       geosparql:asWKT    ?wktGeom                } \n";
		
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		queryString += ""
				+ "\t}";	
		
		UDebug.print("Retriving feature version: "+ fversionURI +" \n", dbgLevel);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
	
		return this.setFVAttributes(queryRawResults, fversionURI, graphUri, lazyDepth);
	}
	
	@Override
	public String convertToRDFXML(Object featureVersion) {
		FFeatureVersion2XML fvXML = new FFeatureVersion2XML();
		Document fvDoc = fvXML.convertToRDFXML( (MFeatureVersion) featureVersion );
		
		String fvTriples = this.writeRDFXML(fvDoc);
		return fvTriples;
	}
	
//	public String retrieveFirst()	{
//		return this.retrieveNext(null);
//	}
	
//	public String retrieveFirst(String graphUri)	{
//		return this.retrieveNext(null,graphUri);
//	}
//	
//	public String retrieveNext(String fv_dateFrom)	{
//		return this.retrieveNext(fv_dateFrom, "");
//	}
	
//	public String retrieveNext(String fv_dateFrom,String graphUri)
//	{		
//		String queryString = ""
//				+ "\tSELECT ?uri ?dateFrom\n"
//				+ "\tWHERE \n"
//				+ "\t{ \n";
//				
//		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
//		
//		queryString += ""
//				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
//				+ "\t\tOPTIONAL { ?uri      hvgi:valid           _:valid          . \n"
//				+ "			  _:valid   hvgi:validFrom       _:time            	  . \n"
//				+ "			  _:time    time:inXSDDateTime   ?dateFrom}          	\n";
//		
//		if (fv_dateFrom != null)
//			queryString += "\t\tFILTER( ?dateFrom > \"" + fv_dateFrom + "\"^^xsd:dateTime )  \n";
//		
//		if (!graphUri.equals("")) queryString += "\t }\n";
//		
//		queryString += ""
//				+ "\t}																\n"
//				+ "\tORDER BY ASC(?dateFrom) 										\n"
//				+ "\tLIMIT 1 \n\n"
//				;
//		
//		UDebug.print("Retriving the next feature version wrt date: "+ fv_dateFrom +" \n", dbgLevel);
//		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+1);
//		
//		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
//		
//		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
//		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2 );
//		queryRawResults.reset();
//		
//		QuerySolution generalQueryResults = queryRawResults.next();
//		
//		RDFNode uri = generalQueryResults.getResource("uri");
//		
//		return uri.toString();
//	}
	
	public ArrayList<MFeatureVersion> retrieveNonEndedVersions(String graphUri) {
		ArrayList<MFeatureVersion> versions = new ArrayList<MFeatureVersion>();	
		ArrayList<String> uris = new ArrayList<String>();
		
		uris = this.retrieveNonEndedVersionsUri(graphUri);
		for(String uri : uris)
			versions.add( this.retrieveByURI(uri,graphUri,0) );
		return versions;
	}
	
	private ArrayList<String> retrieveNonEndedVersionsUri(String graphUri) {
		ArrayList<String> uris = new ArrayList<String>();		
		
		String queryString = ""
				+ "SELECT ?fvUri \n"
				+ "WHERE \n"
				+ "{ \n";
				
		if (!graphUri.equals("")) queryString += " GRAPH " +graphUri+ "\n {\n";
		
		queryString += ""
					+ "  ?fvUri hvgi:valid ?valid \n"
					;
		
		if (!graphUri.equals("")) queryString += " }\n";
		queryString += ""				
				+ " FILTER NOT EXISTS {?valid hvgi:validTo ?validTo}    \n"
				+ "}							\n"
				;
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while ( queryRawResults.hasNext() )		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("fvUri");		
			uris.add(uri.toString());
		}
		
		return uris;
	}

	public ArrayList<String> retrieveLivingNeighbours(String fv_dateFrom,String fv_wkt_buffered) {
		return this.retrieveLivingNeighbours_debug(fv_dateFrom, fv_wkt_buffered, "");
	}
	
	public ArrayList<String> retrieveLivingNeighbours_debug(String fv_dateFrom,String fv_wkt_buffered,String graphUri)
	{	
		ArrayList<String> uris = new ArrayList<String>();		
		
		String queryString = ""
				+ "\tSELECT ?uri ?validFrom ?dateTo ?wktString \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				// Join on TEMPORAL subgraph
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid               . \n"
				+ "  			  ?valid     hvgi:validFrom      _:timeFrom           . \n"
				+ "	     		            _:timeFrom time:inXSDDateTime  ?validFrom  . \n"
				+ "  			  OPTIONAL {?valid     hvgi:validTo        _:timeTo   . \n"
				+ "	     		            _:timeTo   time:inXSDDateTime  ?validTo    } \n"				
				+ "\t\t\t} \n"
				// Join on SPATIAL subgraph
				+ "\t\tOPTIONAL { ?uri      geosparql:hasGeometry    _:geom       . \n"
				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n";
				
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		queryString += ""
				+ "\t\t  BIND(if( "
				+ "NOT EXISTS {?valid hvgi:validTo ?timeTo . ?timeTo time:inXSDDateTime ?validTo },\""
				+ UConfig.getMaxDateTimeAsString() +"\"^^xsd:dateTime,?validTo) AS ?dateTo)  \n";
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t?validFrom < \"" + fv_dateFrom + "\"^^xsd:dateTime  &&  			 \n"
				+ "\t\t\t?dateTo > \"" + fv_dateFrom + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				;

//		queryString += ""				
//				+ "\t\tFILTER(                                                               \n"
//				+ "\t\t\t?dateFrom < \"" + fv_dateFrom + "\"^^xsd:dateTime  &&  			 \n"
//				+ "\t\t\t?dateTo > \"" + fv_dateFrom + "\"^^xsd:dateTime    &&               \n"
//				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
//				+ "\t\t\t)                                                                   \n"
//				+ "\t}																         \n"
//				+ "\tORDER BY DESC(?dateFrom) 										         \n"
////				+ "\tLIMIT 10 										                         \n"
//				;
		
		UDebug.print("Retriving features valid at " +fv_dateFrom+" in "+ fv_wkt_buffered +" \n", dbgLevel+1);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("uri");		
			uris.add(uri.toString());
		}
		
		return uris;
	}
	
	public ArrayList<MFeatureVersion> retrieveLivingNeighbours_prod(String fv_dateFrom,String fv_wkt_buffered,String graphUri)
	{	
//		ArrayList<String> uris = new ArrayList<String>();		
		
		String queryString = ""
				+ "\tSELECT ?uri ?dateFrom ?dateTo ?wktString \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";				
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri  hvgi:isVersionOf      ?isVersionOf } \n"
				+ "\t\tOPTIONAL { ?uri  hvgi:hasVersion       _:ver         .\n"
				+ "\t\t           _:ver hvgi:versionNo     ?versionNo      } \n"
				+ "\t\tOPTIONAL { ?uri  prv:precededBy        ?precededBy  } \n"
				+ "\t\tOPTIONAL { ?uri  osp:createdBy         ?createdBy   } \n"
				+ "\t\tOPTIONAL { ?uri  dcterms:contributor   ?contributor } \n"
				+ "\t\tOPTIONAL { ?uri  hvgi:valid            ?valid       .\n"
				+ "\t\t\tOPTIONAL { ?valid     hvgi:validFrom     _:timeFrom .\n"
				+ "\t		        _:timeFrom time:inXSDDateTime ?validFrom }\n"
				+ "\t\t\tOPTIONAL { ?valid     hvgi:validTo       _:timeTo   .\n"
				+ "\t			    _:timeTo   time:inXSDDateTime ?validTo	 }\n"
				+ "\t\t         }\n"
				+ "\t\tOPTIONAL { ?uri hvgi:isDeleted        ?isDeleted    } \n"
				+ "\t\tOPTIONAL { ?uri osp:hasTag            ?hasTag       } \n"
				+ "\t\tOPTIONAL { ?uri geosparql:hasGeometry _:geom        . \n"
				+ "\t\t\t         _:geom       geosparql:asWKT    ?wktGeom } \n";
		
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		queryString += ""
				+ "\t\t  BIND(if( "
				+ "NOT EXISTS {?valid hvgi:validTo ?timeTo . ?timeTo time:inXSDDateTime ?validTo },\""
				+ UConfig.getMaxDateTimeAsString() +"\"^^xsd:dateTime,?validTo) AS ?dateTo)";
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t?validFrom < \"" + fv_dateFrom + "\"^^xsd:dateTime  &&  			 \n"
				+ "\t\t\t?dateTo > \"" + fv_dateFrom + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktGeom, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				;
		
		UDebug.print("Retriving features valid at " +fv_dateFrom+" in "+ fv_wkt_buffered +" \n", dbgLevel+1);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
//		while ( queryRawResults.hasNext() )
//		{
//			QuerySolution generalQueryResults = queryRawResults.next();
//			RDFNode uri = generalQueryResults.getResource("uri");		
//			uris.add(uri.toString());
//		}
		
//		this.setFVAttributes(queryRawResults, fversionURI, graphUri, lazyDepth);
		
		return null;
	}
	
//	public ArrayList<MFeatureVersion> retrieveConfirmers(MFeatureVersion featureVersion, String dateTo, String graphUri, int lazyDepth) {
//		
//		ArrayList<MFeatureVersion> versions = new ArrayList<MFeatureVersion>();
//		ArrayList<String> uris = this.retrieveConfirmersUris(featureVersion, dateTo, graphUri, lazyDepth);
//		
//		for (String uri : uris) 
//			versions.add( this.retrieveByURI(uri, graphUri, 1) );
//		
//		return versions;
//	}
//	
//	public ArrayList<String> retrieveConfirmersUris(MFeatureVersion featureVersion, String dateTo, String graphUri, int lazyDepth) {
//		
//		ArrayList<String> uris = new ArrayList<String>();
//		double radius = 10.0;
//		
//		String fv_dateFrom = featureVersion.getIsValidFromString();
////		String fv_dateTo = featureVersion.getIsValidToString(); //CHECK su fv_dateTo > fv_dateFrom???
//		String fv_wkt_buffered = featureVersion.getGeometryBuffer(radius);
//		
//		String queryString = ""
//				+ "\tSELECT ?uri \n"
//				+ "\tWHERE \n"
//				+ "\t{ \n";
//				
//		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
//		
//		queryString += ""
//				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
//				// Join on TEMPORAL subgraph
//				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid       . \n"
//				+ "  			  ?valid     hvgi:validFrom      _:timeFrom   . \n"
//				+ "	     		  _:timeFrom time:inXSDDateTime  ?dateFrom      \n"		
//				+ "\t\t\t} \n"
//				// Join on SPATIAL subgraph
//				+ "\t\tOPTIONAL { ?uri      geosparql:hasGeometry    _:geom       . \n"
//				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n";
//				
//		if (!graphUri.equals("")) queryString += "\t\t}\n";
//		
//		queryString += ""				
//				+ "\t\tFILTER(                                                               \n"
//				+ "\t\t\t \"" + fv_dateFrom + "\"^^xsd:dateTime < ?dateFrom  &&  			 \n"
//				+ "\t\t\t?dateFrom < \"" + dateTo + "\"^^xsd:dateTime    &&               \n"
//				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
//				+ "\t\t\t)                                                                   \n"
//				+ "\t}																         \n"
//				+ "\tORDER BY DESC(?dateFrom) 										         \n"
//				;
//		
//		UDebug.print("Retriving features valid at " + fv_dateFrom +" in "+ fv_wkt_buffered +" \n", dbgLevel+1);
//		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
//		
//		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
//		
//		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
//		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
//		queryRawResults.reset();
//		
//		while ( queryRawResults.hasNext() )
//		{
//			QuerySolution generalQueryResults = queryRawResults.next();
//			RDFNode uri = generalQueryResults.getResource("uri");		
//			uris.add(uri.toString());
//		}
//		
//		return uris;
//	}
	
	@SuppressWarnings("unchecked")
	private MFeatureVersion setFVAttributes(ResultSetRewindable queryRawResults, String fversionURI, String graphUri, int lazyDepth)
	{

		FTag ftag = new FTag();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		MFeatureVersion fversion = this.setFVAttributes(generalQueryResults, fversionURI, graphUri, lazyDepth);
		
		queryRawResults.reset();

		for (int i = 0; i < queryRawResults.size(); i++ )
		{
			QuerySolution generalQuery = queryRawResults.next();
			
			if ( generalQuery.getResource("hasTag") != null )
			{
				String tagUri = generalQuery.getResource("hasTag").toString();
				Entry<String, String> tag = (Entry<String, String>) ftag.retrieveByURI(tagUri, graphUri, 0);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					fversion.addTag(tag.getKey(), tag.getValue());
			}
		}
		
		return fversion;
	}
	
	public MFeatureVersion setFVAttributes(QuerySolution generalQueryResults, String fversionURI, String graphUri, int lazyDepth) {
		
		MFeatureVersion fversion = new MFeatureVersion();
		FEdit fedit = new FEdit();
		FAuthor fauthor = new FAuthor();
		FFeature ffeature = new FFeature();
		
		RDFNode isVersionOf = generalQueryResults.getResource("isVersionOf");
		RDFNode versionNo   = generalQueryResults.getLiteral("versionNo");
		RDFNode precededBy  = generalQueryResults.getResource("precededBy");
		RDFNode createdBy   = generalQueryResults.getResource("createdBy");
		RDFNode hasAuthor   = generalQueryResults.getResource("contributor");
		RDFNode validFrom   = generalQueryResults.getLiteral("validFrom");
		RDFNode validTo     = generalQueryResults.getLiteral("validTo");
		RDFNode isDeleted   = generalQueryResults.getLiteral("isDeleted");
		RDFNode wktGeom     = generalQueryResults.getLiteral("wktGeom");
		
		fversion.setUri( fversionURI );
		
		if (isVersionOf != null)  {
			fversion.setFeatureUri(isVersionOf.toString());
			if ( lazyDepth > 0 ) {
				MFeature feature = ffeature.retrieveByURI( isVersionOf.toString(), graphUri, lazyDepth-1); 
				fversion.setFeature(feature);
			}
		}
		
		if (versionNo != null)   fversion.setVersionNo(versionNo.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""));
		if (precededBy != null)  {
			fversion.setPrevFVersionUri( precededBy.toString() );
			if ( lazyDepth > 0 ) {
				MFeatureVersion precededfv = this.retrieveByURI( precededBy.toString(), graphUri, lazyDepth-1); 
				fversion.setPrevFVersion( precededfv );
			}
		}
		if (createdBy != null) {
			fversion.setEditUri(createdBy.toString());
			if ( lazyDepth > 0 ) {
				MEdit edit = fedit.retrieveByURI(createdBy.toString(), graphUri, lazyDepth-1); 
				fversion.setEdit( edit );
			}
		}
		if (hasAuthor != null) {
			fversion.setAuthorUri(hasAuthor.toString());
			if ( lazyDepth > 0 ) {
				MAuthor author = fauthor.retrieveByURI(hasAuthor.toString(), graphUri, lazyDepth-1); 
				fversion.setAuthor( author );
			}
		}
		if (validFrom != null)   fversion.setIsValidFrom(validFrom.toString());
		if (validTo != null)     fversion.setIsValidTo(validTo.toString());
		if (isDeleted != null)   fversion.setIsDeleted( Boolean.parseBoolean(isDeleted.toString()) );
		
		if (wktGeom != null) { 
			fversion.setWktGeometry( wktGeom.toString().replace("^^http://www.opengis.net/ont/sf#wktLiteral", "") );			
		 	fversion.setGeometry( wktGeom.toString().replace("^^http://www.opengis.net/ont/sf#wktLiteral","") );
		}
		
		return fversion;
		
	}
	
	public ArrayList<String> retrieveDateList(String graphUri)
	{	
		ArrayList<String> dates = new ArrayList<String>();
		String queryString = ""
				+ "\tSELECT DISTINCT ?dateFrom\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		
		queryString += ""
				+ "\t\t?uri      rdf:type             osp:FeatureState . \n"
				+ "\t\t?uri      hvgi:valid           _:valid          . \n"
				+ "\t\t_:valid   hvgi:validFrom       _:time           . \n"
				+ "\t\t_:time    time:inXSDDateTime   ?dateFrom          \n";
		
		if (!graphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""
				+ "\t}																\n"
				+ "\tORDER BY ASC(?dateFrom) 										\n"
				;
		
		UDebug.print("Retriving date List \n", dbgLevel);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1 );
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		while (queryRawResults.hasNext()){
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode date = generalQueryResults.getLiteral("dateFrom");
			dates.add(date.toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", ""));
		}

		return dates;
	}
	
	public ArrayList<String> retrieveURIByDate(String dateFrom, String graphUri)
	{	
		ArrayList<String> uris = new ArrayList<String>();
		String queryString = ""
				+ "\tSELECT ?uri\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		
		queryString += ""
				+ "\t\t ?uri      rdf:type             osp:FeatureState                  . \n"
				+ "\t\t ?uri      hvgi:valid           _:valid                           . \n"
				+ "\t\t _:valid   hvgi:validFrom       _:time                            . \n"
				+ "\t\t _:time    time:inXSDDateTime   \""+ dateFrom +"\"^^xsd:dateTime    \n";
		
		if (!graphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""
				+ "\t}																\n"
				+ "\tORDER BY ASC(?dateFrom) 										\n"
				;
		
		UDebug.print("Retriving features versions start in date: "+ dateFrom +" \n", dbgLevel+1);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while (queryRawResults.hasNext()){
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("uri");
			uris.add(uri.toString());
		}

		return uris;
	}
	
	public ArrayList<MFeatureVersion> retrieveUrisByDate(String dateFrom, String graphUri, int lazyDepth) {	
		ArrayList<MFeatureVersion> fvs = new ArrayList<MFeatureVersion>();
		ArrayList<String> uris = new ArrayList<String>();
		uris = this.retrieveURIByDate(dateFrom, graphUri);
		
		for (String fversionURI : uris)
			fvs.add( this.retrieveByURI(fversionURI, graphUri, lazyDepth) );
		
		return fvs;
	}
	
	public ArrayList<MFeatureVersion> retrieveFVSByDate(String dateFrom, String graphUri, int lazyDepth) {	
		ArrayList<MFeatureVersion> fvs = new ArrayList<MFeatureVersion>();
		Map <String, MFeatureVersion> fvsMap = new HashMap<String, MFeatureVersion>();

		String queryString = ""
				+ "\tSELECT DISTINCT ?uri ?isVersionOf ?versionNo ?precededBy ?createdBy ?contributor ?validTo ?isDeleted ?tagKey ?tagValue ?wktGeom \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n {\n";
		
		queryString += ""
				+ "\t\t?uri   rdf:type       osp:FeatureState .\n"
				+ "\t\t?uri   hvgi:valid     ?valid           . \n"
				+ "\t\t?valid hvgi:validFrom _:timeFrom       . \n"
				+ "\t\t_:timeFrom    time:inXSDDateTime   \""+ dateFrom +"\"^^xsd:dateTime .\n"
				
				+ "\t\tOPTIONAL { ?uri   hvgi:isVersionOf      ?isVersionOf } \n"
				+ "\t\tOPTIONAL { ?uri   hvgi:hasVersion       _:ver        . \n"
				+ "\t\t           _:ver      hvgi:versionNo     ?versionNo  } \n"
				+ "\t\tOPTIONAL { ?uri   prv:precededBy        ?precededBy  } \n"
				+ "\t\tOPTIONAL { ?uri   osp:createdBy         ?createdBy   } \n"
				+ "\t\tOPTIONAL { ?uri   dcterms:contributor   ?contributor } \n"
				+ "\t\tOPTIONAL { ?valid     hvgi:validTo       _:timeTo    . \n"
				+ "\t\t			  _:timeTo   time:inXSDDateTime ?validTo	} \n"
				+ "\t\tOPTIONAL { ?uri   hvgi:isDeleted        ?isDeleted   } \n"
				+ "\t\tOPTIONAL { ?uri   geosparql:hasGeometry _:geom       . \n"
				+ "\t\t           _:geom       geosparql:asWKT    ?wktGeom  } \n"
				+ "\t\tOPTIONAL { ?uri  osp:hasTag    ?tag       . \n"
				+ "\t\t           ?tag  osp:hasKey    ?key       . \n"
				+ "\t\t           ?tag  osp:hasValue  ?val       . \n"
				+ "\t\t           ?key  osp:isKey     ?tagKey    . \n"
				+ "\t\t           ?val  osp:isValue   ?tagValue  } \n"
				;
		
		if (!graphUri.equals("")) queryString += "\t\t }\n";
		
		queryString += ""
				+ "\t}\n"
				+ "ORDER BY ?uri";	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		while (queryRawResults.hasNext()){
			QuerySolution generalQueryResults = queryRawResults.next();
			
			RDFNode uriNode  = generalQueryResults.getResource("uri");
			RDFNode tagKey   = generalQueryResults.getLiteral("tagKey");
			RDFNode tagValue = generalQueryResults.getLiteral("tagValue");
			
			String uri = uriNode.toString();
			
			// if is the first fv occurrence create the object and add it to the map  
			MFeatureVersion fversion = fvsMap.get(uri);
			if (fversion == null) {
				fversion = this.setFVAttributes(generalQueryResults, uri, graphUri, lazyDepth);
				fversion.setIsValidFrom(dateFrom);
				fvsMap.put(uri, fversion);
			}
			
			//add tags
			if  (tagKey != null && tagValue != null)
				fversion.addTag(tagKey.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""), 
							tagValue.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""));
		}
		
		for ( Entry<String, MFeatureVersion> fvEntry : fvsMap.entrySet()) 
			fvs.add(fvEntry.getValue());
		
		return fvs;
	}
	
}
