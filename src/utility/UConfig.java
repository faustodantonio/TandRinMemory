package utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.jdom2.Namespace;

public class UConfig {
	
	private static UConfig instance = null;
	
	/*************************
	 * 
	 * Behavioural PARAMETERS
	 *
	 *************************/	
	
	
	/**
	 * 1st char -> 0:don't install   *|* 1: install
	 * 2nd char -> 0:don't compute   *|* 1: compute
	 * 3rd char -> 0:don't validate  *|* 1: validate
	 * 4th char -> 0:don't visualize *|* 1: visualize
	 */
	public static String cmdString = "0010";
	
	/**
	 * 0 -> Never perform installation (DEFAULT)                            *|*  
	 * 1 -> Perform installation whether it has not yet been performed      *|*  
	 * 2 -> Perform installation, deleting previous installed data          *|*
	 * 3 -> Leave previous dataset, but deletes the computed T and R values *|*
	 * 4 -> Restore dataset, leaving the computed T and R values            *|*
	 */
	public static int installation_data = 2;
	
	/**
	 * 0 -> leaves information in triplestore and retrieve it when needed.
	 * 1 -> loads the whole semantic information into in-memory data structure.
	 */
	public static int in_memory = 0;
	
	/**
	 * Possible values: 
	 * **|** Test **|** LAquila **|**  Wien **|**  SingerstrasseWien **|**  StephansdomWien 
	 * **|** SeestadtWien **|** BreitenleerWien **|** DonaustadtFieldsWien **|** DonaustadtWoodsWien **|** DonaustadtLakesWien
	 */
//	public static String dataset_selection = "SingerstrasseWien";
//	public static String dataset_selection = "StephansdomWien";
//	public static String dataset_selection = "Wien";
//	public static String dataset_selection = "LAquila";
//	public static String dataset_selection = "Test";
//	public static String dataset_selection = "SeestadtWien";
//	public static String dataset_selection = "BreitenleerWien";
	public static String dataset_selection = "DonaustadtFieldsWien";
//	public static String dataset_selection = "DonaustadtLakesWien";
	
	/**
	 * Possible values: 
	 * **|** TandR **|**
	 */
	public static String module_selection = "TandR";
	
	/**
	 * 0 -> Raw	     *|*  
	 * 1 -> Compact  *|*  
	 * 2 -> Pretty   *|*
	 */
	public static int rdf_output_format = 2 ;
	
	public static boolean graph_usage = true;
	
	public static int debugLevel = 4;
	
	/*************************
	 * 
	 * TripleStore connection PARAMETERS
	 *
	 *************************/	
	
	public static String triplestoreConnectionClass = "FParliament";
//	public static String triplestoreConnectionClass = "FFuseki";
	
	public static String datasetQueryURI_Fuseki 			= "http://semantic.net:3030/TandR_Model/query";
	public static String datasetSPARQLQueryURI_Parliment	= "http://semantic.net:8080/parliament/sparql";
	public static String datasetBULKQueryURI_Parliment 		= "http://semantic.net:8080/parliament/bulk";
	
	/************************* 
	 * 
	 * Module selection PARAMETERS 
	 * 
	 *************************/ 
	
	/**
	 * the referenced class by ' main_trustworthiness_calculus ' has to extend controller.CMainFactor class
	 * the referenced class by ' validation_trustworthiness '    has to extend controller.validation.CValidation class
	 * the referenced class by ' view_trustworthiness '          has to extend view.VTrustworthiness class
	 * the referenced class by ' trustworthiness_export '        has to implement foundation.FTrustworthinessExport interface
	 * the referenced class by ' reputation_export '             has to implement foundation.FReputationExport interface
	 * the referenced class by ' tandr_import '                  has to extend foundation.FInstallation class
	 */
	
	public static String module_trustworthiness_calculus;
	
	public static String main_trustworthiness_calculus;
	public static String validation_trustworthiness;
	public static String view_trustworthiness;
	public static String trustworthiness_export;
	public static String reputation_export;
	public static String tandr_import;
	
	/*************************
	 * 
	 * Installation and Named Graph PARAMETERS
	 *
	 *************************/
	
	public static String graphURI = "http://parliament.semwebcentral.org/parliament#";

	public static String inputRDFfileRegex = ".*.rdf";
	
	public static String hvgiGraph;
	public static String tandrGraph;
	public static String inputRDFfilesDirectory;
	
	public static String lowerTGraph;
	public static String averageTGraph;
	public static String higherTGraph;
	public static String validationGraph;
	
	/*************************
	 * 
	 * View PARAMETERS
	 *
	 *************************/
	
