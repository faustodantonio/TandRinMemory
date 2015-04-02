package foundation.RDFconverter.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.MFeatureVersion;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import utility.UConfig;

public class FFeatureVersion2XML {

	private HashMap<String, Namespace> namespaces;

	public FFeatureVersion2XML() {
		super();
		
		this.namespaces = UConfig.namespaces;
	}

	public Document convertToRDFXML(MFeatureVersion featureVersion) {
		
		Document fvDoc = new Document();
		Element root = new  Element("RDF",namespaces.get("rdf"));
		
		Element feature_el = null, featureType = null, 
				createdBy = null, hasVersion = null, isVersionOf = null, prev_feature_el = null,
				valid = null, author = null , deleted = null,  
				hasGeometry= null;
		
		feature_el = new Element("Description",namespaces.get("rdf"));
		feature_el.setAttribute("about", featureVersion.getUri(), namespaces.get("rdf"));
		
		featureType = new Element("type", namespaces.get("rdf"));
		featureType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/osm_provenance/osp#FeatureState", namespaces.get("rdf")) );		

		createdBy = this.getFVCreatedByElement(featureVersion);
		hasVersion = this.getFVHasVersionElement(featureVersion);
		isVersionOf = getFVisVersionOfElement(featureVersion);
		prev_feature_el = this.getFVprecededByElement(featureVersion.getPrevFVersionUri(), featureVersion);
		valid = this.getFVvalidityIntervalElement(featureVersion);
		hasGeometry = this.getFVhasGeometryElement(featureVersion);
		
		deleted = new Element("isDeleted",namespaces.get("hvgi")).setText(""+(!featureVersion.getIsDeleted()));
		deleted.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#boolean",this.namespaces.get("rdf")));
		
		author = new Element("contributor" , namespaces.get("dcterms"));
		author.setAttribute(new Attribute("resource", featureVersion.getAuthorUri(), namespaces.get("rdf")) );

		ArrayList<Element> tags = this.tagMap2Element(featureVersion);
		
		if ( featureType != null )     feature_el.addContent(featureType);
		if ( createdBy != null )       feature_el.addContent(createdBy);
		if ( hasVersion != null )      feature_el.addContent(hasVersion);
		if ( isVersionOf != null )     feature_el.addContent(isVersionOf);
		if ( prev_feature_el != null ) feature_el.addContent(prev_feature_el);
		if ( valid != null )           feature_el.addContent(valid);
		if ( hasGeometry != null )     feature_el.addContent(hasGeometry);
		if ( deleted != null )         feature_el.addContent(deleted);
		if ( author != null )          feature_el.addContent(author);

		if ( tags != null )
			for (Element tag : tags)
				feature_el.addContent(tag);
		
		root.addContent(feature_el);
		fvDoc.addContent(root);
		
		return fvDoc;
	}

	private Element getFVCreatedByElement(MFeatureVersion featureVersion)
	{
		Element createdBy = new Element("createdBy", namespaces.get("osp"));
	
		createdBy.setAttribute(new Attribute("resource", "http://semantic.web/data/hvgi/edits.rdf#" + featureVersion.getUriID(), namespaces.get("rdf")) );
		
		return createdBy;
	}
	
	private Element getFVHasVersionElement(MFeatureVersion featureVersion)
	{
		Element ver_blank = null, hasVersion = null, versionNo = null, versionType= null;
		
		if (featureVersion.getVersionNo() != null) {
			ver_blank = new Element("Description", namespaces.get("rdf"));;
			hasVersion = new Element("hasVersion",namespaces.get("hvgi"));
			versionType = new Element("type", namespaces.get("rdf"));
			versionType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/history_vgi/hvgi#Version", namespaces.get("rdf")) );
			versionNo = new Element("versionNo", namespaces.get("hvgi")).setText(featureVersion.getVersionNo());
			versionNo.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		}
		
		ver_blank.addContent(versionType);
		ver_blank.addContent(versionNo);
		hasVersion.addContent(ver_blank);
		
		return hasVersion;
	}
	
	private Element getFVisVersionOfElement(MFeatureVersion featureVersion)
	{
		Element isVersionOf = new Element("isVersionOf",namespaces.get("hvgi"));	
		isVersionOf.setAttribute("resource", featureVersion.getFeatureUri() , namespaces.get("rdf"));
		return isVersionOf;
	}
	
	private Element getFVprecededByElement(String prevVersionUri, MFeatureVersion featureVersion)
	{
		Element prev_feature_el = null;
		
		if ( prevVersionUri != null )
		{
			prev_feature_el = new Element("precededBy",namespaces.get("prv"));
			prev_feature_el.setAttribute("resource", prevVersionUri, namespaces.get("rdf"));
		}
		
		return prev_feature_el;
	}
	
