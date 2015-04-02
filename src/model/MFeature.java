package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import foundation.FFoundationFacade;
import utility.UConfig;
import utility.UDebug;

public class MFeature {

	private String uri;
	private Map<String, MFeatureVersion> versionsByUri;
	private Map<String, String> versionsByVersion;
	
	private MFeatureVersion firstVersion;
	private MFeatureVersion lastVersion;
	
	private FFoundationFacade foundation;
		
	public MFeature() {
		super();
		this.foundation = new FFoundationFacade(); 
		this.versionsByUri = new TreeMap<String, MFeatureVersion>();
		this.versionsByVersion = new TreeMap<String, String>();
	}
	
//	public MFeatureVersion getFirstVersion() {
//		String minFVuri = versionsByVersion.get( Collections.min( versionsByVersion.keySet() ));
//		UDebug.print("\nFirst Version: " + minFVuri.toString(), 10);
//		return this.getFeatureVersionByURI(minFVuri, 0);
//	}
//	
//	public MFeatureVersion getLastVersion() {
//		String maxFVuri = versionsByVersion.get( Collections.max( versionsByVersion.keySet() ));
//		UDebug.print("\nLast Version: " + maxFVuri.toString(), 10);
//		return this.getFeatureVersionByURI(maxFVuri, 0);
//	}
	
	public MFeatureVersion getFirstVersion() {
		return this.firstVersion;
	}
	public MFeatureVersion getLastVersion() {
		return this.lastVersion;
	}
	
	public ArrayList<MFeatureVersion> getPreviousVersions(String version, int lazyDepth) {
		
		ArrayList<MFeatureVersion> versions = new ArrayList<MFeatureVersion>();
		Iterator<Entry<String, String>> verIterator = versionsByVersion.entrySet().iterator();
		Entry<String, String> versionEntry;
		
		boolean beforeLimit = true;
		while ( verIterator.hasNext() && beforeLimit) {
			versionEntry = verIterator.next();
			if (compareVersions(version,versionEntry.getKey()) > 0)//version.compareTo( versionEntry.getKey() ) > 0)
				versions.add( this.getFeatureVersionByVersion(versionEntry.getKey(), 0));
			else beforeLimit = false;
		}
		
		return versions;
	}
	
//	public ArrayList<MFeatureVersion> getCleanedPreviousVersions(MFeatureVersion fversion, int lazyDepth) {
//		
//		String version = fversion.getVersionNo();
//		
//		ArrayList<MFeatureVersion> versions = new ArrayList<MFeatureVersion>();
//		Iterator<Entry<String, String>> verIterator = versionsByVersion.entrySet().iterator();
//		Entry<String, String> versionEntry;
//		
//		boolean beforeLimit = true;
//		while ( verIterator.hasNext() && beforeLimit) {
//			versionEntry = verIterator.next();
//			if (compareVersions(version,versionEntry.getKey()) > 0 ) {
//				//TODO: ***URGENT!!! consider only the latest version for each author! 
//				MFeatureVersion fvEntry = this.getFeatureVersionByVersion(versionEntry.getKey(), 0);
//				if(! fversion.getAuthorUri().equals(fvEntry.getAuthorUri()))
//					versions.add( fvEntry );
//			}
//			else beforeLimit = false;
//		}
//		
//		return versions;
//	}
	
	
	/**
	 * Returns a array list of Feature Versions (fv) such that:
	 * 		+ fv is a version of this feature
	 * 		+ fv has version lower than the version of fversion
	 * 		+ fv has the maximum (possible) version beneath the feature versions of its author 
	 * 
	 * @param fversion
	 * @param lazyDepth
	 * @return
	 */
	public ArrayList<MFeatureVersion> getCleanedPreviousVersions(MFeatureVersion fversion, int lazyDepth) {
		
		String version = fversion.getVersionNo();
		Map<String,MFeatureVersion> cleanedMap = new HashMap<String, MFeatureVersion>();
		ArrayList<MFeatureVersion> result_versions = new ArrayList<MFeatureVersion>();
		
		for (Entry<String,String> versionEntry : versionsByVersion.entrySet()) {
			if (compareVersions(version,versionEntry.getKey()) > 0 ) {
				MFeatureVersion fvEntry = this.getFeatureVersionByVersion(versionEntry.getKey(), 0);
				if ( cleanedMap.containsKey( fvEntry.getAuthorUri() ) ) {
					if( compareVersions( cleanedMap.get(fvEntry.getAuthorUri()).getVersionNo(), fvEntry.getVersionNo()) < 0 ) 
						cleanedMap.put(fvEntry.getAuthorUri(), fvEntry);
				} else {
					cleanedMap.put(fvEntry.getAuthorUri(), fvEntry);
				}
			}
		}
		
		for ( Entry<String, MFeatureVersion> cleanEntry: cleanedMap.entrySet())
			result_versions.add(cleanEntry.getValue());
		
		return result_versions;
	}	
	
