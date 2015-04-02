package foundation;

import org.apache.jena.atlas.logging.LogCtl;

import utility.UConfig;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class FFuseki extends FTripleStore{

	private String datasetQueryURI = UConfig.datasetQueryURI_Fuseki;
	
	@Override
	public boolean sparqlUpdate(String updateQueryString) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public ResultSet sparqlSelect(String queryString)
	{
		LogCtl.setCmdLogging();
//		LogCtl.setLog4j("../utility/log4j.properties");
		
		queryString = this.AddPrefixes(queryString);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(datasetQueryURI, query);

		ResultSet rs = qexec.execSelect() ;
	      
	    return rs;
	}

	@Override
	public boolean jenaModelInsert(Model jenaModel, String namedGraph) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultSet sparqlSelectHandled(String selectQueryString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sparqlUpdateHandled(String updateQueryString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean enableGraphIndexes(String graphName, String namespaceUri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean graphExists(String graphName, String namespaceUri) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
