package foundation;

import java.util.Map.Entry;

import model.MEdit;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class FEdit extends FFoundationAbstract {
	
	int dbgLevel = 100;
	
	public FEdit()
	{
		super();
	}

	@Override
	protected String getClassUri(){
		return "osp:Edit";
	}
	@Override
	public MEdit retrieveByURI(String editURI, String graphUri, int lazyDepth)
	{
		MEdit edit = new MEdit();
		
		String queryString = ""
				+ "\tSELECT ?author ?changesGeometry ?addtag ?changevalueofkey ?removetag \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { <"+editURI+">" + " prv:performedBy ?author } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:changesGeometry ?changesGeometry } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:addTags ?addtag } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:changesValuesOfKey ?changevalueofkey } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:removeTags ?removetag } \n";
		
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		RDFNode changesGeometry = generalQueryResults.getLiteral("changesGeometry");
		RDFNode performedBy = generalQueryResults.getResource("author");
		
		edit.setUri( editURI );
		if (changesGeometry != null)
			edit.setChangesGeometry( Boolean.parseBoolean(changesGeometry.toString().replace("^^http://www.w3.org/2001/XMLSchema#boolean", "")) );
		if (performedBy != null)
			edit.setAuthorUri(performedBy.toString());
		
		FAuthor fauthor = new FAuthor();
		
		if ( lazyDepth > 0 && performedBy != null)
			edit.setAuthor(fauthor.retrieveByURI(performedBy.toString(), graphUri, lazyDepth-1));
		
		this.retriveEditTags(edit, graphUri, queryRawResults);
		
		return edit;
	}	

	@SuppressWarnings("unchecked")
	private void retriveEditTags(MEdit edit, String graphUri, ResultSetRewindable rawResults) {
		
		FTag ftag = new FTag();
		rawResults.reset();
		ResultSet queryRawResults = ResultSetFactory.copyResults(rawResults);

		for (int i = 0; i < rawResults.size(); i++ )
		{
			QuerySolution generalQuery = queryRawResults.next();
			
			if ( generalQuery.getResource("addtag") != null )
			{
				String tagUri = generalQuery.getResource("addtag").toString();
				Entry<String, String> tag = (Entry<String, String>) ftag.retrieveByURI(tagUri, graphUri, 0);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					edit.addAddedTag(tag.getKey(), tag.getValue());
			}
			if ( generalQuery.getResource("changevalueofkey") != null )
			{
				String tagUri = generalQuery.getResource("changevalueofkey").toString();
				Entry<String, String> tag = (Entry<String, String>) ftag.retrieveByURI(tagUri, graphUri, 0);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					edit.addChangedTag(tag.getKey(), tag.getValue());
			}
			if ( generalQuery.getResource("removetag") != null )
			{
				String tagUri = generalQuery.getResource("removetag").toString();
				Entry<String, String> tag = (Entry<String, String>) ftag.retrieveByURI(tagUri, graphUri, 0);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					edit.addRemovedTag(tag.getKey(), tag.getValue());
			}
		}
		
	}

	@Override
	public String convertToRDFXML(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
}
