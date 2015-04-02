package foundation;

import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;

import utility.UConfig;
import utility.UDebug;

public class FInstallation {

	int dbgLevel = 100;
	
	protected FTripleStore triplestore;
	
	public FInstallation() {
		try {
			this.triplestore = (FTripleStore) Class.forName("foundation."+UConfig.triplestoreConnectionClass).newInstance();
		} catch (ClassNotFoundException e) {
			UDebug.error("triplestoreConnectionClass NOT FOUND. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded");
			this.triplestore = new FParliament();
		} catch (InstantiationException e) {
			UDebug.error("Cannot instantiate triplestoreConnectionClass. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded");
			this.triplestore = new FParliament();
		} catch (IllegalAccessException e) {
			UDebug.error("Cannot access to triplestoreConnectionClass. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded");
		}		
	}

	boolean importJenaModel(Model jenaModel, String graphName){
		return triplestore.jenaModelInsert(jenaModel, graphName);
	}
	
	boolean createGraph(String graphName,String namespace){
		Boolean result;
		String createGraphQueryString = "CREATE GRAPH ";
		if (!namespace.equals(""))
			createGraphQueryString = createGraphQueryString + namespace + ":";
		createGraphQueryString = createGraphQueryString + graphName;
		
		UDebug.print("SPARQL query: \n" + createGraphQueryString + "\n\n", dbgLevel+2);
		
		try {
			result = this.triplestore.sparqlUpdate(createGraphQueryString);
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	
	boolean deleteGraph(String graphName,String namespace){
		Boolean result;
		String deleteGraphQueryString = "DROP GRAPH ";
		if (!namespace.equals(""))
			deleteGraphQueryString = deleteGraphQueryString + namespace + ":";
		deleteGraphQueryString = deleteGraphQueryString + graphName;
		
		UDebug.print("SPARQL query: \n" + deleteGraphQueryString + "\n\n", dbgLevel+2);
		
		try {
			result = this.triplestore.sparqlUpdate(deleteGraphQueryString);
		} catch (IOException e) {
			
			UDebug.error(e.getMessage());
			result = false;
			e.printStackTrace();
		}		
		
		return result;		
	}
	
	boolean enableGraphIndexes(String graphName,String namespaceUri){
		return this.triplestore.enableGraphIndexes(graphName, namespaceUri);		
	}
	
	boolean graphExists(String graphName, String namespaceUri) { 
		return this.triplestore.graphExists(graphName, namespaceUri);
	}
	
}
