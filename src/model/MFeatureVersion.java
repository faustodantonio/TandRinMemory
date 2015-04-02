package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import utility.UConfig;
import utility.UDebug;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import foundation.FFoundationFacade;

public class MFeatureVersion {

	private String uri;
	
	private String featureUri;
	private MFeature feature;
	
	private String prevFVersionUri;
	private MFeatureVersion prevFVersion;
	
	private String trustworthinessUri;
	private MTrustworthiness trustworthiness;
	
	private String  authorUri;
	private MAuthor author;
	
	private String  editUri;
	private MEdit   edit;
	
	private String versionNo;
	private boolean isDeleted;
	private Date   isValidFrom;
	private Date   isValidTo;
	
	private String wktGeometry;
	private Geometry geometry;
	
	private HashMap<String,String> tags;
	
	private SimpleDateFormat sdf;
	private FFoundationFacade foundation;

	public MFeatureVersion() {
		super();
		this.tags = new HashMap<String, String>();

		this.sdf = UConfig.sdf;
		this.foundation = new FFoundationFacade();
	}
	
	public String getGeometryBuffer(Double radius)
	{
		String wktGeometryBuffered = null;
		
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(  ), Integer.parseInt(UConfig.rdf_epsg_crs));
		WKTReader reader = new WKTReader( geometryFactory );
		
