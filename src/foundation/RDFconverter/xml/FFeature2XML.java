package foundation.RDFconverter.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import model.MFeature;
import model.MFeatureVersion;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import utility.UConfig;

public class FFeature2XML {

	private HashMap<String, Namespace> namespaces;

	public FFeature2XML() {
		super();
		
		this.namespaces = UConfig.namespaces;
	}

	public Document convertToRDFXML(MFeature feature) {

		TreeMap<String, MFeatureVersion> versions = (TreeMap<String, MFeatureVersion>) feature.getVersions();
		
		Document authorDoc = new Document();
		Element root = new  Element("RDF",namespaces.get("rdf"));
	
		Element feature_el = new Element("Description",namespaces.get("rdf"));		
		feature_el.setAttribute("about", feature.getUri(), namespaces.get("rdf"));
		
		Element featureType = new Element("type", namespaces.get("rdf"));
		featureType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/history_vgi/hvgi#VGIFeature", namespaces.get("rdf")) );
		feature_el.addContent(featureType);

		for (Map.Entry<String, MFeatureVersion> entry_feature : versions.entrySet()) {
			Element hasVersions = new  Element("hasVersion",this.namespaces.get("hvgi"));
			Element version = new Element("Description",this.namespaces.get("rdf"));
			version.setAttribute("about", entry_feature.getValue().getUri(), this.namespaces.get("rdf"));			
			hasVersions.addContent(version);
			feature_el.addContent(hasVersions);
		}
		
		root.addContent(feature_el);
		authorDoc.addContent(root);
		
		return authorDoc;
	}

}
