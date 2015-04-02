package foundation;

import java.io.IOException;

import org.apache.jena.atlas.logging.LogCtl;

import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Model;
import com.bbn.parliament.jena.joseki.client.RemoteModel;

public class FParliament extends FTripleStore{
	
	private int dbgLevel = 100;

	private RemoteModel parliament;
	
	private String sparqlEndpointUrl = UConfig.datasetSPARQLQueryURI_Parliment;
	private String bulkEndpointUrl = UConfig.datasetBULKQueryURI_Parliment;
	
	public FParliament() { 
		
		parliament = new RemoteModel(sparqlEndpointUrl, bulkEndpointUrl);
	}
	
	@Override
	public ResultSet sparqlSelect(String selectQueryString) throws IOException
	{
		LogCtl.setCmdLogging();
		selectQueryString = this.AddPrefixes(selectQueryString);
		
		ResultSet rs = null;
		rs = parliament.selectQuery(selectQueryString);
	      
	    return rs;
	}
	
	@Override
	public boolean sparqlUpdate(String updateQueryString) throws IOException {
		LogCtl.setCmdLogging();
		boolean execFine = true;
		
		updateQueryString = this.AddPrefixes(updateQueryString);
		parliament.updateQuery(updateQueryString);

		return execFine;
	}
	
	public boolean sparqlUpdateHandled(String updateQueryString) {
		boolean execFine = true;
		
		try {
			this.sparqlUpdate(updateQueryString);
		} catch (IOException e) {
			execFine = false;
			e.printStackTrace();
		}

		return execFine;
	}
	@Override
	public ResultSet sparqlSelectHandled(String selectQueryString) {
		
		ResultSet rs = null;
		
		try {
			rs = this.sparqlSelect(selectQueryString);
		} catch (IOException e) {
			rs = null;
			e.printStackTrace();
		}

		return rs;
	}

	@Override
	public boolean jenaModelInsert(Model jenaModel, String namedGraph) {
		
		boolean result;
		
		try {
			if (namedGraph.equals(""))
				parliament.insertStatements(jenaModel);
			else 
				parliament.insertStatements(jenaModel, namedGraph);
			
			result = true;
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public boolean enableGraphIndexes(String graphName,String namespaceUri){
		Boolean result;
		String createIndexesQueryString = ""
				+ "INSERT {} WHERE { " + "\n";
		if (!graphName.equals("") && !namespaceUri.equals(""))
			createIndexesQueryString = createIndexesQueryString
						+ "\t" + "<" + namespaceUri + graphName + ">" + "\n";
		else
			createIndexesQueryString = createIndexesQueryString
						+ "\t" + "urn:x-arq:DefaultGraph" + "\n";
		createIndexesQueryString = createIndexesQueryString
					+ "\t" + "<http://parliament.semwebcentral.org/pfunction#enableIndexing> " + "\n"
					+ "\t" + "\"true\"^^<http://www.w3.org/2001/XMLSchema#boolean> " + "\n"
				+ "}";
		
		UDebug.print("\nSPARQL query: \n" + createIndexesQueryString + "\n\n", dbgLevel+1);
		
		try {
			result = this.sparqlUpdate(createIndexesQueryString);
		} catch (IOException e) {
			
			UDebug.print(e.getMessage(), dbgLevel+2);
			result = false;
			e.printStackTrace();
		}		
		
		return result;		
	}
	
	@Override
	public boolean graphExists(String graphName, String namespaceUri) { 
		
		Boolean result = false;
		String grpahsQueryString = "SELECT DISTINCT ?graphUri "
				+ "WHERE {   "
				+ "\tGRAPH <http://parliament.semwebcentral.org/parliament#MasterGraph> "
				+ "\t\t{?graphUri ?p ?o}"
				+ "}";
		
		UDebug.print("\n\tSPARQL query: \n" + grpahsQueryString + "\n\n", dbgLevel+1);
		ResultSet rawResults;
		int count = 0;
		
		try {
			
			rawResults = this.sparqlSelect(grpahsQueryString);
			ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
			UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
			queryRawResults.reset();
			
			while ( count < queryRawResults.size() && result.equals(false) )
			{
				QuerySolution generalQueryResults = queryRawResults.next();
				if (generalQueryResults.getResource("graphUri").toString().equals(namespaceUri+graphName)) 
					result = true;
				UDebug.print(generalQueryResults.toString()+ (count+1) +"/"+ queryRawResults.size() +"\n", dbgLevel+3);
				count++;
			}
			
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
	}
	
}
