import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

public class DA_Schiper_Eggli_Sandoz_main {

    final static Logger logger = Logger.getLogger(DA_Schiper_Eggli_Sandoz_main.class);
    private static String prefix = "rmi://";

    public static String[] readConfiguration(){
        // initialize node property
        PropertiesConfiguration config = new PropertiesConfiguration();
        try{
            config.read(new FileReader("src/main/resources/url.properties"));
        }catch(IOException e1){
            logger.error("Failed to read configurations. Throw by IOException");
            e1.printStackTrace();
        }catch (ConfigurationException e2){
            logger.error("Failed to read configurations. Throw by ConfigurationException");
            e2.printStackTrace();
        }

        return config.getStringArray("node_url");
    }

    /**
     * Initialize three local processes.
     */
    public static void start() {
        String[] urls = readConfiguration();

        List<DA_Schiper_Eggli_Sandoz_RMI> processes = new ArrayList<DA_Schiper_Eggli_Sandoz_RMI>();
        int index = 0;

        try {
            for (String url : urls) {
                DA_Schiper_Eggli_Sandoz process;

                if(isLocalProcess(url)){
                    process = new DA_Schiper_Eggli_Sandoz(urls.length, index);
                    new Thread(process).start();
                    Naming.bind(url, process);
                }else
                    process = (DA_Schiper_Eggli_Sandoz)Naming.lookup(urls[index]);

                processes.add(process);
            }
        }catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (AlreadyBoundException e2) {
            e2.printStackTrace();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (NotBoundException e4){
            e4.printStackTrace();
        }
    }

    public static boolean isLocalProcess(String url){

        if(url.startsWith(prefix + "localhost"))
            return true;
        else if(url.startsWith(prefix + "127.0.0.1"))
            return true;
        else
            return false;
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

        localStart(0);
    }

    /**
     * Initialize one local process.
     * @param index the index of process in configuration file
     */
    public static void localStart(int index) {
        try {
            String[] urls = readConfiguration();

            DA_Schiper_Eggli_Sandoz process = new DA_Schiper_Eggli_Sandoz(urls.length, index);
            new Thread(process).start();
            Naming.bind(urls[index], process);
        } catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (AlreadyBoundException e2) {
            e2.printStackTrace();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        }
    }
}