	public static String generalOutputFilePath;
	public static String logFilePath;
	
	/*************************
	 * 
	 * Miscellaneous PARAMETERS
	 *
	 *************************/	

	private static String minDateTime;
	private static String maxDateTime;
	
	public static String validationPath;
	
	public static HashMap<String, Namespace> namespaces;
	
	/*************************
	 *
	 * Temporal and Spatial PARAMETERS
	 *
	 *************************/
	
	// EPSG:900913  ---->    Spherical Mercator
	// EPSG:3857    ---->    Spherical Mercator
	// EPSG:4326    ---->    WGS84
	public static String rdf_epsg_crs = "900913";
	public static String ogd_epsg_crs = "900913";
	public static double featureInfluenceRadius = 10;
	public static double temporalCurveSlope = 1000.0;
//	public static double temporalCurveSlope = 1500.0
	
	public static SimpleDateFormat sdf;

	//***************************************************************************************//
	//*************************************** METHODS ***************************************//
	//***************************************         ***************************************//
	
	/**********  Instantiation methods  **********/
	
	public static UConfig instance()
	{
		if (instance == null)
			instance = new UConfig();
		
		return instance;
	}

	private UConfig()
	{
		this.initNamespaces();
		this.initModule();
		this.initVariables();
//		initTestVariables();
//		initTandRModule();
		UConfig.sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	private void initModule() {
		Method method;
		String methodName = "init" + UConfig.module_selection + "Module";
		
		try {
		  method = this.getClass().getMethod(methodName);
		  method.invoke(this);
		} catch (SecurityException e) {
			this.initTandRModule();
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			this.initTandRModule();
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			this.initTandRModule();
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			this.initTandRModule();
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			this.initTandRModule();
			e.printStackTrace();
		}		
	}
	
	private void initVariables() {
		Method method;
		String methodName = "init" + UConfig.dataset_selection + "Variables";
		
		try {
		  method = this.getClass().getMethod(methodName);
		  method.invoke(this);
		} catch (SecurityException e) {
			this.initWienVariables();
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			this.initWienVariables();
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			this.initWienVariables();
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			this.initWienVariables();
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			this.initWienVariables();
			e.printStackTrace();
		}
	}

	public void initTandRModule() {
		module_trustworthiness_calculus = "tandr";
		
		main_trustworthiness_calculus = "controller.CTandR";
		validation_trustworthiness    = "controller.CValidationTandR";
		view_trustworthiness 		  = "view.VTrustworthinessTandr";	
		trustworthiness_export 		  = "foundation.FTrustworthinessTandr";
		reputation_export 			  = "foundation.FReputationTandr";
		tandr_import 				  = "foundation.FTandR";		
	}

	/**********  Initialization methods  **********/
	
	@SuppressWarnings("deprecation")
	public void initTestVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_test";
		tandrGraph 				= "tandr_test";
		inputRDFfilesDirectory 	= "./input/test/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_test";
		averageTGraph 	= "averegeT_test";
		higherTGraph 	= "higherT_test";
		validationGraph = "validation_test";
		
		minDateTime = "2012-01-01T00:00:00Z";
		maxDateTime = "2012-01-01T06:00:00Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/testValidation.csv";
		
		generalOutputFilePath = "./output/test.txt";
		logFilePath = "./output/log_test_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initLAquilaVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_laquila";
		tandrGraph 				= "tandr_laquila";
		inputRDFfilesDirectory 	= "./input/laquila/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_laquila";
		averageTGraph 	= "averegeT_laquila";
		higherTGraph 	= "higherT_laquila";
		validationGraph = "validation_laquila";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "";
		
		generalOutputFilePath = "./output/laquila.txt";
		logFilePath = "./output/log_laquila_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_wien";
		tandrGraph 				= "tandr_wien";
		inputRDFfilesDirectory 	= "./input/wien/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_wien";
		averageTGraph 	= "averegeT_wien";
		higherTGraph 	= "higherT_wien";
		validationGraph = "validation_wien";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/REALNUT2012OGD.csv";
		
		generalOutputFilePath = "./output/wien.txt";
		logFilePath = "./output/log_wien_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initSingerstrasseWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_singerstrasse";
		tandrGraph 				= "tandr_singerstrasse";
		inputRDFfilesDirectory 	= "./input/singerstrasseWien2014/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_singerstrasse";
		averageTGraph 	= "averegeT_singerstrasse";
		higherTGraph 	= "higherT_singerstrasse";
		validationGraph = "validation_singerstrasse";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/3857/stephansdomFromBasemap_3857.csv";
		
		generalOutputFilePath = "./output/singerstrasse.txt";
		logFilePath = "./output/log_singerstrasse_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initStephansdomWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_stephansdom";
		tandrGraph 				= "tandr_stephansdom";
		inputRDFfilesDirectory 	= "./input/stephansdomWien/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_stephansdom";
		averageTGraph 	= "averegeT_stephansdom";
		higherTGraph 	= "higherT_stephansdom";
		validationGraph = "validation_stephansdom";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/3857/stephansdomFromBasemap_3857.csv";
		
		generalOutputFilePath = "./output/stephansdom.txt";
		logFilePath = "./output/log_stephansdom_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initSeestadtWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_seestadt";
		tandrGraph 				= "tandr_seestadt";
		inputRDFfilesDirectory 	= "./input/seestadtWien/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_seestadt";
		averageTGraph 	= "averegeT_seestadt";
		higherTGraph 	= "higherT_seestadt";
		validationGraph = "validation_seestadt";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/3857/REALNUT2012OGD_BEZ22_3857.csv";
		
		generalOutputFilePath = "./output/seestadt.txt";
		logFilePath = "./output/log_seestadt_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initBreitenleerWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_breitenleer";
		tandrGraph 				= "tandr_breitenleer";
		inputRDFfilesDirectory 	= "./input/breitenleerWien/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_breitenleer";
		averageTGraph 	= "averegeT_breitenleer";
		higherTGraph 	= "higherT_breitenleer";
		validationGraph = "validation_breitenleer";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/3857/REALNUT2012OGD_BEZ22_3857.csv";
		
		generalOutputFilePath = "./output/breitenleer.txt";
		logFilePath = "./output/log_breitenleer_"+ new Date().toGMTString().trim() +".txt";
	}

