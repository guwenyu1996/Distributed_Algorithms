import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;


public class DA_Schiper_Eggli_Sandoz_main {

    final static Logger logger = Logger.getLogger(DA_Schiper_Eggli_Sandoz_main.class);

    public static String[] readConfiguration(){
        // initialize node property
        PropertiesConfiguration config = new PropertiesConfiguration();
        try{
            config.read(new FileReader("./lab1/src/main/resources/url.properties"));
        }catch(IOException e1){
            logger.error("Failed to read configurations. Throw by IOException");
            e1.printStackTrace();
        }catch (ConfigurationException e2){
            logger.error("Failed to read configurations. Throw by ConfigurationException");
            e2.printStackTrace();
        }

        String[] urls = config.getStringArray("node_url");
        return urls;
    }

    public static void start(){

        String[] urls = readConfiguration();

        List<DA_Schiper_Eggli_Sandoz> processes = new ArrayList<DA_Schiper_Eggli_Sandoz>();
        int index = 0;

        try{
            for(String url: urls){
                DA_Schiper_Eggli_Sandoz process = new DA_Schiper_Eggli_Sandoz(urls.length, index);
                new Thread(process).start();
                Naming.bind(url, process);
                processes.add(process);

                index ++;
            }

        }catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (AlreadyBoundException e2) {
            e2.printStackTrace();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        }

    }

    public static void main(String args[]) {

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        start();
    }
}
