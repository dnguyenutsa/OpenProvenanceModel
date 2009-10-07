package org.openprovenance.model.extension;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;

import org.openprovenance.model.OPMFactory;
import org.openprovenance.model.ArtifactRef;
import org.openprovenance.model.Artifact;
import org.openprovenance.model.ProcessRef;
import org.openprovenance.model.Process;
import org.openprovenance.model.AgentRef;
import org.openprovenance.model.Agent;
import org.openprovenance.model.AccountRef;
import org.openprovenance.model.Role;
import org.openprovenance.model.WasDerivedFrom;
import org.openprovenance.model.Account;
// import org.openprovenance.model.ArtifactExt;
// import org.openprovenance.model.NamedWasDerivedFrom;
// import org.openprovenance.model.NamedWasControlledBy;

/** An extended Factory of OPM objects with NamedWasDerivedFrom edges.
    Needs to be moved in a separate module probably, since it is an extension*/

public class OPMExtendedFactory extends OPMFactory {

    public static final String packageList=
        "org.openprovenance.model:org.openprovenance.model.extension";

    public String getPackageList() {
        return packageList;
    }


    private final static OPMFactory oFactory=new OPMExtendedFactory();

    protected ObjectFactory extof=new ObjectFactory();

    public static OPMFactory getFactory() {
        return oFactory;
    }


    public NamedWasDerivedFrom newNamedWasDerivedFrom(ArtifactRef aid1,
                                                      ArtifactRef aid2,
                                                      String type,
                                                      Collection<AccountRef> accounts) {
        NamedWasDerivedFrom res=extof.createNamedWasDerivedFrom();
        res.setCause(aid2);
        res.setEffect(aid1);
        res.setType(type);
        if ((accounts !=null) && (accounts.size()!=0)) {
            res.getAccount().addAll(accounts);
        }

        return res;
    }




    public NamedWasDerivedFrom newNamedWasDerivedFrom(Artifact a1,
                                                      Artifact a2,
                                                      String type,
                                                      Collection<Account> accounts) {
        ArtifactRef aid1=newArtifactRef(a1);
        ArtifactRef aid2=newArtifactRef(a2);
        LinkedList ll=new LinkedList();
        for (Account acc: accounts) {
            ll.add(newAccountRef(acc));
        }
        return  newNamedWasDerivedFrom(aid1,aid2,type,ll);
    }



    public NamedWasControlledBy newNamedWasControlledBy(ProcessRef pid,
                                                        Role role,
                                                        AgentRef agid,
                                                        String type,
                                                        Collection<AccountRef> accounts) {
        NamedWasControlledBy res=extof.createNamedWasControlledBy();
        res.setCause(agid);
        res.setEffect(pid);
        res.setRole(role);
        res.setType(type);
        if ((accounts !=null) && (accounts.size()!=0)) {
            res.getAccount().addAll(accounts);
        }
        return res;
    }




    public NamedWasControlledBy newNamedWasControlledBy(Process p,
                                                        Role role,
                                                        Agent ag,
                                                        String type,
                                                        Collection<Account> accounts) {
        ProcessRef pid=newProcessRef(p);
        AgentRef agid=newAgentRef(ag);
        LinkedList ll=new LinkedList();
        for (Account acc: accounts) {
            ll.add(newAccountRef(acc));
        }
        return  newNamedWasControlledBy(pid,role, agid,type,ll);
    }


    public NamedWasDerivedFrom newNamedWasDerivedFrom(NamedWasDerivedFrom g) {
        return newNamedWasDerivedFrom(g.getEffect(),
                                      g.getCause(),
                                      g.getType(),
                                      g.getAccount());
    }

    public WasDerivedFrom newWasDerivedFrom(WasDerivedFrom g) {
        if (g instanceof NamedWasDerivedFrom) {
            return newNamedWasDerivedFrom((NamedWasDerivedFrom) g);
        } else {
            return newWasDerivedFrom(g.getEffect(),
                                     g.getCause(),
                                     g.getAccount());
        }
    }

    public ArtifactExt newArtifactExt(String id,
                                      Collection<Account> accounts,
                                      Object value,
                                      List<Object> any) {
        ArtifactExt res=extof.createArtifactExt();
        res.setId(id);
        if ((accounts !=null) && (accounts.size()!=0)) {
            LinkedList ll=new LinkedList();
            for (Account acc: accounts) {
                ll.add(newAccountRef(acc));
            }
            res.getAccount().addAll(ll);
        }
        res.setValue(value);
        if (any!=null) {
            res.getAny().addAll(any);
        }
        return res;
    }


            
}

