package controller;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.jdom2.Namespace;

import model.MAuthor;
import model.MEdit;
import model.MFeature;
import model.MFeatureVersion;
import model.MReputation;
import modules.tandr.foundation.FTandrFacade;
import modules.tandr.model.MReputationTandr;
import modules.tandr.view.VReputationTandr;
import utility.UConfig;
import utility.UDebug;
import view.VReputation;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import foundation.FFoundationFacade;

public class CTest {
	
	private String graphUri = "<http://parliament.semwebcentral.org/parliament#hvgi>";
	
	@SuppressWarnings("deprecation")
	public void printAllAuthorURIs()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		
		ResultSet uris = ffactory.retrieveAllAsResultSet("MAuthor");
		
	    ResultSetFormatter.out( uris );	    	  
	}
	
	public void printFirstAuthorInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		
		ArrayList<String> uris = ffactory.retrieveAll("MAuthor", graphUri);
		
		UDebug.print( "Author URI: "+ uris.get(0) +"\n\n" , 2);
		
		MAuthor author = (MAuthor) ffactory.retrieveByUri( uris.get(0), graphUri, 0, "MAuthor");
		
		UDebug.print(author.toString(""),1);    	  
	}

	public void printFirstEditInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		ArrayList<String> uris = ffactory.retrieveAll("MEdit");
		
		UDebug.print( "Edit ID: "+ uris.get(0) +"\n\n" , 2);
		
		MEdit edit = (MEdit) ffactory.retrieveByUri(uris.get(0), graphUri, 0, "MEdit");
		
		UDebug.print(edit.toString(""),1);   	  
	}
	
	public void printEditInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/edits.rdf#wayEdit5105728_5.0";
		
		UDebug.print( "Edit URI: "+ id +"\n\n" , 2);
		
		MEdit edit = (MEdit) ffactory.retrieveByUri(id, graphUri, 1, "MEdit");
		
		UDebug.print(edit.toString(""),1);   	  
	}
	
	public void printFeatureInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/ways.rdf#way4271855";
		
		MFeature feature = (MFeature) ffactory.retrieveByUri(id, graphUri, 0, "MFeature");
		
		UDebug.print(feature.toString(""),1);   	  
	}
	
	public void printReputationInfos()
	{
		modules.tandr.foundation.FTandrFacade ffactory = new FTandrFacade();
		String id = "http://semantic.web/data/hvgi/author.rdf#author303158";

		VReputationTandr view = new VReputationTandr(); 		
		
		MAuthor author = (MAuthor) ffactory.retrieveByUri( id, graphUri, 0, "MAuthor");
		MReputationTandr rep = (MReputationTandr) ffactory.retrieveByUri( author.getReputationUri(), graphUri, 0, "MReputationTandr");
		author.setReputation(rep);
		
		UDebug.print(author.getReputationUri() + "\n\n\n" + view.getReputationString(author),1);  
	}
	
	public void printFeatureVersionInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
//		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion199732_1";
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		
		UDebug.print(fversion.toString(""),1);   	  
	}
	
	public void printQuery_1()
	{
		boolean graphUri = true;
		String authorUri = "author0";
		String effect = "effect";
		String aspect = "aspect";
		
		String queryString = ""
				+ "\tSELECT DISTINCT ?fvUri ?aspectValue ?aspectComputedAt  \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (graphUri) queryString += "\t GRAPH graphs:hvgi \n\t {\n";
		queryString += ""
				+ "\t\t ?fvUri dcterms:contributor <"+authorUri+"> .\n";
		if (graphUri) queryString += "\t }\n";
		
		queryString += "\t\t \n";
		
		if (graphUri) queryString += "\t GRAPH graphs:tandr \n\t {\n";
		queryString += ""
					+ "\t\t ?trust tandr:refersToFeatureVersion     ?fvUri  .\n"
					+ "\t\t ?trust tandr:hasTrustworthinessEffect ?effect   .\n"
				    + "\t\t         ?effect       tandr:hasEffectDescription  ?EDescription                .\n"
					+ "\t\t         ?EDescription tandr:effectNameIs          \""+ effect +"\"^^xsd:string .\n"
					+ "\t\t \n"
					+ "\t\t         ?effect       tandr:hasTrustworthinessAspect ?aspect                      .\n"
					+ "\t\t         ?aspect       tandr:hasAspectDescription     ?ADescription                .\n"
					+ "\t\t         ?ADescription tandr:aspectNameIs             \""+ aspect +"\"^^xsd:string .\n"
					+ "\t\t         ?aspect       tandr:hasAspectValue           ?AValue                      .\n"
					+ "\t\t         ?AValue       tandr:aspectValueIs            ?aspectValue                 .\n"
					+ "\t\t         { \n"
					+ "\t\t             SELECT  ?aspectValue (MAX(?aspectTimeStamp) AS ?aspectComputedAt)\n"
					+ "\t\t             WHERE { \n" ;
					if (graphUri) queryString += "\t\t              GRAPH graphs:tandr {\n";
		queryString += ""
					+ "\t\t  		?AValue       tandr:computedAt               ?aspectTimeStamp            .\n";
					if (graphUri) queryString += "\t\t              }\n";
		queryString += ""
					+ "\t\t              FILTER( ?aspectTimeStamp  < \""+UConfig.getMaxDateTimeAsString()+"\"^^xsd:dateTime )      \n"
					+ "\t\t             }\n"
					+ "\t\t         } \n"
					;
		if (graphUri) queryString += "\t }\n";
		queryString += ""
					+ "\t FILTER( !isblank(?aspectComputedAt) )  \n"
					+ "\t}";

		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 1);
	}
	
	public void printQuery_2()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		
