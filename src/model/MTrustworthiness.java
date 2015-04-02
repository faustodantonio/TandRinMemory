package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import foundation.FFoundationFacade;
import utility.UConfig;
import utility.UDebug;

public class MTrustworthiness {

	protected String uri;
	protected double value;
	protected Date computedAt;
	
	protected String featureVersionUri;
	protected MFeatureVersion featureVersion;	

	protected SimpleDateFormat sdf;
	
	protected FFoundationFacade foundation;
	
	public MTrustworthiness() {
		this.sdf = UConfig.sdf;
		this.foundation = new FFoundationFacade();
	}
	
//	public MTrustworthiness(MFeatureVersion featureVersion) {
//		this.sdf = UConfig.sdf;
//		
//		this.setUri(this.generateTrustworthinessUri(featureVersion));
//		this.setComputedAt(featureVersion.getIsValidFromString());
//		
//		featureVersion.setTrustworthiness(this);
//		this.setFeatureVersion(featureVersion);
//	}
	
//	public String generateTrustworthinessUri() {
//		return ""+UConfig.graphURI + "Trustworthiness_" + UConfig.module_trustworthiness_calculus + "_" + featureVersion.getUriID();
//	}
	public String generateTrustworthinessUri(MFeatureVersion fVersion) {
		return ""+UConfig.graphURI + "Trustworthiness_" + UConfig.module_trustworthiness_calculus + "_" + fVersion.getUriID();
	}
	
	public MFeatureVersion getFeatureVersion() {
		MFeatureVersion fv = null;
		if(this.featureVersion == null)
			if(this.getFeatureVersionUri() == null || this.getFeatureVersionUri().equals(""))
				UDebug.error("There is no feature version associated!");
			else
				this.setFeatureVersion((MFeatureVersion) 
						foundation.retrieveByUri(this.getFeatureVersionUri(), UConfig.getVGIHGraphURI(), 0, MFeatureVersion.class) );
		else 
			fv = this.featureVersion;
		return fv;
	}
	public void setFeatureVersion(MFeatureVersion featureVersion) {
		this.featureVersionUri = featureVersion.getUri();
		this.featureVersion = featureVersion;
//		this.setUri(this.generateTrustworthinessUri(featureVersion));
//		this.setComputedAt(featureVersion.getIsValidFromString());
	}	
	public String getFeatureVersionUri() {
		return featureVersionUri;
	}
	public void setFeatureVersionUri(String featureVersionUri) {
		this.featureVersionUri = featureVersionUri;
	}	
	
	public String getValueString() {
		return UConfig.getDoubleAsString(value);
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	public Date getComputedAt() {
		return computedAt;
	}
	public void setComputedAt(Date isValidFrom) {
		this.computedAt = isValidFrom;
	}
    public void setComputedAt(String isValidFrom) {
    	try {
			this.computedAt = sdf.parse(isValidFrom);
		} catch (ParseException e) {
			UDebug.error("\n *** ERROR: IsValidFrom field not formatted\n");
			e.printStackTrace();	}
    }
	public String getComputedAtString(){
		String date = "";
		if (this.computedAt != null)
			date = this.sdf.format(this.computedAt);
		return date;
	}
	public String getUri() {
		
		if (this.uri == null) {
			if (this.featureVersion == null) {
				if (this.featureVersionUri == null || this.featureVersionUri.equals(""))
					UDebug.error("There is no feature version associated, can't generate uri");
			}
			else this.uri = this.generateTrustworthinessUri(this.getFeatureVersion());
		}
		
		return this.uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}


	
	public String toString(String rowPrefix)
	{
		String trustworthinessString = "";
		//TODO: implement conversion from MTrustworthiness to String
		return trustworthinessString;
	}
}
