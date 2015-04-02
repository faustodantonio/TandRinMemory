package foundation;

import java.util.ArrayList;

import org.jdom2.Document;

import utility.UConfig;
import utility.UDebug;
import model.MAuthor;
import model.MFeatureVersion;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import foundation.RDFconverter.xml.FAuthor2XML;

public class FAuthor extends FFoundationAbstract{
	
	int dbgLevel = 100;
	
	public FAuthor()
	{
		super();
	}
	@Override
	protected String getClassUri(){
		return "osp:User";
	}
	
	@Override
	public MAuthor retrieveByURI(String authorURI, String graphUri, int lazyDepth)
	{
		MAuthor author = new MAuthor();
		
		String queryString = ""
				+ "\tSELECT ?accountName ?accountServerHomepage \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\t<"+authorURI+">" + " ?p ?o .\n"
				+ "\t\t?o foaf:accountName ?accountName . \n"
				+ "\t\t?o foaf:accountServerHomepage ?accountServerHomepage . \n";
				
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t} \n";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		if (queryRawResults.hasNext())
		{
			QuerySolution queryResults = queryRawResults.next();
			
			RDFNode accountName = queryResults.getLiteral("accountName");
			RDFNode accountServerHomepage = queryResults.getLiteral("accountServerHomepage");
			
			author.setUri( authorURI );
			author.setAccountName(accountName.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""));
			author.setAccountServerHomepage(accountServerHomepage.toString());
		}
		return author;
	}


	public ArrayList<String> retrieveFeatreVersionsByURI(String authorURI, String graphUri, int lazyDepth)
	{
		ArrayList<String> fvs = new ArrayList<String>();
		
		String queryString = ""
				+ "\tSELECT ?fvUri \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\t?fvUri dcterms:contributor <"+authorURI+"> .\n"
				;
				
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t} \n";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		while (queryRawResults.hasNext()) {
			QuerySolution queryResults = queryRawResults.next();
			
			RDFNode fvUri = queryResults.getLiteral("fvUri");
			fvs.add(fvUri.toString());
		}
		
		return fvs;
	}

	public ArrayList<MAuthor> retrieveConfirmers(MFeatureVersion featureVersion, String dateTo, String graphUri, int lazyDepth) {
		
		ArrayList<MAuthor> versions = new ArrayList<MAuthor>();
		ArrayList<String> uris = this.retrieveConfirmersUris(featureVersion, dateTo, graphUri);
		
		for (String uri : uris) 
			versions.add( this.retrieveByURI(uri, graphUri, lazyDepth) );
		
		return versions;
	}
	
	public ArrayList<String> retrieveConfirmersUris(MFeatureVersion featureVersion, String dateTo, String graphUri) {
		
		ArrayList<String> uris = new ArrayList<String>();
		double radius = UConfig.featureInfluenceRadius;
		
		String fv_dateFrom = featureVersion.getIsValidFromString();
//		String fv_dateTo = featureVersion.getIsValidToString(); //CHECK su fv_dateTo > fv_dateFrom???
		String fv_wkt_buffered = featureVersion.getGeometryBuffer(radius);
		
		String queryString = ""
				+ "\tSELECT DISTINCT ?authorUri \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				+ "\t\tOPTIONAL { ?uri      dcterms:contributor  ?authorUri       } \n"
				// Join on TEMPORAL subgraph
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid       . \n"
				+ "  			  ?valid     hvgi:validFrom      _:timeFrom   . \n"
				+ "	     		  _:timeFrom time:inXSDDateTime  ?dateFrom      \n"		
				+ "\t\t\t} \n"
				// Join on SPATIAL subgraph
				+ "\t\tOPTIONAL { ?uri      geosparql:hasGeometry    _:geom       . \n"
				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n";
				
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t \"" + fv_dateFrom + "\"^^xsd:dateTime < ?dateFrom  &&  			 \n"
				+ "\t\t\t?dateFrom < \"" + dateTo + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				+ "\tORDER BY DESC(?dateFrom) 										         \n"
				;
		
		UDebug.print("Retriving features valid at " + fv_dateFrom +" in "+ fv_wkt_buffered +" \n", dbgLevel+1);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("authorUri");		
			uris.add(uri.toString());
		}
		
		return uris;
	}
	
	
	@Override
	public String convertToRDFXML(Object author) {
				
		FAuthor2XML authorXML = new FAuthor2XML();
		Document authorDoc = authorXML.convertToRDFXML( (MAuthor) author );
		
		String authorTriples = this.writeRDFXML(authorDoc);
		return authorTriples;
	}
	
}
