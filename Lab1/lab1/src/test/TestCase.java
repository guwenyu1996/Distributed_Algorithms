
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

public class TestCase {
    final static Logger logger = Logger.getLogger(TestCase.class);
    List<DA_Schiper_Eggli_Sandoz> processes = new ArrayList<DA_Schiper_Eggli_Sandoz>();

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

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


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

    @Test
    public void Test1()  throws RemoteException{
        logger.info("Test 1 starts !");

        Message message1 = new Message(0, 1, 10000);
        message1.setContent("This is message 1");

        processes.get(0).send(1, message1);
        logger.warn("Message 1 send");

//        Message message2 = new Message(0, 2, 100);
//        message2.setContent("This is message 2");
//
//        processes.get(0).send(2, message2);

        Message message3 = new Message(2, 1, 300);
        message3.setContent("This is message 3");

        processes.get(2).send(1, message3);
        logger.warn("Message 3 send");

    }

}
