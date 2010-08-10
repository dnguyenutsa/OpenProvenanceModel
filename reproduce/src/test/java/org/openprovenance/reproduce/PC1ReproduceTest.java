/* This example is inspired by Clark & Parsia example to use Pellet with Jena,
   and by an IBM example to write sparql queries in Jena. */


package org.openprovenance.reproduce;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.openprovenance.model.OPMFactory;
import org.openprovenance.model.Artifact;
import org.openprovenance.model.Account;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Reproducibility of PC1
 */
public class PC1ReproduceTest extends TestCase {

    public OPMFactory oFactory=new OPMFactory();

    public ArtifactFactory aFactory= new ArtifactFactory () {
            public Artifact newArtifact(Artifact a) {
                return a;
            }};


    Utilities u=new Utilities(oFactory);

    public PC1ReproduceTest (String testName) {
        super(testName);
    }

    public void testWithModel1() {
        loadModel();
    }
    
    static Model theModel;

    public static void loadModel() {
        
        // ontologies that will be used
        String ont1 = "file:src/test/resources/opm.owl";
        String ont2 = "file:src/test/resources/primitives3.owl";

        String ns1 = "http://openprovenance.org/ontology#";
        String ns2 = "http://openprovenance.org/primitives#";
        
  	    // create Pellet reasoner
        Reasoner reasoner = PelletReasonerFactory.theInstance().create();
        
        // create an empty model
        Model emptyModel = ModelFactory.createDefaultModel( );
        
        // create an inferencing model using Pellet reasoner
        InfModel model = ModelFactory.createInfModel( reasoner, emptyModel );
            
        // read the files
        model.read( "file:src/test/resources/pc1-full.n3", "N3" );
        model.read( ont1 );
        model.read( ont2 );
        
        // print validation report
        ValidityReport report = model.validate();
        printIterator( report.getReports(), "Validation Results" );
        
        // print superclasses
        Resource c = model.getResource( ns1 + "Artifact" );         
        printIterator(model.listObjectsOfProperty(c, RDFS.subClassOf), "All super classes of " + c.getLocalName());

        c = model.getResource( ns2 + "Integer" );         
        printIterator(model.listObjectsOfProperty(c, RDFS.subClassOf), "All super classes of " + c.getLocalName());
        
        theModel=model;
    }


    
    public static void printIterator(Iterator<?> i, String header) {
        System.out.println(header);
        for(int c = 0; c < header.length(); c++)
            System.out.print("=");
        System.out.println();
        
        if(i.hasNext()) {
	        while (i.hasNext()) 
	            System.out.println( i.next() );
        }       
        else
            System.out.println("<EMPTY>");
        
        System.out.println();
    }

    public void testQuery2() throws java.io.FileNotFoundException, java.io.IOException {
        
        // Create a new query
        String queryString = 
            "PREFIX opm: <http://openprovenance.org/ontology#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT ?proc " +
            "WHERE {" +
            "      ?proc rdf:type opm:Process " +
            "      }";

        runQuery(queryString);
    }


    public void testQuery3() throws java.io.FileNotFoundException, java.io.IOException {
        
        // Create a new query
        String queryString = 
            "PREFIX opm: <http://openprovenance.org/ontology#> " +
            "PREFIX pc1: <http://www.ipaw.info/pc1/>  " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT ?a1 ?a2 " +
            "WHERE {" +
            "      ?a1 opm:_wasDerivedFrom_star ?a2 " +
            "      }";

        runQuery(queryString);
    }

    public void runQuery (String queryString) {
        Query query = QueryFactory.create(queryString);

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, theModel);
        ResultSet results = qe.execSelect();

        // Output query results	
        ResultSetFormatter.out(System.out, results, query);

