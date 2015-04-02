package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class UDebug {
	
//	FileOutputStream output;
	private static PrintStream output;
	private static PrintStream log;
	
	private static int logTabs = 0;

	public static void print(String text, int level){
		if (level <= UConfig.debugLevel)
			System.out.print(text);
	}

	public static void error(String text) {
		System.err.print("\n *** ERROR: ");
		print(text, 1);
	}
	
	public static void output(String text, int level){
		
		if (level <= UConfig.debugLevel)
			System.out.print(text);
		
		getOutputFile().print(text);
	}
	
	public static void log(String text, int level){
		
		text = text.replaceAll("\\n", "\n"+UDebug.getTabulation());
		
		if (level <= UConfig.debugLevel)
			System.out.print(text);
		
		getLogFile().print(UDebug.getTabulation() + text);
	}
	
	private static PrintStream getOutputFile() {
		
		String path = UConfig.generalOutputFilePath;
		File file = new File(path);
		
		if ( ! file.exists() )
			file.getParentFile().mkdirs();
		
		if (UDebug.output == null)
		{
			try
		    {
				FileOutputStream outputFile = new FileOutputStream(path);
		        UDebug.output = new PrintStream(outputFile);
		    }
		    catch (IOException e)
		    {
		        System.out.println("Errore: " + e);
		        System.exit(1);
		    }
		}
		
		return UDebug.output;
	}
	
	private static PrintStream getLogFile() {
		
		String path = UConfig.logFilePath;
		File file = new File(path);
		
		if ( ! file.exists() )
			file.getParentFile().mkdirs();
		
		if (UDebug.log == null)
		{
			try
		    {
				FileOutputStream logFile = new FileOutputStream(path);
		        UDebug.log = new PrintStream(logFile);
		    }
		    catch (IOException e)
		    {
		        System.out.println("Errore: " + e);
		        System.exit(1);
		    }
		}
		
		return UDebug.log;
	}
	
	private static String getTabulation() {
		String tabs = "";
		for(int i = 0; i < UDebug.logTabs; i++)
			tabs += "\t";
		return tabs;
	}
	
	public static void addLogTab() {
		UDebug.logTabs ++;
	}
	public static void removeLogTab() {
		if (UDebug.logTabs > 0)
			UDebug.logTabs --;
	}
	
	public static String formatInterval(long milliseconds)
	{
		String interval = "";
		
		long ms   = milliseconds % 1000;
		long sec  = (milliseconds / 1000) % 60 ;
		long min  = ((milliseconds / (1000*60)) % 60);
		long hour = ((milliseconds / (1000*60*60)) % 24);
		
		interval     = ms   + " ms" + interval;
		if (sec != 0 || min != 0 || hour != 0)
			interval = sec  + " sec " + interval;
		if (min != 0 || hour != 0)
			interval = min  + " min " + interval;
		if (hour != 0)
			interval = hour + " hour " + interval;
		
		return interval;
	}
	
}
