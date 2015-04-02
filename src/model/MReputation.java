package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import foundation.FFoundationFacade;
import utility.UConfig;
import utility.UDebug;

public class MReputation {

	protected String uri;
	protected double value;
	protected Date computedAt;
	
	protected String  authorUri;
	protected MAuthor author;	

	protected SimpleDateFormat sdf;
	
	protected FFoundationFacade foundation;
	
	public MReputation() {
		this.sdf = UConfig.sdf;
		this.foundation = new FFoundationFacade();
	}
	
	public MReputation(MAuthor author) {
		this.sdf = UConfig.sdf;
		this.foundation = new FFoundationFacade();
		
		this.setUri(this.generateReputationUri(author));
		
		author.setReputation(this);
		this.setAuthor(author);
	}
	
	public MReputation(MAuthor author, String computedAt) {
		this.sdf = UConfig.sdf;
		this.foundation = new FFoundationFacade();
		
		this.setUri(this.generateReputationUri(author));
		this.setComputedAt(computedAt);
		
		author.setReputation(this);
		this.setAuthor(author);
	}
	
	public String generateReputationUri() {
		return ""+UConfig.graphURI + "Reputation_" + UConfig.module_trustworthiness_calculus + "_" + author.getAccountName();
	}
	public String generateReputationUri(MAuthor contributor) {
		return ""+UConfig.graphURI + "Reputation_" + UConfig.module_trustworthiness_calculus + "_" + contributor.getAccountName();
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
		if (this.uri == null)
			if (this.author == null) 
				if (this.authorUri == null || this.authorUri.equals(""))
					UDebug.error("There is no author associated, can't generate uri");
			else
				this.uri = this.generateReputationUri(this.getAuthor());
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public MAuthor getAuthor() {
		
		MAuthor contributor = null;
		if(this.author == null)
			if(this.getAuthorUri() == null || this.getAuthorUri().equals(""))
				UDebug.error("There is no author version associated!");
			else
				this.setAuthor((MAuthor) 
						foundation.retrieveByUri(this.getAuthorUri(), UConfig.getVGIHGraphURI(), 0, MAuthor.class) );
		else 
			contributor = this.author;
		return contributor;
	}
	public void setAuthor(MAuthor author) {
		this.author = author;
		this.setAuthorUri(author.getUri());
		this.setUri(this.generateReputationUri());
	}
	
	public String getAuthorUri() {
		return authorUri;
	}
	public void setAuthorUri(String authorUri) {
		this.authorUri = authorUri;
	}
	
	public String toString(String rowPrefix)
	{
		String reputationString = "";
		//TODO: implement conversion from MReputation to String
		return reputationString;
	}
		
	
}