        // Important - free up resources used running the query
        qe.close();
    }


    public void testQuery4() {

        Queries q=new Queries(theModel);
        q.addPrefixes("pc1","http://www.ipaw.info/pc1/");

        List ll=q.getUsedArtifactsAsResources("pc1:p1");
        System.out.println(" found " + ll);
        q.close();
        assertTrue(ll.size()==4);


        ll=q.getGeneratedArtifactsAsResources("pc1:p2");
        System.out.println(" found " + ll);
        q.close();
        assertTrue(ll.size()==1);
        
    }


    public void testOrderedProcesses() {
        Queries q=new Queries(theModel);
        String pc1NS="http://www.ipaw.info/pc1/";
        q.addPrefixes("pc1",pc1NS);

        List<Resource> results = q.getProcessesAsResources(pc1NS);

        System.out.println("Found Processes " + results);

        q.close();
    }


    public void testInputArtifacts() {
        Queries q=new Queries(theModel);
        String pc1NS="http://www.ipaw.info/pc1/";
        q.addPrefixes("pc1",pc1NS);

        List<Resource> results = q.getInputArtifactsAsResources(pc1NS);

        System.out.println("==> Found Input Artifacts " + results);

        q.close();
    }


    static String PATH_PROPERTY="http://openprovenance.org/primitives#path";
    static String FILE_LOCATION="//home/lavm/papers/papers/opmowl/OpenProvenanceModel/reproduce/src/test/resources/pc1/";
    //static String FILE_LOCATION="../src/test/resources/pc1/";


    static List<Account> black=new LinkedList();

    public void testReproduceP1() throws java.io.IOException, org.jaxen.JaxenException, org.xml.sax.SAXException {
        String p0="http://www.ipaw.info/pc1/p0";
        
        HashMap<String,Artifact> args=new HashMap();

        Artifact a1=oFactory.newArtifact("a1",
                                         black,
                                         "Reference Image");
        oFactory.addAnnotation(a1,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a1,
                               oFactory.newEmbeddedAnnotation("an1_1",
                                                              PATH_PROPERTY,
                                                              FILE_LOCATION + "reference.img",
                                                              null));

        Artifact a3=oFactory.newArtifact("a3",
                                         black,
                                         "Anatomy I1");
        oFactory.addAnnotation(a3,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a3,
                               oFactory.newEmbeddedAnnotation("an1_3",
                                                              PATH_PROPERTY,
                                                              FILE_LOCATION + "anatomy1.img",
                                                              null));


        Artifact a11=oFactory.newArtifact("a11",
                                          black,
                                          "Warp Params1");
        oFactory.addAnnotation(a11,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a11,
                               oFactory.newEmbeddedAnnotation("an1_11",
                                                              PATH_PROPERTY,
                                                              "./params1.warp",
                                                              null));


        Artifact a15=oFactory.newArtifact("a15",
                                         black,
                                         "Resliced I1");
        oFactory.addAnnotation(a15,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a15,
                               oFactory.newEmbeddedAnnotation("an1_15",
                                                              PATH_PROPERTY,
                                                              "./resliced1.img",
                                                              null));


        Artifact a16=oFactory.newArtifact("a16",
                                         black,
                                         "Resliced H1");
        oFactory.addAnnotation(a16,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a16,
                               oFactory.newEmbeddedAnnotation("an1_16",
                                                              PATH_PROPERTY,
                                                              "./resliced1.hdr",
                                                              null));

        Artifact a23=oFactory.newArtifact("a23",
                                          black,
                                          "Atlas Image");

        oFactory.addAnnotation(a23,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a23,
                               oFactory.newEmbeddedAnnotation("an1_23",
                                                              PATH_PROPERTY,
                                                              "./atlas.img",
                                                              null));

        Artifact a24=oFactory.newArtifact("a24",
                                          black,
                                          "Atlas Header");

        oFactory.addAnnotation(a24,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a24,
                               oFactory.newEmbeddedAnnotation("an1_24",
                                                              PATH_PROPERTY,
                                                              "./atlas.hdr",
                                                              null));

        /*

~/Download/AIR5.2.6/bin/reslice params1.warp resliced1
~/Download/AIR5.2.6/bin/softmean atlas.hdr y null resliced1.img 
~/Download/fsl/bin/slicer ./atlas.hdr -x .5 ./sliced.pgm

        */



        Artifact a_1=oFactory.newArtifact("a_1",
                                          black,
                                          "Atlas X Slice");
        oFactory.addAnnotation(a_1,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a_1,
                               oFactory.newEmbeddedAnnotation("an1__1",
                                                              PATH_PROPERTY,
                                                              "./atlas-x.pgm",
                                                              null));

        Artifact a_2=oFactory.newArtifact("a_2",
                                          black,
                                          "Atlas X Graphic");
        oFactory.addAnnotation(a_2,
                               oFactory.newType("http://openprovenance.org/primitives#File"));
        oFactory.addAnnotation(a_2,
                               oFactory.newEmbeddedAnnotation("an1__2",
                                                              PATH_PROPERTY,
                                                              "./atlasX.jpg",
                                                              null));


        args.put("img", a3);
        args.put("imgRef", a1);
        args.put("out", a11);
        Execute exec=new Execute(oFactory,aFactory);
        Document doc=exec.createInvocationDocument("http://openprovenance.org/reproducibility/air#align_warp",args);
        u.serializeToStandardOut(doc.getDocumentElement(), doc);
        u.serialize(doc.getDocumentElement(),doc, new FileOutputStream("target/p0-swift2.xml"));
        exec.invokeSwift("p0-swift2.xml", "p0-swift2.kml");


        HashMap<String,Artifact> args2=new HashMap();
        args2.put("in", a11);
        args2.put("img", a15);
        args2.put("hdr", a16);
        exec=new Execute(oFactory,aFactory);
        doc=exec.createInvocationDocument("http://openprovenance.org/reproducibility/air#reslice",args2);
        u.serializeToStandardOut(doc.getDocumentElement(), doc);
        u.serialize(doc.getDocumentElement(),doc, new FileOutputStream("target/p5-swift2.xml"));
        exec.invokeSwift("p5-swift2.xml", "p5-swift2.kml");



        HashMap<String,Artifact> args3=new HashMap();
        args3.put("i1", a15);
        args3.put("h1", a16);
        args3.put("img", a23);
        args3.put("hdr", a24);
        exec=new Execute(oFactory,aFactory);
        doc=exec.createInvocationDocument("http://openprovenance.org/reproducibility/air#softmean",args3);
        u.serializeToStandardOut(doc.getDocumentElement(), doc);
        u.serialize(doc.getDocumentElement(),doc, new FileOutputStream("target/p9-swift2.xml"));
        exec.invokeSwift("p9-swift2.xml", "p9-swift2.kml");



        HashMap<String,Artifact> args4=new HashMap();
        args4.put("img", a23);
        args4.put("hdr", a24);
        args4.put("pgm", a_1);
        exec=new Execute(oFactory,aFactory);
        doc=exec.createInvocationDocument("http://openprovenance.org/reproducibility/air#slicer",args4);
        u.serializeToStandardOut(doc.getDocumentElement(), doc);
        u.serialize(doc.getDocumentElement(),doc, new FileOutputStream("target/p10-swift2.xml"));
        exec.invokeSwift("p10-swift2.xml", "p10-swift2.kml");



        HashMap<String,Artifact> args5=new HashMap();
        args5.put("img", a_1);
        args5.put("jpg", a_2);
        exec=new Execute(oFactory,aFactory);
        doc=exec.createInvocationDocument("http://openprovenance.org/reproducibility/air#convert",args5);
        u.serializeToStandardOut(doc.getDocumentElement(), doc);
        u.serialize(doc.getDocumentElement(),doc, new FileOutputStream("target/p13-swift2.xml"));
        exec.invokeSwift("p13-swift2.xml", "p13-swift2.kml");

    }

        
    

}
