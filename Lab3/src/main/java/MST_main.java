import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class MST_main {
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
        int index = Integer.parseInt(args[0]);
        ProcessManager.startServer(index);
    }
}
