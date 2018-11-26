import java.rmi.*;
import java.rmi.registry.LocateRegistry;


public class DA_Schiper_Eggli_Sandoz_main {

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

        ProcessManager.startServer(2);
    }


}
