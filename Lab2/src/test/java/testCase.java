import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

public class testCase {

    final static Logger logger = Logger.getLogger(testCase.class);
    List<DA_Singhal_RMI> processes = new ArrayList<DA_Singhal_RMI>();


    @Before
    public void Initialize(){
        File directory = new File("");
        System.out.println(directory.getAbsolutePath());

        // initialize node property
        PropertiesConfiguration config = new PropertiesConfiguration();
        try{
            config.read(new FileReader("./src/main/resources/url.properties"));
        }catch(IOException e1){
            logger.error("Failed to read configurations. Throw by IOException");
            e1.printStackTrace();
        }catch (ConfigurationException e2){
            logger.error("Failed to read configurations. Throw by ConfigurationException");
            e2.printStackTrace();
        }

        String[] urls = config.getStringArray("node_url");
        int index = 0;

        try{
            LocateRegistry.createRegistry(1099);

            DA_Singhal_RMI process;

            for(String url: urls){
                process = (DA_Singhal_RMI)Naming.lookup(urls[index]);
                processes.add(process);
                index ++;
            }


        }catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (NotBoundException e4){
            e4.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
    }

    @Test
    @Ignore
    public void test1() throws RemoteException{
        processes.get(0).requestCS();
        processes.get(1).requestCS();
    }

    @Test
    public void test2() throws RemoteException{
        processes.get(0).requestCS();
        processes.get(1).requestCS();
        processes.get(2).requestCS();
        processes.get(3).requestCS();
    }

}