//		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion14955295_21.10";
		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion27935560_8.0";
		MFeatureVersion featureVersion = (MFeatureVersion) ffactory.retrieveByUri(id, UConfig.getVGIHGraphURI(), 0, "MFeatureVersion");

//		String fv_dateFrom = featureVersion.getIsValidFromString();
//		String fv_dateTo = featureVersion.getIsValidToString();		
		String fv_dateFrom = "2009-01-12T15:59:30Z";
		String fv_dateTo = "2009-02-13T15:59:51Z";
//		String fv_dateFrom = UConfig.getMinDateTimeAsString();
//		String fv_dateTo = UConfig.getMaxDateTimeAsString();
		double radius = 50.0;
		

		
		String fv_wkt_buffered = featureVersion.getGeometryBuffer(radius);
		
		String queryString = ""
				+ "\tSELECT ?uri \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				// Join on TEMPORAL subgraph
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid       . \n"
				+ "  			  ?valid     hvgi:validFrom      _:timeFrom   . \n"
				+ "	     		  _:timeFrom time:inXSDDateTime  ?dateFrom      \n"		
				+ "\t\t\t} \n"
				// Join on SPATIAL subgraph
				+ "\t\tOPTIONAL { ?uri      geosparql:hasGeometry    _:geom       . \n"
				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n";
				
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		// TODO: manage if dateTo do not exists
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t \"" + fv_dateFrom + "\"^^xsd:dateTime < ?dateFrom  &&  			 \n"
				+ "\t\t\t?dateFrom < \"" + fv_dateTo + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				+ "\tORDER BY DESC(?dateFrom) 										         \n"
//				+ "\tLIMIT 10 										                         \n"
				;

		UDebug.print("SPARQL query: \n" + printPrefixes(queryString) + "\n\n", 1);
	}
	
	public void printQuery_3()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion14955295_21.10";
		MFeatureVersion featureVersion = (MFeatureVersion) ffactory.retrieveByUri(id, UConfig.getVGIHGraphURI(), 0, "MFeatureVersion");

		String fv_dateFrom = featureVersion.getIsValidFromString();
//		String fv_dateTo = featureVersion.getIsValidToString();		
//		String fv_dateFrom = "2009-01-12T15:59:30Z";
//		String fv_dateTo = "2009-02-13T15:59:51Z";
//		String fv_dateFrom = UConfig.getMinDateTimeAsString();
//		String fv_dateTo = UConfig.getMaxDateTimeAsString();
		double radius = 50.0;
		
		String fv_wkt_buffered = featureVersion.getGeometryBuffer(radius);
		
		String queryString = ""
				+ "\tSELECT ?uri ?dateFrom ?dateTo ?wktString \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				// Join on TEMPORAL subgraph
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid               . \n"
				+ "  			  OPTIONAL {?valid     hvgi:validFrom      _:timeFrom . \n"
				+ "	     		            _:timeFrom time:inXSDDateTime  ?dateFrom  } \n"
				+ "  			  OPTIONAL {?valid     hvgi:validTo        _:timeTo   . \n"
				+ "	     		            _:timeTo   time:inXSDDateTime  ?dateTo    } \n"				
				+ "\t\t\t} \n"
				// Join on SPATIAL subgraph
				+ "\t\tOPTIONAL { ?uri      geosparql:hasGeometry    _:geom       . \n"
				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n";
				
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		// TODO: manage if dateTo do not exists
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t?dateFrom < \"" + fv_dateFrom + "\"^^xsd:dateTime  &&  			 \n"
				+ "\t\t\t?dateTo > \"" + fv_dateFrom + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				+ "\tORDER BY DESC(?dateFrom) 										         \n"