	/**
	 * 
	 * @param leftVer
	 * @param rightVer
	 * @return 
	 * 		 1 if leftVer > rightVer; 
	 * 		 0 if leftVer = rightVer; 
	 * 		-1 if leftVer < rightVer;
	 */
	public static int compareVersions(String leftVer, String rightVer) 
	{
		ArrayList<String> leftVer_array =  new ArrayList<String>( Arrays.asList( leftVer.split("\\.") ) );
		ArrayList<String> rightVer_array = new ArrayList<String>( Arrays.asList( rightVer.split("\\.") ) );
		
		int length_gap = leftVer_array.size() - rightVer_array.size();
		int abs_gap = Math.abs(length_gap);
		
		for (int i = 0; i < abs_gap; i++ )
			if (length_gap > 0)
				rightVer_array.add( "0" );
			else
				leftVer_array.add( "0" );	
		
		for ( int i = 0; i < leftVer_array.size() ; i++  ) {
		
			if (leftVer_array.get(i).equals("official")) return -1;
			if (rightVer_array.get(i).equals("official")) return 1;
			
			if (Integer.parseInt(leftVer_array.get(i)) > Integer.parseInt(rightVer_array.get(i)) )
				return 1;
			else if (Integer.parseInt(leftVer_array.get(i)) < Integer.parseInt(rightVer_array.get(i))) 
				return -1;
		}
		
		return 0;
	}
	
	public String toString(String rowPrefix)
	{
		String featureString = "";
		
		featureString +=  rowPrefix + "Feature :" + "\n"
				      +   rowPrefix + "\t uri = \""+ this.getUri() +"\"\n";
		
		featureString += rowPrefix + "\t feature versions : \n";
		for ( Entry<String, MFeatureVersion> version : this.versionsByUri.entrySet())
		{
			featureString += rowPrefix + "\t\t uri = \""+ version.getKey() + "\"\n";
			if (version.getValue() != null)
			featureString += version.getValue().toString(rowPrefix + "\t\t ");
		}
		
		return featureString;
	}

