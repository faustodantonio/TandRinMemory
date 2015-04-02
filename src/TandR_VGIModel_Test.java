import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;

import utility.UConfig;
import utility.UDebug;
import controller.CInstallation;
import controller.CTest;
import foundation.FFuseki;


public class TandR_VGIModel_Test {

	public static void main(String[] args) {
		
		UConfig.instance();
		
		CTest test = new CTest();
//		CTRCalculus trust = new CTRCalculus();
//		CInstallation install = new CInstallation();
//		CValidation validation = new CValidation();
		
//		test.printAllAuthorURIs();
//		test.printFirstAuthorInfos();
//		test.printEditInfos();
//		test.printFeatureInfos();
//		test.printFeatureVersionInfos();
//		test.buildEffectHierarchy() ;
//		test.getTrustworthinessCalculation();
//		test.retreiveNextFV();
//		test.retreiveSuccNeighboursFVs();
		
//		test.printTrustworthiness();
		test.printQuery_2();
		
//		test.printCleanedPreviousVersions();
		
//		test.printReputationInfos();
		
//		validation.validate();
		
//		install.install();

//		trust.buildMaps();
//		UDebug.print( "# di Features in RAM: "+trust.getFeatures().size() +"\n", 2 );
//		UDebug.print( "# di FeatureVersions in RAM: "+trust.getFeatureVersionsByDate().size() +"\n", 2 );
		
//		FFuseki fuseki = new FFuseki();
//		ResultSet res = fuseki.getQueryResult("Select * {?s ?p ?o}");
//		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(res);
//		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",1);
		
		
	}

}