//				+ "\tLIMIT 10 										                         \n"
				;

		UDebug.print("SPARQL query: \n" + printPrefixes(queryString) + "\n\n", 1);
	}
	
	public void printQuery_4()
	{
		String trustworthinessUri = "http://parliament.semwebcentral.org/parliament#Trustworthiness_tandr_1_1";
		
		String graphUri = UConfig.getTANDRGraphURI();
		
		String queryString = ""
				+ "SELECT \n"
				+ "#Trustworthiness Info \n"
				+ " ?tUri ?fvUri (str(?computedAt) AS ?timeStamp) (str(?trustworthinessValue) AS ?TrustValue)\n"
				+ "# Effects Values \n"
				+ " (str(?directEffectValue) AS ?DirValue) (str(?inirectEffectValue) AS ?IndValue) (str(?temporalEffectValue) AS ?TempValue)\n"
				+ "#Direct Aspects Values\n"
				+ " (str(?dirGeomAspectValue) AS ?GeomDirValue) (str(?dirQualAspectValue) AS ?QualDirValue) (str(?dirSemAspectValue) AS ?SemDirValue)\n"
				+ "#Indirect Aspect Values\n"
				+ " (str(?indGeomAspectValue) AS ?GeomIndValue) (str(?indQualAspectValue) AS ?QualIndValue) (str(?indSemAspectValue) AS ?SemIndValue)\n"
				+ "\n"
				+ "WHERE \n"
				+ "{ \n";
				
		if (!graphUri.equals("")) queryString += " GRAPH " +graphUri+ "\n {\n";
		
		// General T info
		queryString += ""
				+ "\t ?tUri tandr:hasTrustworthinessValue ?value                 .\n"
				+ "\t ?tUri tandr:refersToFeatureVersion ?fvUri                  .\n"
				+ "\t ?value tandr:trustworthinessValueIs  ?trustworthinessValue .\n"
				+ "\t ?value tandr:computedAt              ?computedAt           .\n"
				+ "\n" 
				;
		
		// Effect Values
		queryString += ""
				+ "\t ?tUri          tandr:hasTrustworthinessEffect       ?dirEffect     .\n"
			    + "\t ?dirEffect      tandr:hasEffectDescription  <http://parliament.semwebcentral.org/parliament#tandrEffectDirect> .\n"
				+ "\t ?dirEffect      tandr:hasEffectValue        ?dirEffectValue         .\n"
				+ "\t ?dirEffectValue tandr:effectValueIs         ?directEffectValue      .\n"
				+ "\t ?dirEffectValue tandr:computedAt            ?computedAt .\n"
				+ "\n";
				
		queryString += ""
				+ "\t ?tUri          tandr:hasTrustworthinessEffect  ?indEffect            .\n"
			    + "\t ?indEffect      tandr:hasEffectDescription  <http://parliament.semwebcentral.org/parliament#tandrEffectIndirect>  .\n"
				+ "\t ?indEffect      tandr:hasEffectValue        ?indEffectValue           .\n"
				+ "\t ?indEffectValue tandr:effectValueIs         ?indirectEffectValue      .\n"
				+ "\t ?indEffectValue tandr:computedAt            ?computedAt .\n"
				+ "\n";
				
		queryString += ""
				+ "\t ?tUri           tandr:hasTrustworthinessEffect ?tempEffect           .\n"
				+ "\t ?tempEffect      tandr:hasEffectDescription <http://parliament.semwebcentral.org/parliament#tandrEffectTemporal>  .\n"
				+ "\t ?tempEffect      tandr:hasEffectValue       ?tempEffectValue          .\n"
				+ "\t ?tempEffectValue tandr:effectValueIs        ?temporalEffectValue      .\n"
				+ "\t ?tempEffectValue tandr:computedAt           ?computedAt .\n"
				+ "\n"
				;
				
		// Direct Aspect Values
		queryString += ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirGeomAspect      .\n"
				+ "\t\t ?dirGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomDir> .\n"	
				+ "\t\t ?dirGeomAspect      tandr:hasAspectValue           ?dirGeomValue       .\n"
				+ "\t\t ?dirGeomValue       tandr:aspectValueIs            ?dirGeomAspectValue .\n"
				+ "\t\t ?dirGeomValue       tandr:computedAt               ?computedAt  .\n"
				+ "\t }\n"
				+ ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirQualAspect      .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualDir> .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectValue           ?dirQualValue       .\n"
				+ "\t\t ?dirQualValue       tandr:aspectValueIs            ?dirQualAspectValue .\n"
				+ "\t\t ?dirQualValue       tandr:computedAt               ?computedAt  .\n"
				+ "\t }\n"
				+ ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect         tandr:hasTrustworthinessAspect ?dirSemAspect      .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectSemDir> .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectValue           ?dirSemValue       .\n"
				+ "\t\t ?dirSemValue       tandr:aspectValueIs            ?dirSemAspectValue .\n"
				+ "\t\t ?dirSemValue       tandr:computedAt               ?computedAt  .\n"
				+ "\t }\n"
				+ "";
		
		// Indirect Aspect Values
		queryString += ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indGeomAspect                   .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomInd> .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectValue           ?indGeomValue                    .\n"
				+ "\t\t ?indGeomValue       tandr:aspectValueIs            ?indGeomAspectValue              .\n"
				+ "\t\t ?indGeomValue       tandr:computedAt               ?computedAt               .\n"
				+ "\t }\n"
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indQualAspect                     .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualInd> .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectValue           ?indQualValue                      .\n"
				+ "\t\t ?indQualValue       tandr:aspectValueIs            ?indQualAspectValue                .\n"
				+ "\t\t ?indQualValue       tandr:computedAt               ?computedAt                 .\n"
				+ "\t }\n"
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect         tandr:hasTrustworthinessAspect ?indSemAspect                   .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectDescrDirValueasdasdasdasdasdiption     <http://parliament.semwebcentral.org/parliament#tandrAspectSemInd> .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectValue           ?indSemValue                    .\n"
				+ "\t\t ?indSemValue       tandr:aspectValueIs            ?indSemAspectValue              .\n"
				+ "\t\t ?indSemValue       tandr:computedAt               ?computedAt               .\n"
				+ "\t }\n";
		
		if (!graphUri.equals("")) queryString += " }\n";
		
		queryString += ""
				+ " FILTER (?tUri = <"+trustworthinessUri+">)"
				+ "\n"
				;
		
		queryString += ""
				+ "}"
				+ "\nORDER BY DESC(?computedAt) \n"
				+ "LIMIT 1 \n";	

		UDebug.print("SPARQL query: \n" + printPrefixes(queryString) + "\n\n", 1);
	}
	
	public void printQuery_5()
	{
		boolean graphUri = true;
		String authorUri = "http://semantic.web/data/hvgi/author.rdf#author2";
		String untilDate = "2012-01-01T06:00:00Z";
		
		String queryString = ""
				+ "\tSELECT \n"
				+ "\t#Reputation Info \n"
				+ "\t (( <"+ authorUri +">) AS ?author) (STR(AVG(?trustworthinessValue)) AS ?repuValue)\n"
				+ "\t# Effects Values \n"
				+ "\t (STR(AVG(?directEffectValue)) AS ?directRepValue) (STR(AVG(?indirectEffectValue)) AS ?indirectRepValue) (STR(AVG(?temporalEffectValue)) AS ?temporalRepValue)\n"
				+ "\t#Direct Aspects Values\n"
				+ "\t (STR(AVG(?dirGeomAspectValue)) AS ?DirGeomRepValue) (STR(AVG(?dirQualAspectValue)) AS ?DirQualRepValue) (STR(AVG(?dirSemAspectValue)) AS ?DirSemRepValue)\n"
				+ "\t#Indirect Aspect Values\n"
				+ "\t (STR(AVG(?indGeomAspectValue)) AS ?IndGeomRepValue) (STR(AVG(?indQualAspectValue)) AS ?IndQualRepValue) (STR(AVG(?indSemAspectValue)) AS ?IndSemRepValue)\n"
				+ "\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (graphUri) queryString += "\t GRAPH " +UConfig.getVGIHGraphURI()+ "\n\t {\n";
		
		queryString += ""
				+ "\t  ?fvUri dcterms:contributor <"+ authorUri +"> .\n"
				;
		if (graphUri) queryString += "\t }\n";
		
		//***//
				
		if (graphUri) queryString += "\t GRAPH " +UConfig.getTANDRGraphURI()+ "\n\t {\n";
		
		queryString += ""
				+ "\t  ?tUri  tandr:refersToFeatureVersion  ?fvUri                .\n"
				+ "\t  ?tUri  tandr:hasTrustworthinessValue ?value                .\n"
				+ "\t  ?value tandr:trustworthinessValueIs  ?trustworthinessValue .\n"
				+ "\t  ?value tandr:computedAt              ?computedAt           .\n"
				+ "\n" 
				;
		
		queryString += ""
				+ "\t  ?tUri           tandr:hasTrustworthinessEffect ?dirEffect  .\n"
			    + "\t  ?dirEffect      tandr:hasEffectDescription     <http://parliament.semwebcentral.org/parliament#tandrEffectDirect> .\n"
				+ "\t  ?dirEffect      tandr:hasEffectValue           ?dirEffectValue     .\n"
				+ "\t  ?dirEffectValue tandr:effectValueIs            ?directEffectValue  .\n"
				+ "\t  ?dirEffectValue tandr:computedAt               ?computedAt .\n"
				+ "\n"
				+ "\t  ?tUri           tandr:hasTrustworthinessEffect  ?indEffect        .\n"
			    + "\t  ?indEffect      tandr:hasEffectDescription      <http://parliament.semwebcentral.org/parliament#tandrEffectIndirect>  .\n"
				+ "\t  ?indEffect      tandr:hasEffectValue            ?indEffectValue      .\n"
				+ "\t  ?indEffectValue tandr:effectValueIs             ?indirectEffectValue .\n"
				+ "\t  ?indEffectValue tandr:computedAt                ?computedAt          .\n"
				+ "\n"
				+ "\t  ?tUri            tandr:hasTrustworthinessEffect ?tempEffect       .\n"
				+ "\t  ?tempEffect      tandr:hasEffectDescription     <http://parliament.semwebcentral.org/parliament#tandrEffectTemporal>  .\n"
				+ "\t  ?tempEffect      tandr:hasEffectValue           ?tempEffectValue     .\n"
				+ "\t  ?tempEffectValue tandr:effectValueIs            ?temporalEffectValue .\n"
				+ "\t  ?tempEffectValue tandr:computedAt               ?computedAt          .\n"
				+ "\n"
				;
		
		queryString += ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirGeomAspect      .\n"
				+ "\t\t ?dirGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomDir> .\n"	
				+ "\t\t ?dirGeomAspect      tandr:hasAspectValue           ?dirGeomValue       .\n"
				+ "\t\t ?dirGeomValue       tandr:aspectValueIs            ?dirGeomAspectValue .\n"
				+ "\t\t ?dirGeomValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirQualAspect      .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualDir> .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectValue           ?dirQualValue       .\n"
				+ "\t\t ?dirQualValue       tandr:aspectValueIs            ?dirQualAspectValue .\n"
				+ "\t\t ?dirQualValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?dirEffect         tandr:hasTrustworthinessAspect ?dirSemAspect      .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectSemDir> .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectValue           ?dirSemValue       .\n"
				+ "\t\t ?dirSemValue       tandr:aspectValueIs            ?dirSemAspectValue .\n"
				+ "\t\t ?dirSemValue       tandr:computedAt               ?computedAt        .\n"
				+ "\t  }\n"
				+ "";
		
		queryString += ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indGeomAspect      .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomInd> .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectValue           ?indGeomValue       .\n"
				+ "\t\t ?indGeomValue       tandr:aspectValueIs            ?indGeomAspectValue .\n"
				+ "\t\t ?indGeomValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indQualAspect      .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualInd> .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectValue           ?indQualValue       .\n"
				+ "\t\t ?indQualValue       tandr:aspectValueIs            ?indQualAspectValue .\n"
				+ "\t\t ?indQualValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?indEffect         tandr:hasTrustworthinessAspect ?indSemAspect      .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectSemInd> .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectValue           ?indSemValue       .\n"
				+ "\t\t ?indSemValue       tandr:aspectValueIs            ?indSemAspectValue .\n"
				+ "\t\t ?indSemValue       tandr:computedAt               ?computedAt        .\n"
				+ "\t  }\n";
		
		queryString += ""
				+ "\t  { \n"
				+ "\t   SELECT (MAX(?aspectTimeStamp) AS ?computedAt)\n"
				+ "\t   WHERE \n"
				+ "\t   { \n";
		
		if (graphUri) queryString += "\t    GRAPH " +UConfig.getTANDRGraphURI()+ "\n\t   {\n";
		
		queryString += ""
				+ "\t     ?tUri1  tandr:refersToFeatureVersion  ?fvUri .\n"
				+ "\t     ?tUri1  tandr:hasTrustworthinessValue ?value1 .\n"
				+ "\t     ?value1 tandr:trustworthinessValueIs  ?trustworthinessValue1 .\n"
				+ "\t     ?value1 tandr:computedAt  ?aspectTimeStamp  .\n"
				;
		
		if (graphUri) queryString += "\t    }\n";
		
		queryString += "\t    FILTER( ?aspectTimeStamp  \""+untilDate+"\"^^xsd:dateTime  )";
		
		queryString += ""
				+ "\n\t   } \n"
				+ "\t  } \n"
				;
		
		if (graphUri) queryString += "\t }\n";
		
		queryString += ""
				+ "\t} \n"
				;
		
		UDebug.print("SPARQL query: \n" + printPrefixes(queryString) + "\n\n", 1);
	}
	
	
	public void printTrustworthiness()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
//		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
//		
		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion14955295_21.10";
		
		
		MFeatureVersion featureVersion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		UDebug.print(featureVersion.toString(""),3); 
		
		CTRCalculus controller = new CTRCalculus();
		controller.compute(featureVersion);
	}
	
