package foundation;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public abstract class FFoundationAbstract {

	protected FTripleStore triplestore;
	protected String classUri;
	
	protected FFoundationAbstract() 
	{	
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
		
		this.classUri = this.getClassUri();
	}
	
	/*************************
	 * 
	 * Abstract FUNCTIONS
	 *
	 *************************/	
	
	public    abstract Object retrieveByURI(String uri, String graphUri, int lazyDepth);
	public    abstract String convertToRDFXML(Object obj);
	protected abstract String getClassUri();
	
	/*************************
	 * 
	 * Retrive FUNCTIONS
	 *
	 *************************/
	
	public Object retrieveByURI(String uri){
		return this.retrieveByURI(uri, 0);
	};
	
	public Object retrieveByURI(String uri, int lazyDepth){
		return this.retrieveByURI(uri, "", 0);
	};
	
	/*************************
	 * 
	 * Retrive List FUNCTIONS
	 *
	 *************************/	
	
	@Deprecated
	public ResultSet retrieveAllAsResultSet()
	{
		return this.getURIsOfClassAsResultSet(this.classUri);
	}
	
	public ArrayList<String> retrieveAll()	{
		return this.getURIsOfClass(this.classUri);
	}

	public ArrayList<String> retrieveAll(String graphUri) {
		return this.getURIsOfClass(this.classUri,graphUri);
	}
	
	ResultSet getURIsOfClassAsResultSet( String vocabClass ) {
		return this.getURIsOfClassAsResultSet(vocabClass, "");
	}
	
	public ArrayList<String> getURIsOfClass( String vocabClass )	{
		return this.getURIsOfClass(vocabClass, "");
	}
	
	ResultSet getURIsOfClassAsResultSet( String vocabClass, String graphUri )	{
		String queryString;
		if (graphUri.equals("")) 
			queryString = "SELECT ?uri { ?uri rdf:type "+ vocabClass +" }";
		else 
			queryString = "SELECT ?uri { GRAPH "+ graphUri +" {?uri rdf:type "+ vocabClass +" }}"; 
		
		return triplestore.sparqlSelectHandled(queryString);
	}
	
	public ArrayList<String> getURIsOfClass( String vocabClass, String graphUri )
	{
		ArrayList<String> uris = new ArrayList<String>();
		
		ResultSet queryRawResults = getURIsOfClassAsResultSet( vocabClass, graphUri );
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode classuri = generalQueryResults.getResource("uri");
			uris.add(classuri.toString());
		}
		
		return uris;
	}
	
	/*************************
	 * 
	 * Create FUNCTIONS
	 *
	 *************************/	
	
	private boolean create(String rdfTriples, String graphUri) {
		String updateQueryString;
		if (graphUri.equals(""))
			updateQueryString = "INSERT DATA { "+ rdfTriples +" }";
		else
			updateQueryString = "INSERT DATA { GRAPH "+ graphUri +" {"+ rdfTriples +" } }";
		
		UDebug.print("\n\n Insertion Query : \n" + updateQueryString,10);
		
		boolean result = this.triplestore.sparqlUpdateHandled(updateQueryString);
		
		return result;
	}
	
	boolean create(Object modelObj) {
		return this.create(modelObj, "");
	}
	
	boolean create(Object modelObj, String graphUri) 	{
		boolean creation = false;
		if (this.checkObjectModel(modelObj)) {
			String rdfTriples = this.convertToRDFTTL(modelObj,false);
			creation = this.create(rdfTriples,graphUri);
		}
		// TODO: RAISE EXCEPTION at create(), the object to be "created" is not a Model object.
		return creation;
	}
	
	/*************************
	 * 
	 * Update FUNCTIONS
	 *
	 *************************/	

	private boolean update(String oldRdfTriples, String updatedRdfTriples, String graphUri) {
		boolean result = false;
		if ( this.delete(oldRdfTriples,graphUri) )
			result = this.create(updatedRdfTriples,graphUri);
		
		return result;
	}
	
	boolean update(Object oldObj, Object updObj) {
		return this.update(oldObj, updObj, "");
	}
	
	boolean update(Object oldObj, Object updObj, String graphUri) {
		boolean update = false;
		if ( this.checkObjectModel(oldObj) && this.checkObjectModel(updObj) ) {
			String oldRdfTriples =  this.convertToRDFTTL(oldObj);
			String updatedRdfTriples = this.convertToRDFTTL(updObj);
			update = this.update(oldRdfTriples, updatedRdfTriples, graphUri);
		}
		// TODO: RAISE EXCEPTION at update(), the object to be "updated" is not a Model object.
		return update;
	}
	
	/*************************
	 * 
	 * Delete FUNCTIONS
	 *
	 *************************/	
	
	private boolean delete(String rdfTriples, String graphUri) {
		String updateQueryString;
		
		if (graphUri.equals(""))
			updateQueryString = "DELETE DATA { "+ rdfTriples +" }";
		else updateQueryString = "DELETE DATA { GRAPH "+ graphUri +" {"+ rdfTriples +"} }";
		
		boolean result = this.triplestore.sparqlUpdateHandled(updateQueryString);
		return result;
	}
	
	boolean delete(Object modelObj) {
		return this.delete(modelObj, "");
	}
	
	boolean delete(Object modelObj, String graphUri) {
		boolean deletion = false;
		if (this.checkObjectModel(modelObj)) {
			String rdfTriples = this.convertToRDFTTL(modelObj);
			deletion = this.delete(rdfTriples, graphUri);
		}
		// TODO: RAISE EXCEPTION at delete(), the object to be "deleted" is not a Model object.
		return deletion;
	}
	
	/*************************
	 * 
	 * RDF format convert FUNCTIONS
	 *
	 *************************/
	
	public String convertToRDFTTL(Object modelObj, boolean prefixes) {
		String outputTriples = this.convertToRDF(modelObj, "TURTLE");
		
		if (!prefixes)
			outputTriples = outputTriples.replaceAll("(?m)^@prefix.*?[\r\n]", "");
		
		return outputTriples;
	}
	
	// The other output formats do not have prefixes so there is no need for methods like the above
	
	public String convertToRDFTTL(Object modelObj) {	
		return this.convertToRDF(modelObj, "TURTLE");
	}

	public String convertToRDFN3(Object modelObj) {
		return this.convertToRDF(modelObj, "N3");
	}
	
	public String convertToRDFNT(Object modelObj) {
		return this.convertToRDF(modelObj, "N-TRIPLES");
	}
	
	public String convertToRDFJSON(Object modelObj) {
		return this.convertToRDF(modelObj, "RDF/JSON");
	}
	
	public String convertToRDFXML(Object modelObj, boolean prefixes) {
		String outputTriples = this.convertToRDFXML(modelObj);
		
		//	delete xml header and last empty line 
		outputTriples = outputTriples.substring( outputTriples.indexOf('\n') + 1 );
		outputTriples = outputTriples.substring( 0, outputTriples.lastIndexOf('\n') );
		
		if (!prefixes){
			// delete rdf main tag
			outputTriples = outputTriples.substring( outputTriples.indexOf('\n') + 1 );
			outputTriples = outputTriples.substring( 0, outputTriples.lastIndexOf('\n') );
		}
		
		return outputTriples;
	}
	
	private String convertToRDF(Object modelObj, String outputFormat) {
		String outputString = "";
		
		if (this.checkObjectModel(modelObj)) {
		
			String xmlTriples = this.convertToRDFXML(modelObj, true);
			StringReader inTriples = new StringReader(xmlTriples);
			StringWriter outTriples = new StringWriter();
			
			UDebug.print( "\n\n" + xmlTriples, 10);
			
			Model tripleModel = ModelFactory.createDefaultModel();
			tripleModel.read(inTriples, null, "RDF/XML");
			
			tripleModel.write(outTriples, outputFormat);
			outputString = outTriples.toString();
		}
		// TODO: RAISE EXCEPTION at convertToRDF(), the object to be "converted" is not a Model object.
		return outputString;
	}
	
	/*************************
	 * 
	 * Miscellaneous FUNCTIONS
	 *
	 *************************/	

	public int countClassSubject()	{
		return this.countClassSubject("");
	}
	
	public int countClassSubject(String graphUri)
	{
		String queryString = "";
		if (graphUri.equals("")) 
			queryString = "SELECT (COUNT(?uri) AS ?count) { ?uri rdf:type "+ this.getClassUri() +" }";
		else 
			queryString = "SELECT (COUNT(?uri) AS ?count) { GRAPH "+ graphUri +" {?uri rdf:type "+ this.getClassUri() +" }}"; 
		
		int count = 0;
		
		ResultSet queryRawResults = this.triplestore.sparqlSelectHandled(queryString);
		QuerySolution generalQueryResults = queryRawResults.next();
		
		RDFNode countNode = generalQueryResults.getLiteral("count");
		count = Integer.parseInt( countNode.toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "") );	
		
		return count;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean checkObjectModel(Object obj) {
		boolean isModelObject = false;
		Class objClass = obj.getClass();
		if (objClass.getPackage().toString().contains("model"))
			isModelObject = true;
		return isModelObject;
	}
	
	public Document setHeader(Document doc)	{
		Element root = doc.getRootElement();
		
		for (Map.Entry<String, Namespace> ns : UConfig.namespaces.entrySet())
			root.addNamespaceDeclaration(ns.getValue());

		return doc;
	}

	protected String writeRDFXML(Document document)
	{
		String output ="";
	
		this.setHeader(document);
		
		switch ( UConfig.rdf_output_format )
		{
			case 0 : output = new XMLOutputter(Format.getRawFormat()    ).outputString(document); break;
			case 1 : output = new XMLOutputter(Format.getCompactFormat()).outputString(document); break;
			case 2 : output = new XMLOutputter(Format.getPrettyFormat() ).outputString(document); break;
			default: output = new XMLOutputter(Format.getRawFormat()    ).outputString(document); break;
		}
		
		return output;	
	}
	
}
