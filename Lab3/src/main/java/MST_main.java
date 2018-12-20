import org.apache.log4j.Logger;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class MST_main {
    final static Logger logger = Logger.getLogger(MST_main.class);
    public static void main(String args[]) {

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        for(int i =0; i < args.length; i++){
            logger.info("args "+ i + " "+ args[i]);
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        int index = Integer.parseInt(args[0]);
        ProcessManager.startServer(index);
    }
}