	private Element getFVvalidityIntervalElement(MFeatureVersion featureVersion)
	{
		Element valid = null, validityIntervalBlank = null, validityIntervalType = null, validFrom = null, validTo = null,
				instantFrom = null, instantTo = null, instantFromType = null, instantToType = null, 
				dateFrom = null, dateTo = null;
		
		if ((featureVersion.getIsValidFrom() != null) || (featureVersion.getIsValidTo() != null))
		{
			valid = new Element("valid",namespaces.get("hvgi"));
			
			validityIntervalBlank = new Element("Description", namespaces.get("rdf"));
			
			validityIntervalType = new Element("type", namespaces.get("rdf"));
			validityIntervalType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/history_vgi/hvgi#ValidityInterval", namespaces.get("rdf")) );
			
			if (featureVersion.getIsValidFrom() != null){
				validFrom = new Element("validFrom", namespaces.get("hvgi"));
				
				instantFrom = new Element("Description", namespaces.get("rdf"));
				instantFromType = new Element("type",namespaces.get("rdf"));
				instantFromType.setAttribute(new Attribute("resource", "http://www.w3.org/2006/time#Instant", namespaces.get("rdf")));
				dateFrom = new Element("inXSDDateTime",namespaces.get("time")).setText(featureVersion.getIsValidFromString());
				dateFrom.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#dateTime",this.namespaces.get("rdf")));
				
				instantFrom.addContent(instantFromType);
				instantFrom.addContent(dateFrom);
				validFrom.addContent(instantFrom);
				validityIntervalBlank.addContent(validFrom);
			}
			
			if (featureVersion.getIsValidTo() != null){
				validTo = new Element("validTo", namespaces.get("hvgi"));
				
				instantTo = new Element("Description", namespaces.get("rdf"));
				instantToType = new Element("type",namespaces.get("rdf"));
				instantToType.setAttribute(new Attribute("resource", "http://www.w3.org/2006/time#Instant", namespaces.get("rdf")));
				dateTo = new Element("inXSDDateTime",namespaces.get("time")).setText(featureVersion.getIsValidToString());
				dateTo.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#dateTime",this.namespaces.get("rdf")));
				
				instantTo.addContent(instantToType);
				instantTo.addContent(dateTo);
				validTo.addContent(instantTo);
				validityIntervalBlank.addContent(validTo);
			}
			
			validityIntervalBlank.addContent(validityIntervalType);
			valid.addContent(validityIntervalBlank);
		}
		return valid;
	}
	
	private Element getFVhasGeometryElement(MFeatureVersion featureVersion)
	{
		Element hasGeometry = null, geom_blank= null, geometryType= null, asWKT = null ;
		
		if (featureVersion.getWktGeometry() != null){
			hasGeometry = new Element("hasGeometry",namespaces.get("geosparql"));
			geom_blank = new Element("Description", namespaces.get("rdf"));
			geometryType = new Element("type", namespaces.get("rdf"));
			
			if ( featureVersion.getWktGeometry().startsWith("POLYGON") )
				geometryType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/history_vgi/hvgi#Polygon", namespaces.get("rdf")) );
			if ( featureVersion.getWktGeometry().startsWith("LINESTRING") )
				geometryType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/history_vgi/hvgi#Linestring", namespaces.get("rdf")) );
			if ( featureVersion.getWktGeometry().startsWith("POINT") )
				geometryType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/history_vgi/hvgi#Point", namespaces.get("rdf")) );
			
			if ( !featureVersion.getWktGeometry().startsWith("POLYGON") && !featureVersion.getWktGeometry().startsWith("LINESTRING") && !featureVersion.getWktGeometry().startsWith("POINT"))
				geometryType.setAttribute(new Attribute("resource", "http://www.opengis.net/ont/geosparql#Geometry", namespaces.get("rdf")) );
			
			asWKT = new Element("asWKT",namespaces.get("geosparql")).setText(featureVersion.getWktGeometry());
			asWKT.setAttribute(new Attribute("datatype", "http://www.opengis.net/ont/sf#wktLiteral",this.namespaces.get("rdf")));
			}
		
		geom_blank.addContent(geometryType);
		geom_blank.addContent(asWKT);
		hasGeometry.addContent(geom_blank);
		
		return hasGeometry;
	}
	
	private ArrayList<Element> tagMap2Element(MFeatureVersion featureVersion)// Map<String,String> tag_map, String feat_id, String feat_version)
	{
		ArrayList<Element> tag_el_list = new ArrayList<Element>();
		
		for (Map.Entry<String,String> tag : featureVersion.getTags().entrySet())
		{
			Element hasTag, hasValue, hasKey, isKey, isValue;
			Element valueEl, keyEl, tagEl;
			Element valueElType, keyElType, tagElType;
			
			hasTag = new Element("hasTag",namespaces.get("osp"));
			tagEl = new Element("Description",namespaces.get("rdf"));
						
			tagEl.setAttribute("about", "http://semantic.web/data/hvgi/nodeVersions.rdf#tag" + featureVersion.getUriID() +"_"+ tag.getKey(),	this.namespaces.get("rdf"));
			
			tagElType = new Element("type", namespaces.get("rdf"));
			tagElType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/osm_provenance/osp#Tag", namespaces.get("rdf")) );

			hasKey = new Element("hasKey",namespaces.get("osp"));
			hasValue = new Element("hasValue",namespaces.get("osp"));
			
			keyEl = new Element("Description", namespaces.get("rdf"));
			valueEl = new Element("Description", namespaces.get("rdf"));
			keyElType = new Element("type", namespaces.get("rdf"));
			keyElType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/osm_provenance/osp#Key", namespaces.get("rdf")) );
			valueElType = new Element("type", namespaces.get("rdf"));
			valueElType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/osm_provenance/osp#Value", namespaces.get("rdf")) );
			
			isKey = new Element("isKey",namespaces.get("hvgi")).setText( tag.getKey() );
			isValue = new Element("isValue",namespaces.get("hvgi")).setText( tag.getValue() );
			isKey.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
			isValue.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));

			keyEl.addContent(keyElType);
			keyEl.addContent(isKey);
			valueEl.addContent(valueElType);
			valueEl.addContent(isValue);
			
			hasKey.addContent(keyEl);
			hasValue.addContent(valueEl);
			
			tagEl.addContent(tagElType);
			tagEl.addContent(hasKey);
			tagEl.addContent(hasValue);
			
			hasTag.addContent(tagEl);
			tag_el_list.add(hasTag);
		}
		return tag_el_list;
	}

	
}
