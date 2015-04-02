package foundation.RDFconverter.xml;

import java.util.HashMap;

import model.MAuthor;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import utility.UConfig;

public class FAuthor2XML {

	private HashMap<String, Namespace> namespaces;

	public FAuthor2XML() {
		super();
		
		this.namespaces = UConfig.namespaces;
	}

	public Document convertToRDFXML(MAuthor author) {
		
		Document authorDoc = new Document();
		Element root = new  Element("RDF",namespaces.get("rdf"));
		
		Element authorEl = new Element("Description",namespaces.get("rdf"));
		authorEl.setAttribute(new Attribute("about", author.getUri(), namespaces.get("rdf")) );
		
		Element foafAccount = new Element("account",this.namespaces.get("foaf"));
		
		Element authorType = new Element("type", namespaces.get("rdf"));
		authorType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/osm_provenance/osp#User", namespaces.get("rdf")) );
		
		Element blank = new Element("Description", namespaces.get("rdf"));
		Element blankType = new Element("type", namespaces.get("rdf"));
		blankType.setAttribute(new Attribute("resource", "http://xmlns.com/foaf/0.1/OnlineAccount", namespaces.get("rdf")));
		
		Element accountServerHomepage = new Element("accountServerHomepage", namespaces.get("foaf")).setText( author.getAccountServerHomepage() );
		Element accountName = new Element("accountName", namespaces.get("foaf")).setText( author.getAccountName() );
		accountName.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		
		blank.addContent(blankType);
		blank.addContent(accountName);
		blank.addContent(accountServerHomepage);
		
		foafAccount.addContent(blank);
		
		authorEl.addContent(authorType);
		authorEl.addContent(foafAccount);
		
		root.addContent(authorEl);
		authorDoc.addContent(root);
		
		return authorDoc;
	}

}
