import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class ProcessManager {

    final static Logger logger = Logger.getLogger(ProcessManager.class);
    private static String prefix = "rmi://";

    public static String[] readConfiguration(){
        // initialize node property
        PropertiesConfiguration config = new PropertiesConfiguration();
        try{
            config.read(new FileReader("url.properties"));
        }catch(IOException e1){
            logger.error("Failed to read configurations. Throw by IOException");
            e1.printStackTrace();
        }catch (ConfigurationException e2){
            logger.error("Failed to read configurations. Throw by ConfigurationException");
            e2.printStackTrace();
        }


        String[] urls = config.getStringArray("node_url");
        logger.info("read url : " +  urls[0]);
        return urls;
    }

    /**
     *
     */
    public static void startServer(int index) {
        String[] urls = readConfiguration();

        try {
            Singhal process = new Singhal(urls.length, index);
            logger.info("create server at" + urls[index]);
            new Thread(process).start();
            Naming.bind("rmi://localhost/SES", process);

        }catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (AlreadyBoundException e2) {
            e2.printStackTrace();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        }
    }
}