		Geometry geometryBuffered;
		try {
			geometryBuffered = ((Geometry) reader.read(this.getWktGeometry())).buffer( radius );
			wktGeometryBuffered = geometryBuffered.toText();
		} catch (com.vividsolutions.jts.io.ParseException e) {
			wktGeometryBuffered = null;
			e.printStackTrace();
		}
		return wktGeometryBuffered;
	}
	
	public String getGeometryBuffer()
	{
		Double radius = UConfig.featureInfluenceRadius;
		return this.getGeometryBuffer(radius);
	}
	
	public String getUriID(){
		String uriID = this.getUri();
		
		uriID = uriID.replace("http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion", "");
		uriID = uriID.replace("http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion", "");
		uriID = uriID.replace("http://semantic.web/data/hvgi/featureVersions.rdf#featureVersion", "");
		
		return uriID;
	}
	
	public boolean isFirst() {
		boolean first = true;
		
		if (this.getFeature().getFirstVersion() != null) {
			if ( this.getFeature().getFirstVersion().getUri().equals(this.uri) ) first = true;
			else first = false;
		}
		else first = false;
		
		return first;
	}
	
	public String getUri() {
		if (this.uri == null) this.uri = "";
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getFeatureUri() {
		return featureUri;
	}
	public void setFeatureUri(String featureUri) {
		this.featureUri = featureUri;
	}
	public void setFeatureUri(String featureUri, int lazyDepth) {
		this.featureUri = featureUri;
		if (lazyDepth>0)
			this.setFeature( (MFeature) foundation.retrieveByUri(featureUri, UConfig.getVGIHGraphURI(), lazyDepth - 1, MFeature.class) );
	}
	
	
	public String generateUri() {
		
		String featureID = "";
		String versionID = "";
		
		UDebug.print("\n\nFeature URI: " + this.getFeatureUri(), 1);
		
//		if ( this.getGeometry().getGeometryType().equals("Point") )
//			featureID = this.getFeatureUri().replace("http://semantic.web/data/hvgi/nodes.rdf#node", "");
//		else if ( this.getGeometry().getGeometryType().equals("LineString") )
//			featureID = this.getFeatureUri().replace("http://semantic.web/data/hvgi/ways.rdf#way", "");
//		else
//			featureID = this.getFeatureUri().replace("http://semantic.web/data/hvgi/features.rdf#feature", "");
		
		featureID = this.getFeatureUri();
		
		featureID = featureID.replace("http://semantic.web/data/hvgi/nodes.rdf#node", "");
		featureID = featureID.replace("http://semantic.web/data/hvgi/ways.rdf#way", "");
		featureID = featureID.replace("http://semantic.web/data/hvgi/features.rdf#feature", "");
		
		versionID = this.versionNo;
		
		if ( this.getGeometry().getGeometryType().equals("Point") )
			this.setUri( "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion" + featureID + "_" + versionID );
		else if ( this.getGeometry().getGeometryType().equals("LineString") )
			this.setUri( "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion" + featureID + "_" + versionID);
		else
			this.setUri( "http://semantic.web/data/hvgi/featureVersions.rdf#featureVersion" + featureID + "_" + versionID);
		
		return this.getUri();
	}
	
	
	public MFeature getFeature() {
		if (this.feature == null) {
			if (this.getFeatureUri() != null && ! this.getFeatureUri().equals(""))
				this.setFeature( (MFeature) foundation.retrieveByUri(this.getFeatureUri(), UConfig.getVGIHGraphURI(), 1, MFeature.class) );
			else this.setFeature(new MFeature());
		}
		return this.feature;
	}
	public void setFeature(MFeature feature) {
		this.setFeatureUri( feature.getUri() );
		this.feature = feature;
	}
	public String getPrevFVersionUri() {
		return prevFVersionUri;
	}
	public void setPrevFVersionUri(String prevFVersionUri) {
		this.setPrevFVersionUri(prevFVersionUri, 0);
	}
	public void setPrevFVersionUri(String prevFVersionUri, int lazyDepth) {
		this.prevFVersionUri = prevFVersionUri;
		if (lazyDepth>0)
			this.setPrevFVersion( (MFeatureVersion) foundation.retrieveByUri(prevFVersionUri, UConfig.getVGIHGraphURI(), lazyDepth - 1, MFeatureVersion.class) );
	}
	public MFeatureVersion getPrevFVersion() {
		if (this.prevFVersion == null) {
			if (this.getPrevFVersionUri() != null && !this.getPrevFVersionUri().equals(""))
				this.setPrevFVersion( (MFeatureVersion) foundation.retrieveByUri(this.getPrevFVersionUri(), UConfig.getVGIHGraphURI(), 0, MFeatureVersion.class) );
			else this.setPrevFVersion( new MFeatureVersion() );
		}
		return this.prevFVersion;
	}
	public void setPrevFVersion(MFeatureVersion prevFVersion) {
		this.setPrevFVersionUri( prevFVersion.getUri() );
		this.prevFVersion = prevFVersion;
	}
	public String getAuthorUri() {
		return authorUri;
	}
	public void setAuthorUri(String authorUri) {
		this.authorUri = authorUri;
	}
	public void setAuthorUri(String authorUri, int lazyDepth) {
		this.authorUri = authorUri;
		if (lazyDepth > 0)
			this.setAuthor( (MAuthor) foundation.retrieveByUri(authorUri, UConfig.getVGIHGraphURI(), lazyDepth - 1, MAuthor.class) );
	}
	public MAuthor getAuthor() {
		if (this.author == null){
			if (this.getAuthorUri() != null && ! this.getAuthorUri().equals(""))
				this.setAuthor( (MAuthor) foundation.retrieveByUri(this.getAuthorUri(), UConfig.getVGIHGraphURI(), 0, MAuthor.class) );
			else this.setAuthor(new MAuthor());
		}
		return this.author;
	}
	public void setAuthor(MAuthor author) {
		this.setAuthorUri(author.getUri());
		this.author = author;
	}
	public String getEditUri() {
		return editUri;
	}
	public void setEditUri(String editUri) {
		this.editUri = editUri;
	}
	public void setEditUri(String editUri, int lazyDepth) {
		this.editUri = editUri;
		if (lazyDepth > 0)
			this.setEdit( (MEdit) foundation.retrieveByUri(editUri, UConfig.getVGIHGraphURI(), lazyDepth - 1, MEdit.class) );
	}
	public MEdit getEdit() {
		if (this.edit == null) {
			if (this.getEditUri() != null && ! this.getEditUri().equals(""))
				this.setEdit( (MEdit) foundation.retrieveByUri(this.getEditUri(), UConfig.getVGIHGraphURI(), 0, MEdit.class) );
			else this.setEdit(new MEdit());
		}
		return this.edit;
	}
	public void setEdit(MEdit edit) {
		this.setEditUri(edit.getUri());
		this.edit = edit;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Date getIsValidFrom() {
		if (this.isValidFrom == null)
			return UConfig.getMinDateTime();
		else
			return isValidFrom;
	}
	public void setIsValidFrom(Date isValidFrom) {
		this.isValidFrom = isValidFrom;
	}
	public Date getIsValidTo() {
		if (this.isValidTo == null)
			return UConfig.getMaxDateTime();
		else
			return isValidTo;
	}
	public void setIsValidTo(Date isValidTo) {
		this.isValidTo = isValidTo;
	}
	public String getIsValidFromString(){
		String date = "";
//		if (this.isValidFrom != null)
			date = this.sdf.format(this.getIsValidFrom());
		return date;
	}
    public void setIsValidFrom(String isValidFrom) {
    	try {
			this.isValidFrom = sdf.parse(isValidFrom);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: IsValidFrom field not formatted\n",5);
			e.printStackTrace();	}
    }
	public String getIsValidToString(){
		String date = "";
//		if (this.isValidTo != null)
			date = this.sdf.format(this.getIsValidTo());
//		else date = UConfig.getMaxDateTimeAsString();
		return date;
	}
    public void setIsValidTo(String isValidTo) {
    	try {
			this.isValidTo = sdf.parse(isValidTo);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: IsValidTo field not formatted\n",5);
			e.printStackTrace();	}
    }
	public String getWktGeometry() {
		return wktGeometry;
	}
	public void setWktGeometry(String wktGeometry) {
		this.wktGeometry = wktGeometry;
	}
	public Geometry getGeometry() {
		if (this.geometry == null ) {
			if(this.wktGeometry == null || this.wktGeometry.equals(""))
				UDebug.error("Can't build geometry. No information available");
			else this.setGeometry(this.wktGeometry);
		}
		
		return geometry;
	}
	public void setGeometry(Geometry the_geom) {
		this.geometry = the_geom;
		
		WKTWriter writer = new WKTWriter();
		this.wktGeometry = writer.write(the_geom);
	}
	public void setGeometry(String wktGeometry) {
		try {
			this.geometry = new WKTReader().read(wktGeometry);
			this.setWktGeometry(wktGeometry);
		} catch (com.vividsolutions.jts.io.ParseException e1) {
			UDebug.print("\n*** ERROR: wkt geometry bad formatted\nCorrection Attempt #1",1);
			UDebug.print("\n*** ERROR: " + e1.getMessage(),2);
			wktGeometry.replace("( ", "(");
			wktGeometry.replace(" )", ")");
				try {
					this.geometry = new WKTReader().read(wktGeometry);
					UDebug.print("\n****** ERROR: Correction Attempt #1 Succeded",1);
				} catch (com.vividsolutions.jts.io.ParseException e2) {
					UDebug.print("\n****** ERROR: Correction Attempt #1 Failed \n The geometry will be set as null",1);
					UDebug.print("\n****** ERROR: " + e2.getMessage(),2);
					this.geometry = null;
				}
		}
	}
	public HashMap<String, String> getTags() {
		return tags;
	}
	public void setTags(HashMap<String, String> tags) {
		this.tags = tags;
	}
	public void addTag(String key, String value)	{
		this.tags.put(key, value);
	}
	
	public MTrustworthiness getTrustworthiness() {
		if (this.trustworthiness == null) {
			if (this.getUri() != null) {				
				this.setTrustworthiness( (MTrustworthiness) foundation.retrieveByUri(this.getTrustworthinessUri(), UConfig.getTANDRGraphURI(), 0, MTrustworthiness.class) );
				this.trustworthiness.setFeatureVersionUri(this.getUri());
				this.trustworthiness.setFeatureVersion(this);
			} else {
				this.trustworthiness = new MTrustworthiness();
			}
		}
		return this.trustworthiness;
	}
	public void setTrustworthiness(MTrustworthiness trustworthiness) {
		this.trustworthiness = trustworthiness;
		this.setTrustworthinessUri(trustworthiness.getUri(), 0);
	}
	public String getTrustworthinessUri() {
		if (this.trustworthinessUri == null || this.trustworthinessUri.equals(""))
			this.setTrustworthinessUri( this.generateTrustworthinessUri() );
		return trustworthinessUri;
	}
	public void setTrustworthinessUri(String trustworthinessUri) {
		this.trustworthinessUri = trustworthinessUri;
	}
	public void setTrustworthinessUri(String trustworthinessUri, int lazyDepth) {
		this.trustworthinessUri = trustworthinessUri;
		if (lazyDepth > 0)
			this.setTrustworthiness( (MTrustworthiness) foundation.retrieveByUri(this.getTrustworthinessUri(), UConfig.getTANDRGraphURI(), lazyDepth - 1, MTrustworthiness.class) );
	}

	public String generateTrustworthinessUri() {
		return ""+UConfig.graphURI + "Trustworthiness_" + UConfig.module_trustworthiness_calculus + "_" + this.getUriID();
	}
	
	public String toString(String rowPrefix)
	{
		String fversionString = "";
		
		fversionString +=  rowPrefix + "FeatureVersion :" + "\n"
				+  rowPrefix +         "\t uri               = \""+ this.getUri() +"\"\n";
		//referenced feature (hvgi:isVersionOf)
		fversionString +=  rowPrefix + "\t reference feature = \""+ this.getFeatureUri() +"\"\n";
		if (this.feature != null) 
			fversionString += rowPrefix + this.getFeature().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t Feature: null" + "\n"; 
		//geometry
		if (this.wktGeometry != null) 
			fversionString +=  rowPrefix + "\t wkt geometry = \""+ this.getWktGeometry() +"\"\n";
		if (this.geometry != null) 
			fversionString += rowPrefix + "\t Geometry: " + this.getGeometry().toString() + "\n"; 
		else fversionString += rowPrefix + "\t Geometry: null" + "\n"; 
		//version (versionNo)
		fversionString +=  rowPrefix + "\t version = \""+ this.getVersionNo() +"\"\n";
		//precedent fv (prv:precedeedBy)
		fversionString +=  rowPrefix + "\t previous version = \""+ this.getPrevFVersionUri() +"\"\n";
		if (this.prevFVersion != null) 
			fversionString += rowPrefix + this.getPrevFVersion().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t FeatureVersion: null" + "\n"; 
		//edit (osp:createdBy)
		fversionString +=  rowPrefix + "\t edit          = \""+ this.getEditUri() +"\"\n";
		if (this.edit != null) 
			fversionString += rowPrefix + this.getEdit().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t Edit: null" + "\n"; 		
		//author (hvgi:hasAuthor)
		fversionString +=  rowPrefix + "\t author          = \""+ this.getAuthorUri() +"\"\n";
		if (this.author != null) 
			fversionString += rowPrefix + this.getAuthor().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t Author: null" + "\n"; 
		//validity
		fversionString +=  rowPrefix + "\t is valid from = \""+ this.getIsValidFromString() +"\"\n";
		fversionString +=  rowPrefix + "\t is valid to   = \""+ this.getIsValidToString() +"\"\n";
		//deleted
		fversionString +=  rowPrefix + "\t version = \""+ this.getVersionNo() +"\"\n";
		//tags
		fversionString += rowPrefix + "\t tags : \n";
		for (Entry<String, String> tag : tags.entrySet())
			fversionString += rowPrefix + "\t\t "+ tag.getKey() + " => \"" + tag.getValue() +"\"\n";
		
		return fversionString;
	}
}
