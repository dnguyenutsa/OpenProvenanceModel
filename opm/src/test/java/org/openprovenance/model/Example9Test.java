package org.openprovenance.model;
import java.io.File;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openprovenance.model.collections.CollectionFactory;


/**
 * Unit test for simple App.
 */
public class Example9Test 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Example9Test( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */


    static OPMGraph graph1;




    public void testCollectionProposal2() throws Exception 
    {
        OPMFactory oFactory=new OPMFactory();
        CollectionFactory cFactory=new CollectionFactory(oFactory);

        Collection<Account> black=Collections.singleton(oFactory.newAccount("black"));
        Collection<Account> orange=Collections.singleton(oFactory.newAccount("orange"));
        
        List<Account> black_orange=new LinkedList();
        black_orange.addAll(orange);
        black_orange.addAll(black);


        Process p1=oFactory.newProcess("x",
                                       black,
                                       "product");


        Process p2=oFactory.newProcess("map",
                                       black,
                                       "map");


        Process p3=oFactory.newProcess("apply",
                                       black,
                                       "apply");



        Artifact pp=oFactory.newArtifact("pp",
                                         black,
                                         "p");

        Artifact a1=oFactory.newArtifact("a",
                                         black,
                                         "a:[]");
        Artifact ai1=oFactory.newArtifact("a_i1",
                                         black,
                                         "a_i1");

        Artifact ai2=oFactory.newArtifact("a_i2",
                                         black,
                                         "a_i2");


        Artifact b1=oFactory.newArtifact("b",
                                         black,
                                         "b:[]");
        Artifact bj1=oFactory.newArtifact("b_j1",
                                         black,
                                         "b_j1");

        Artifact bj2=oFactory.newArtifact("b_j2",
                                         black,
                                         "b_j2");


        Artifact c1=oFactory.newArtifact("c",
                                         black,
                                         "c1:[]");

        Artifact cij=oFactory.newArtifact("cij1",
                                         black,
                                         "cij1:[]");

        Artifact cij2=oFactory.newArtifact("cij2",
                                         black,
                                         "cij2");

        Artifact c2=oFactory.newArtifact("c2",
                                         black,
                                         "c2:[]");


        Used u1=oFactory.newUsed(p1,oFactory.newRole("left"),a1,black);
        Used u2=oFactory.newUsed(p1,oFactory.newRole("right"),b1,black);


        WasGeneratedBy wg1=oFactory.newWasGeneratedBy(c1,oFactory.newRole("product"),p1,black);


        WasDerivedFrom wd1=oFactory.newWasDerivedFrom("wd1",c1,a1,"wasLeftProduct",black);
        WasDerivedFrom wd2=oFactory.newWasDerivedFrom("wd2",c1,b1,"wasRightProduct",black);
        
        WasDerivedFrom wd3=cFactory.newWasIdenticalTo("wd3",ai2,ai1,orange);
        WasDerivedFrom wd4=cFactory.newWasIdenticalTo("wd4",bj2,bj1,orange);

        WasDerivedFrom wd5=cFactory.newContained("wd5",a1,ai1,orange);
        WasDerivedFrom wd6=cFactory.newContained("wd6",b1,bj1,orange);

        WasDerivedFrom wd7=cFactory.newContained("wd7",cij,ai2,orange);
        WasDerivedFrom wd8=cFactory.newContained("wd8",cij,bj2,orange);

        WasDerivedFrom wd9=cFactory.newContained("wd9",c1,cij,orange);





        Used u3=oFactory.newUsed(p2,oFactory.newRole("collection"),c1,black);
        Used u4=oFactory.newUsed(p2,oFactory.newRole("function"),pp,black);

        WasGeneratedBy wg2=oFactory.newWasGeneratedBy(c2,oFactory.newRole("result"),p2,black);


        Used u5=oFactory.newUsed(p3,oFactory.newRole("left"),ai2,orange);
        Used u6=oFactory.newUsed(p3,oFactory.newRole("right"),bj2,orange);
        Used u7=oFactory.newUsed(p3,oFactory.newRole("fun"),pp,orange);

        WasGeneratedBy wg3=oFactory.newWasGeneratedBy(cij2,oFactory.newRole("result"),p3,orange);

        WasDerivedFrom wd10=cFactory.newContained("wd10",c2,cij2,orange);

        WasDerivedFrom wd11=oFactory.newWasDerivedFrom("wd11",cij2,cij,"wasApplied",orange);
        
        WasDerivedFrom wd12=oFactory.newWasDerivedFrom("wd12",c2,c1,"wasMapped",black);
        

        OPMGraph graph=oFactory.newOPMGraph(black_orange,
                                            new Overlaps[] { },
                                            new Process[] {p1, p2, p3},
                                            new Artifact[] {a1,ai1, ai2, b1, bj1, bj2, c1, cij, cij2, pp, c2},
                                            new Agent[] {  },
                                            new Object[] {u1,u2, u3, u4, u5, u6, u7,
                                                          wg1,wg2,wg3,
                                                          wd1, wd2, wd3, wd4, wd5, wd6, wd7, wd8, wd9, wd10, wd11, wd12} );




        OPMSerialiser serial=OPMSerialiser.getThreadOPMSerialiser();
        serial.serialiseOPMGraph(new File("target/collection2.xml"),graph,true);

        
        //System.out.println(sw);

        graph1=graph;
        System.out.println("testOPM1 asserting True");
        assertTrue( true );


        OPMToDot toDot=new OPMToDot();
        
        toDot.convert(graph1,"target/collection2.dot", "target/collection2.pdf");


        
    }
    
}