//	public void retreiveNextFV()
//	{
//		FFoundationFacade ffactory = new FFoundationFacade();
//		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
//		
//		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
//		
//		UDebug.print(fversion.toString(""),2);
//		
//		UDebug.print("\n\nDATA ESTRATTA: " + fversion.getIsValidFromString() + "\n\n",2);  
//		
//		String nextUri = ffactory.retrieveNextFVUri( fversion, graphUri );
//		
//		UDebug.print("Prossima feature version: \n\t" + nextUri , 2);
//	}
	
	public void retreiveSuccNeighboursFVs()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(  ), Integer.parseInt(UConfig.rdf_epsg_crs));
		
		Double radius = UConfig.featureInfluenceRadius;
		String wktGeometryBuffered = null;
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
//		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion8179208_3.0";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id,1,"MFeatureVersion");
		
		UDebug.print(fversion.toString(""),2);
		
		WKTReader reader = new WKTReader( geometryFactory );
		Geometry geometryBuffered;
		try {
			geometryBuffered = ((Geometry) reader.read(fversion.getWktGeometry())).buffer( radius );
			wktGeometryBuffered = geometryBuffered.toText();
		} catch (ParseException e) {
			wktGeometryBuffered = null;
			e.printStackTrace();
		}
		
		ArrayList<String> uris = ffactory.retrieveFVPreviousesNeighbours(fversion, wktGeometryBuffered);
		
		for (String uri : uris)
			UDebug.print("\n\t feature version: \t" + uri , 2);
	}	
	
	public void printCleanedPreviousVersions() {
		int dbgLevel = 1;
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion8043973_27.1";
//		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion8179208_3.0";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id,UConfig.getVGIHGraphURI(),1,"MFeatureVersion");
		MFeature feat = fversion.getFeature();
		

		
		ArrayList<MFeatureVersion> fvs = feat.getCleanedPreviousVersions(fversion, 0);
		
		UDebug.print("Number of fvs retrieved: " + fvs.size() + " for...\n",dbgLevel+2);
		UDebug.print("  feature version "+ fversion.getUriID() +"",dbgLevel+2);
		UDebug.print("\n  author "+ fversion.getAuthor().getAccountName() +"\n",dbgLevel+2);
		UDebug.print("\n\n",dbgLevel+2);
		
		for(MFeatureVersion fv : fvs) {
			UDebug.print("\t * feature version "+ fv.getUriID() +"",dbgLevel+2);
			UDebug.print("\n\t * author "+ fv.getAuthor().getAccountName() +"\n",dbgLevel+2);
			UDebug.print("\n",dbgLevel+2);
		}
	}
	
	private String printPrefixes(String query) {
		String prefixes = "\n";
		
		for (Entry<String, Namespace> namespace : UConfig.namespaces.entrySet())
			prefixes += "PREFIX " + namespace.getKey() + ": \t\t <" + namespace.getValue().getURI() +">\n" ;
		
		UDebug.print(prefixes, 6);
		
		query = prefixes + "\n" + query; 
		
		return query;
	}
	

	
}