	public String getUriID(){
		String uriID = this.getUri();
		uriID = uriID.replace("http://semantic.web/data/hvgi/nodes.rdf#node", "");
		uriID = uriID.replace("http://semantic.web/data/hvgi/ways.rdf#way", "");
//							   http://semantic.web/data/hvgi/ways.rdf#way		
		uriID = uriID.replace("http://semantic.web/data/hvgi/features.rdf#feature", "");
//		uriID = uriID.replace("http://semantic.web/data/hvgi/featureVersions.rdf#featureVersion", "");
		return uriID;
	}
	public String getUri() {
		if (this.uri == null) this.uri = "";
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Map<String, MFeatureVersion> getVersions() {
		
			for (Entry<String, MFeatureVersion> version : versionsByUri.entrySet())
				if (version.getValue() == null)
					versionsByUri.put( version.getKey() , (MFeatureVersion) foundation.retrieveByUri(version.getKey(), UConfig.getVGIHGraphURI(), 0, MFeatureVersion.class) );
		
		return versionsByUri;
	}
	public void setVersions(Map<String, MFeatureVersion> versions) {
		this.versionsByUri = versions;
	}
	public Map<String, String> getVersionsByVersion() {
		return versionsByVersion;
	}	
	public void setVersionsByVersion(Map<String, String> versionsByVersion) {
		this.versionsByVersion = versionsByVersion;
	}

	@Deprecated
	public void addVersion(String fversionuri, MFeatureVersion fversion)
	{
		this.updateFirstLastVersion(fversionuri, null, fversion);
		this.versionsByUri.put(fversionuri, fversion);
	}
	public void addVersion(String fversionUri, String version, MFeatureVersion fversion)
	{
		this.updateFirstLastVersion(fversionUri, version, fversion);
		this.versionsByUri.put(fversionUri, fversion);
		this.versionsByVersion.put(version, fversionUri);
	}

	private void updateFirstLastVersion(String fversionUri, String version, MFeatureVersion fversion) {
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		if (this.firstVersion == null) {
			if (fversion == null)
				fversion = (MFeatureVersion) foundation.retrieveByUri(fversionUri, vgihGraphUri, 0, MFeatureVersion.class);
			this.firstVersion = fversion;
		} else {
			if ( this.compareVersions(version, this.firstVersion.getVersionNo()) < 0 ){
				if (fversion == null)
					fversion = (MFeatureVersion) foundation.retrieveByUri(fversionUri, vgihGraphUri, 0, MFeatureVersion.class);
				this.firstVersion = fversion;
			}
		}
		
		if (this.lastVersion == null) {
			if (fversion == null)
				fversion = (MFeatureVersion) foundation.retrieveByUri(fversionUri, vgihGraphUri, 0, MFeatureVersion.class);
			this.lastVersion = fversion;
		} else {
			if ( this.compareVersions(version, this.lastVersion.getVersionNo()) > 0 ){
				if (fversion == null)
					fversion = (MFeatureVersion) foundation.retrieveByUri(fversionUri, vgihGraphUri, 0, MFeatureVersion.class);
				this.lastVersion = fversion;
			}
		}
		
//		if (this.lastVersion == null) 
//			this.lastVersion = fversion;
//		else
//			if ( this.compareVersions(fversion.getVersionNo(), this.lastVersion.getVersionNo()) > 0 )
//				this.lastVersion = fversion;
	}
	
	public MFeatureVersion getFeatureVersionByURI(String uri, int lazyDepth) {
		MFeatureVersion featureVersion;
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		if ( this.versionsByUri.get(uri) != null )
			featureVersion = this.versionsByUri.get(uri);
		else {
			featureVersion = (MFeatureVersion) foundation.retrieveByUri(uri, vgihGraphUri, lazyDepth, MFeatureVersion.class);
			this.versionsByUri.put(uri, featureVersion);
		}
		return featureVersion;
	}
	public MFeatureVersion getFeatureVersionByVersion(String version, int lazyDepth) {
		String uri = this.versionsByVersion.get(version);
		return this.getFeatureVersionByURI(uri, lazyDepth);
	}

	public String generateFeatureVesionUri(String version)	{
		String fvUri = "";
		
		UDebug.print("\n\n(generating fv uri) Feature Uri: "+this.getUri()+"\n",1);
		
		if ( this.getUri().contains("http://semantic.web/data/hvgi/nodes.rdf#node") )
			fvUri = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion";
		else if ( this.getUri().contains("http://semantic.web/data/hvgi/ways.rdf#way") )
			fvUri = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion";
		else
			fvUri = "http://semantic.web/data/hvgi/featureVersions.rdf#featureVersion";
		
		fvUri = fvUri + this.getUriID() + "_" + version;
		
		UDebug.print("(generating fv uri) Feature Version Uri: "+fvUri+"\n\n",1);
		
		return fvUri;
	}
	
	public String generateGeneralFeatureVesionUri(String version)	{
		String fvUri = "";
		
		UDebug.print("\n\n(generating fv uri) Feature Uri: "+this.getUri()+"\n",1);
		
		fvUri = "http://semantic.web/data/hvgi/featureVersions.rdf#featureVersion";
		
		fvUri = fvUri + this.getUriID() + "_" + version;
		
		UDebug.print("(generating fv uri) Feature Version Uri: "+fvUri+"\n\n",1);
		
		return fvUri;
	}
}
