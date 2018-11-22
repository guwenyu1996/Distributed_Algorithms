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
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.rmi.registry.LocateRegistry;

public class TestCase {
    final static Logger logger = Logger.getLogger(TestCase.class);
    List<DA_Schiper_Eggli_Sandoz> processes = new ArrayList<DA_Schiper_Eggli_Sandoz>();

//    @Before
//    @Ignore
//    public void Initialize(){
//
//        File directory = new File("");
//        System.out.println(directory.getAbsolutePath());
//
//        // initialize node property
//        PropertiesConfiguration config = new PropertiesConfiguration();
//        try{
//            config.read(new FileReader("./src/main/resources/url.properties"));
//        }catch(IOException e1){
//            logger.error("Failed to read configurations. Throw by IOException");
//            e1.printStackTrace();
//        }catch (ConfigurationException e2){
//            logger.error("Failed to read configurations. Throw by ConfigurationException");
//            e2.printStackTrace();
//        }
//
//        String[] urls = config.getStringArray("node_url");
//        int index = 0;
//
//        try{
//            LocateRegistry.createRegistry(1099);
//
//            DA_Schiper_Eggli_Sandoz process;
//
//            for(String url: urls){
//                if(ProcessManager.isLocalProcess(url)){
//                    process = new DA_Schiper_Eggli_Sandoz(urls.length, index);
//                    new Thread(process).start();
//                    Naming.bind(url, process);
//                }else
//                    process = (DA_Schiper_Eggli_Sandoz)Naming.lookup(urls[index]);
//
//                processes.add(process);
//
//                index ++;
//            }
//
//        }catch (RemoteException e1) {
//            e1.printStackTrace();
//        } catch (AlreadyBoundException e2) {
//            e2.printStackTrace();
//        } catch (MalformedURLException e3) {
//            e3.printStackTrace();
//        } catch (NotBoundException e4){
//            e4.printStackTrace();
//        }
//    }

    @Test
    @Ignore
    public void Test1()  throws RemoteException{
        logger.info("Test 1 starts !");

        Message message1 = new Message(0, 1, 0);
        message1.setContent("This is message 1");

        processes.get(0).send(1, message1);

        Message message2 = new Message(0, 2, 100);
        message2.setContent("This is message 2");

        processes.get(0).send(2, message2);

        Message message3 = new Message(2, 1, 300);
        message3.setContent("This is message 3");

        processes.get(2).send(1, message3);
        logger.warn("Message 3 send");
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    /**
     * P0 sends m1 to P1.
     * P0 sends m2 to P2.
     * P2 sends m3 to P1, which arrives earlier than m1.
     */
    @Test
    @Ignore
    public void Test2()  throws RemoteException{
        logger.info("Test 1 starts !");

        Message message1 = new Message(0, 1, 2000);
        message1.setContent("This is message 1");

        processes.get(0).send(1, message1);

        Message message2 = new Message(0, 2, 0);
        message2.setContent("This is message 2");

        processes.get(0).send(2, message2);

        try{
            Thread.sleep(400);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        Message message3 = new Message(2, 1, 400);
        message3.setContent("This is message 3");

        processes.get(2).send(1, message3);
        logger.warn("Message 3 send");

        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Test
    public void Test3()  throws RemoteException{
        logger.info("Test 3 starts !");
        DA_Schiper_Eggli_Sandoz_RMI process =null;

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        try{
            process = (DA_Schiper_Eggli_Sandoz_RMI)Naming.lookup("rmi://3.16.151.106:1099/SES2");
        }catch (RemoteException e1) {
            e1.printStackTrace();
        }catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (NotBoundException e4){
            e4.printStackTrace();
        }
        process.test();

    }

}