	@SuppressWarnings("deprecation")
	public void initDonaustadtFieldsWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_donaustadtFields";
		tandrGraph 				= "tandr_donaustadtFields";
		inputRDFfilesDirectory 	= "./input/donaustadtFieldsWien/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_donaustadtFields";
		averageTGraph 	= "averegeT_donaustadtFields";
		higherTGraph 	= "higherT_donaustadtFields";
		validationGraph = "validation_donaustadtFields";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/3857/REALNUT2012OGD_BEZ22_DonaustadtField_3857.csv";
		
		generalOutputFilePath = "./output/donaustadtFields.txt";
		logFilePath = "./output/log_donaustadtFields_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initDonaustadtWoodsWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_donaustadtWoods";
		tandrGraph 				= "tandr_donaustadtWoods";
		inputRDFfilesDirectory 	= "./input/donaustadtWoodsWien/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_donaustadtWoods";
		averageTGraph 	= "averegeT_donaustadtWoods";
		higherTGraph 	= "higherT_donaustadtWoods";
		validationGraph = "validation_donaustadtWoods";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/3857/REALNUT2012OGD_BEZ22_3857.csv";
		
		generalOutputFilePath = "./output/donaustadtWoods.txt";
		logFilePath = "./output/log_donaustadtWoods_"+ new Date().toGMTString().trim() +".txt";
	}
	
	@SuppressWarnings("deprecation")
	public void initDonaustadtLakesWienVariables() {
		
		/*** Data Graphs		 */
		hvgiGraph 				= "hvgi_donaustadtLakes";
		tandrGraph 				= "tandr_donaustadtLakes";
		inputRDFfilesDirectory 	= "./input/donaustadtLakesWien/";
		
		/*** Validation Graphs		 */
		lowerTGraph 	= "lowerT_donaustadtLakes";
		averageTGraph 	= "averegeT_donaustadtLakes";
		higherTGraph 	= "higherT_donaustadtLakes";
		validationGraph = "validation_donaustadtLakes";
		
		minDateTime = "2005-09-15T21:42:44Z";
		maxDateTime = "2012-03-31T03:29:56Z"; // the original one is "2012-03-30T03:29:56Z"
		
		validationPath = "input/validation/3857/REALNUT2012OGD_BEZ22_3857.csv";
		
		generalOutputFilePath = "./output/donaustadtLakes.txt";
		logFilePath = "./output/log_donaustadtLakes_"+ new Date().toGMTString().trim() +".txt";
	}
	
	private void initNamespaces()
	{
		namespaces = new HashMap<String, Namespace>();
		
		Namespace rdf       = Namespace.getNamespace("rdf",       "http://www.w3.org/1999/02/22-rdf-syntax-ns#"        );
		Namespace xsd       = Namespace.getNamespace("xsd",       "http://www.w3.org/2001/XMLSchema#"                  );
		Namespace fn        = Namespace.getNamespace("fn",        "http://www.w3.org/2005/xpath-functions#"            );
		Namespace afn       = Namespace.getNamespace("afn",       "http://jena.hpl.hp.com/ARQ/function#"               );
		Namespace dcterms   = Namespace.getNamespace("dcterms",   "http://purl.org/dc/terms/"                          );
		Namespace foaf      = Namespace.getNamespace("foaf",      "http://xmlns.com/foaf/0.1/"                         );
		Namespace time      = Namespace.getNamespace("time",      "http://www.w3.org/2006/time#"                       );		
		Namespace geosparql = Namespace.getNamespace("geosparql", "http://www.opengis.net/ont/geosparql#"              );
		Namespace geof      = Namespace.getNamespace("geof",      "http://www.opengis.net/def/function/geosparql/"     );
		Namespace sf        = Namespace.getNamespace("sf",        "http://www.opengis.net/ont/sf#"                     );
		Namespace units     = Namespace.getNamespace("units",     "http://www.opengis.net/def/uom/OGC/1.0/"            );
		Namespace prv       = Namespace.getNamespace("prv",       "http://purl.org/net/provenance/ns#"                 );
		Namespace osp       = Namespace.getNamespace("osp",       "http://semantic.web/vocabs/osm_provenance/osp#"     );
		Namespace hvgi      = Namespace.getNamespace("hvgi",      "http://semantic.web/vocabs/history_vgi/hvgi#"       );
		Namespace tandr     = Namespace.getNamespace("tandr",     "http://semantic.web/vocabs/tandr_assessment/tandr#" );
		Namespace graphs    = Namespace.getNamespace("graphs",    "http://parliament.semwebcentral.org/parliament#"    );
				
		namespaces.put(rdf.getPrefix()       , rdf       );
		namespaces.put(xsd.getPrefix()       , xsd       );
		namespaces.put(fn.getPrefix()        , fn        );
		namespaces.put(afn.getPrefix()       , afn       );
		namespaces.put(dcterms.getPrefix()   , dcterms   );
		namespaces.put(foaf.getPrefix()      , foaf      );
		namespaces.put(time.getPrefix()      , time      );
		namespaces.put(geosparql.getPrefix() , geosparql );
		namespaces.put(geof.getPrefix()      , geof      );
		namespaces.put(sf.getPrefix()        , sf        );
		namespaces.put(units.getPrefix()     , units     );
		namespaces.put(prv.getPrefix()       , prv       );		
		namespaces.put(osp.getPrefix()       , osp       );
		namespaces.put(hvgi.getPrefix()      , hvgi      );
		namespaces.put(tandr.getPrefix()     , tandr     );
		namespaces.put(graphs.getPrefix()    , graphs    );
	}
	
	/**********  Get/Set methods  **********/
	
	public static String getVGIHGraphURI(){
		return "<" + UConfig.graphURI + UConfig.hvgiGraph + ">";	}
	public static String getTANDRGraphURI(){
		return "<" + UConfig.graphURI + UConfig.tandrGraph + ">";	}
	public static String getLOWESTTGraphURI(){
		return "<" + UConfig.graphURI + UConfig.lowerTGraph + ">";	}
	public static String getAVERAGETraphURI(){
		return "<" + UConfig.graphURI + UConfig.averageTGraph + ">";	}
	public static String getHIGHESTTGraphURI(){
		return "<" + UConfig.graphURI + UConfig.higherTGraph + ">";	}
	public static String getValidationGraphURI(){
		return "<" + UConfig.graphURI + UConfig.validationGraph + ">";	}
	
	public static String getMinDateTimeAsString(){
		return minDateTime;	}
	public static String getMaxDateTimeAsString(){
    	return maxDateTime;	}
	
	public static Date getMinDateTime(){
		Date minDate = new Date();
		try {
			minDate = sdf.parse(minDateTime);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: minDateTime field not formatted\n",5);
			e.printStackTrace();	}
    	return minDate;
	}
	public static Date getMaxDateTime(){
		Date maxDate = new Date();
		try {
			maxDate = sdf.parse(maxDateTime);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: minDateTime field not formatted\n",5);
			e.printStackTrace();	}
    	return maxDate;
	}
	
	public static String getDoubleAsString(double decimal) {
		
		int startIndex = 0;
		int endIndex = 9;
		
		BigDecimal Decimal = new BigDecimal(decimal);
		String value = Decimal.toPlainString();
		
		if ( value.length() < endIndex)
			endIndex = value.length();
		
		return value.substring(startIndex, endIndex);
	}
	
}
