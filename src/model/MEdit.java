package model;

import java.util.HashMap;
import java.util.Map.Entry;

public class MEdit {

	private String uri;
	private boolean changesGeometry;
	private MAuthor author;
	private String authorUri;
	private HashMap<String,String> addTags;
	private HashMap<String,String> changesValuesOfKey;
	private HashMap<String,String> removeTags;
	
	
	
	public MEdit() {
		super();
//		this.setAuthor(new MAuthor());
		this.addTags = new HashMap<String, String>();
		this.changesValuesOfKey = new HashMap<String, String>();
		this.removeTags = new HashMap<String, String>();
	}
	public String getUri() {
		if (this.uri == null) this.uri = "";
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public boolean isChangesGeometry() {
		return changesGeometry;
	}
	public void setChangesGeometry(boolean changesGeometry) {
		this.changesGeometry = changesGeometry;
	}
	public String getAuthorUri() {
		return authorUri;
	}
	public void setAuthorUri(String author) {
		this.authorUri = author;
	}
	public MAuthor getAuthor() {
		return author;
	}
	public void setAuthor(MAuthor author) {
		this.author = author;
	}
	public HashMap<String, String> getAddTags() {
		return addTags;
	}
	public void setAddTags(HashMap<String, String> addTags) {
		this.addTags = addTags;
	}
	public HashMap<String, String> getChangesValuesOfKey() {
		return changesValuesOfKey;
	}
	public void setChangesValuesOfKey(HashMap<String, String> changesValuesOfKey) {
		this.changesValuesOfKey = changesValuesOfKey;
	}
	public HashMap<String, String> getRemoveTags() {
		return removeTags;
	}
	public void setRemoveTags(HashMap<String, String> removeTags) {
		this.removeTags = removeTags;
	}
	
	public void addAddedTag(String key, String value)	{
		this.addTags.put(key, value);
	}
	public void addChangedTag(String key, String value)	{
		this.changesValuesOfKey.put(key, value);
	}
	public void addRemovedTag(String key, String value)	{
		this.removeTags.put(key, value);
	}
	
	public String toString(String rowPrefix)
	{
		String editString = "";
		
		editString +=  rowPrefix + "Edit :" + "\n"
				+  rowPrefix +     "\t uri             = \""+ this.getUri() +"\"\n";
		
		editString +=  rowPrefix + "\t changesGeometry = \""+ this.isChangesGeometry() +"\"\n";
		editString +=  rowPrefix + "\t author          = \""+ this.getAuthorUri() +"\"\n";
		if (this.getAuthor() != null) 
			editString += rowPrefix + this.getAuthor().toString(rowPrefix + "\t "); 
		else editString += rowPrefix + "\t Author: null" + "\n"; 
		
		editString += rowPrefix + "\t added   tags : \n";
		for (Entry<String, String> tag : addTags.entrySet())
			editString += rowPrefix + "\t\t "+ tag.getKey() + " => \"" + tag.getValue() +"\"\n";
		
		editString += rowPrefix + "\t changed tags : \n";
		for (Entry<String, String> tag : changesValuesOfKey.entrySet())
			editString += rowPrefix + "\t\t "+ tag.getKey() + " => \"" + tag.getValue() +"\"\n";
		
		editString += rowPrefix + "\t removed tags : \n";
		for (Entry<String, String> tag : removeTags.entrySet())
			editString += rowPrefix + "\t\t "+ tag.getKey() + " => \"" + tag.getValue() +"\"\n";
		
		return editString;
	}
	
}
