package foundation;


import org.jdom2.Document;

import model.MFeature;
import model.MFeatureVersion;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import foundation.RDFconverter.xml.FFeature2XML;

public class FFeature extends FFoundationAbstract{
	
	int dbgLevel = 100;
	
	public FFeature()
	{
		super();
	}
	@Override
	protected String getClassUri(){
		return"hvgi:VGIFeature";
	}
	
	@Override
	public String convertToRDFXML(Object feature) {
		
		FFeature2XML featureXML = new FFeature2XML();
		Document featureDoc = featureXML.convertToRDFXML( (MFeature) feature );
		
		String featureTriples = this.writeRDFXML(featureDoc);
		return featureTriples;
	}
	
	@Override
	public MFeature retrieveByURI(String featureURI, String graphUri, int lazyDepth)
	{
		return this.retrieveByURI_debug_(featureURI, graphUri, lazyDepth);
	}
	
	public MFeature retrieveByURI_debug_(String featureURI, String graphUri, int lazyDepth)
	{
		MFeature feature = new MFeature();
		FFeatureVersion ffversion = new FFeatureVersion();
		
		String queryString = ""
				+ "\tSELECT ?versionuri ?versionNo \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\t <"+featureURI+">" + " hvgi:hasVersion   ?versionuri .\n"
				+ "\t\t ?versionuri           hvgi:hasVersion   _:version   .\n"
				+ "\t\t _:version             hvgi:versionNo    ?versionNo  .\n";
		
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		feature.setUri( featureURI );
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode fversionuri = generalQueryResults.getResource("versionuri");
			RDFNode versionNo = generalQueryResults.getLiteral("versionNo");
			MFeatureVersion fversion = null;
			
			if ( lazyDepth > 0 )
				fversion = ffversion.retrieveByURI(fversionuri.toString(), graphUri, lazyDepth-1);
			
			feature.addVersion(fversionuri.toString(), versionNo.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""), fversion);
		}
		
		return feature;
	}
	
	public MFeature retrieveByURI_prod_(String featureURI, String graphUri, int lazyDepth)
	{
		MFeature feature = new MFeature();
		FFeatureVersion ffversion = new FFeatureVersion();
		
		String queryString = ""
				+ "\tSELECT ?versionuri ?versionNo \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\t <"+featureURI+">" + " hvgi:hasVersion   ?versionuri .\n"
				+ "\t\t ?versionuri           hvgi:hasVersion   _:version   .\n"
				+ "\t\t _:version             hvgi:versionNo    ?versionNo  .\n";
		
		
		
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		feature.setUri( featureURI );
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode fversionuri = generalQueryResults.getResource("versionuri");
			RDFNode versionNo = generalQueryResults.getLiteral("versionNo");
			MFeatureVersion fversion = null;
			
			if ( lazyDepth > 0 )
				fversion = ffversion.setFVAttributes(generalQueryResults, fversionuri.toString(), graphUri, lazyDepth);
			
			feature.addVersion(fversionuri.toString(), versionNo.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""), fversion);
		}
		
		return feature;
	}	
}
