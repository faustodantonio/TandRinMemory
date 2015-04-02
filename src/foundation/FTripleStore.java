package foundation;

import java.io.IOException;
import java.util.Map.Entry;

import org.jdom2.Namespace;

import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public abstract class FTripleStore {
	
	private int dbgLevel = 100;

	public FTripleStore() {	}
	
	public abstract ResultSet sparqlSelect(String selectQueryString) throws IOException;
	public abstract boolean sparqlUpdate(String updateQueryString) throws IOException;
	
	public abstract ResultSet sparqlSelectHandled(String selectQueryString);
	public abstract boolean sparqlUpdateHandled(String updateQueryString);
	
	public abstract boolean jenaModelInsert(Model jenaModel, String namedGraph);
	public abstract boolean enableGraphIndexes(String graphName,String namespaceUri);
	public abstract boolean graphExists(String graphName, String namespaceUri);
	
	protected String AddPrefixes(String queryString) {		
		
//		String prefixes = "\n"
//				+ "PREFIX rdf: "       + "\t\t" +"<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"     + "\n"
//				+ "PREFIX xsd: "       + "\t\t" +"<http://www.w3.org/2001/XMLSchema#>"               + "\n"
//				+ "PREFIX graphs: "    + "\t\t" +"<http://parliament.semwebcentral.org/parliament#>" + "\n"
//				+ "PREFIX dcterms: "   + "\t"   +"<http://purl.org/dc/terms/>" 						 + "\n"
//		  		+ "PREFIX foaf: "      + "\t\t" +"<http://xmlns.com/foaf/0.1/>" 					 + "\n"
//		  		+ "PREFIX geosparql: " + "\t"   +"<http://www.opengis.net/ont/geosparql#>" 			 + "\n"
//		  		+ "PREFIX geof: "      + "\t\t" +"<http://www.opengis.net/def/function/geosparql/>"  + "\n"
//		  		+ "PREFIX sf: "        + "\t\t" +"<http://www.opengis.net/ont/sf#>" 				 + "\n"
//		  		+ "PREFIX units: "     + "\t\t" +"<http://www.opengis.net/def/uom/OGC/1.0/>" 		 + "\n"		  		
//		  		+ "PREFIX hvgi: "      + "\t\t" +"<http://semantic.web/vocabs/history_vgi/hvgi#>" 	 + "\n"
//		  		+ "PREFIX osp: "       + "\t\t" +"<http://semantic.web/vocabs/osm_provenance/osp#> " + "\n"
//		  		+ "PREFIX prv: "       + "\t\t" +"<http://purl.org/net/provenance/ns#>" 			 + "\n"
//		  		+ "PREFIX time: "      + "\t\t" +"<http://www.w3.org/2006/time#>" 			  + "\n" + "\n"
//		  		+ "";
		
		String prefixes = "\n";
		
		for (Entry<String, Namespace> namespace : UConfig.namespaces.entrySet()) {
			prefixes += "PREFIX " + namespace.getKey() + ": \t";
			if(namespace.getKey().length() < 7) prefixes +=  "\t";
			prefixes +=  " <" + namespace.getValue().getURI() +">\n" ;
		}
		
		UDebug.print(prefixes, dbgLevel+1);
		
		return prefixes + "\n" + queryString;
		
//PREFIX rdf: 		<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//PREFIX xsd: 		<http://www.w3.org/2001/XMLSchema#>
//PREFIX dcterms: 	<http://purl.org/dc/terms/>
//PREFIX foaf: 		<http://xmlns.com/foaf/0.1/>
//PREFIX geosparql: <http://www.opengis.net/ont/geosparql#>
//PREFIX geof: 		<http://www.opengis.net/def/function/geosparql/>
//PREFIX sf: 		<http://www.opengis.net/ont/sf#>
//PREFIX units: 	<http://www.opengis.net/def/uom/OGC/1.0/>
//PREFIX hvgi: 		<http://semantic.web/vocabs/history_vgi/hvgi#>
//PREFIX osp: 		<http://semantic.web/vocabs/osm_provenance/osp#> 
//PREFIX prv: 		<http://purl.org/net/provenance/ns#>
//PREFIX time: 		<http://www.w3.org/2006/time#>

		
	}

}
