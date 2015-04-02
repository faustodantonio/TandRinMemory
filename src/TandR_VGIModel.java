import java.util.Date;

import controller.CInstallation;
import controller.CTRCalculus;
import controller.validation.CValidation;
import utility.UConfig;
import utility.UDebug;
import view.VShow;


public class TandR_VGIModel {

	private static int dbgLogLevel = 10;
	
	private static String cmdString = UConfig.cmdString;
	
	public static void main(String[] args) {
		// TODO complete application start 
		UConfig.instance();
		
		Date installationStartTmp;
		Date installationEndTmp;
		Date computationStartTmp;
		Date computationEndTmp;
		Date validationStartTmp;
		Date validationEndTmp;
		Date visualizationStartTmp;
		Date visualizationEndTmp;
		
		CTRCalculus trust   = new CTRCalculus();
		CInstallation  install = new CInstallation();

		
		VShow view = new VShow();
		
		debugLogInfo();
		
		/****** INSTALLATION ***/
		
		installationStartTmp = new Date();
		UDebug.log("\nStarting Installation \n", dbgLogLevel);
		UDebug.addLogTab();
		if ( Character.getNumericValue(cmdString.charAt(0)) == 1)
			install.install();
		installationEndTmp = new Date();
		UDebug.removeLogTab();
		UDebug.log("\nEnding Installation \n", dbgLogLevel);
		
		/****** COMPUTATION ***/
		
		computationStartTmp = new Date();
		UDebug.log("\nStarting Computation \n", dbgLogLevel);
		UDebug.addLogTab();
		if ( Character.getNumericValue(cmdString.charAt(1)) == 1)
			trust.computeAll();
		computationEndTmp = new Date();
		UDebug.removeLogTab();
		UDebug.log("\nEnding Computation \n", dbgLogLevel);
		
		/****** VALIDATION ***/
		
		validationStartTmp = new Date();
		UDebug.log("\nStarting Validation \n", dbgLogLevel);
		UDebug.addLogTab();
		if ( Character.getNumericValue(cmdString.charAt(2)) == 1) {
			CValidation validation = new CValidation();
			validation.validate();
		}
		validationEndTmp = new Date();
		UDebug.removeLogTab();
		UDebug.log("\nEnding Validation \n", dbgLogLevel);
		
		/****** VISUALIZATION ***/
		
		visualizationStartTmp = new Date();
		UDebug.log("\nStarting Visualization \n", dbgLogLevel);
		UDebug.addLogTab();
		if ( Character.getNumericValue(cmdString.charAt(3)) == 1)
			view.showAll(trust.getDates());
		visualizationEndTmp = new Date();
		UDebug.removeLogTab();
		UDebug.log("\nEnding Visualization \n", dbgLogLevel);
		
		/****** DEBUG ***/
		
		debugTemporalInformation(
				installationStartTmp, installationEndTmp, computationStartTmp, computationEndTmp, 
				validationStartTmp, validationEndTmp, visualizationStartTmp, visualizationEndTmp);
	}

	private static void debugTemporalInformation(
			Date installationStartTmp, Date installationEndTmp, 
			Date computationStartTmp, Date computationEndTmp, 
			Date validationStartTmp, Date validationEndTmp, 
			Date visualizationStartTmp, Date visualizationEndTmp) {

		String header = ""
				+ "-------------------------------------" 	+ "\n"
				+ "******* Temporal Information ********"	+ "\n"
				+ "-------------------------------------" 	+ "\n";
		
		UDebug.log("\n\n\n " + header , dbgLogLevel);
		
		/****** INSTALLATION ***/
		
		UDebug.log("\n\nStarting Installation at: " + installationStartTmp.toString() , dbgLogLevel);
		UDebug.log("\nInstallation ended at: " + installationEndTmp.toString() , dbgLogLevel);
		UDebug.log("\nInstallation time: " + (  UDebug.formatInterval(installationEndTmp.getTime() - installationStartTmp.getTime())) + " milliseconds", dbgLogLevel);
		
		/****** COMPUTATION ***/
		
		UDebug.log("\n\nStarting Computation at: " + computationStartTmp.toString() , dbgLogLevel);
		UDebug.log("\nComputation ended at: " + computationEndTmp.toString() , dbgLogLevel);
		UDebug.log("\nComputation time: " + ( UDebug.formatInterval(computationEndTmp.getTime() - computationStartTmp.getTime())) + " milliseconds", dbgLogLevel);
		
		/****** VALIDATION ***/
		
		UDebug.log("\n\nStarting Validation at: " + validationStartTmp.toString() , dbgLogLevel);
		UDebug.log("\nValidation ended at: " + validationEndTmp.toString() , dbgLogLevel);
		UDebug.log("\nValidation time: " + ( UDebug.formatInterval(validationEndTmp.getTime() - validationStartTmp.getTime())) + " milliseconds", dbgLogLevel);
		
		/****** VISUALIZATION ***/
		
		UDebug.log("\n\nStarting Visualization at: " + visualizationStartTmp.toString() , dbgLogLevel);
		UDebug.log("\nVisualization ended at: " + visualizationEndTmp.toString() , dbgLogLevel);
		UDebug.log("\nVisualization time: " + ( UDebug.formatInterval(visualizationEndTmp.getTime() - visualizationStartTmp.getTime())) + " milliseconds", dbgLogLevel);
		
		/****** VISUALIZATION ***/
		UDebug.log("\n\nWhole process execution time: " + ( UDebug.formatInterval(visualizationEndTmp.getTime() - installationStartTmp.getTime())) + " milliseconds", dbgLogLevel);
		UDebug.log("\n" , dbgLogLevel);
	}

	private static void debugLogInfo() {
		String info = "";
		
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		String tandrGraphUri = UConfig.getVGIHGraphURI();
		String dataset = UConfig.dataset_selection;
		String module = UConfig.module_selection;
		
		String operations = "";
		
		if ( Character.getNumericValue(cmdString.charAt(0)) == 1) operations += "/Installation";
		if ( Character.getNumericValue(cmdString.charAt(1)) == 1) operations += "/Computation";
		if ( Character.getNumericValue(cmdString.charAt(2)) == 1) operations += "/Validation";
		if ( Character.getNumericValue(cmdString.charAt(3)) == 1) operations += "/Visualization";
		
		info = 	  
				  "********** Main Debug Info **********" 	+ "\n"
				+ "\n" 
				+ "Computed At: " 			+ new Date() 				+ "\n"
				+ "VGIH Graph Uri: "		+ vgihGraphUri 		    	+ "\n"
				+ "TANDR Graph Uri: "		+ tandrGraphUri		    	+ "\n"
				+ "Selected Dataset: "		+ dataset			    	+ "\n"
				+ "Calculation Module: "	+ module		 			+ "\n"
				+ "Actions Performed: "		+ operations	 			+ "\n"
				+ "\n"
				+ "*************************************" 	+ "\n"
				+ "_____________________________________" 	+ "\n"
				+ "\n";
		
		
		UDebug.log(info , dbgLogLevel);
	}
	
	

}
