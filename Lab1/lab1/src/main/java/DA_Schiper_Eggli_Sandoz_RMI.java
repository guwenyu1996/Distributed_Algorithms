import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DA_Schiper_Eggli_Sandoz_RMI extends Remote  {

    /**
     *
     * @param node
     * @param message
     * @param delay
     * @throws RemoteException
     */
    void send(int node, Message message, int delay) throws RemoteException;

    /**
     * Receive a process from a remote process. Processes should be delivered in a casual order.
     * @param message message to receive
     */
    void receive(Message message) throws RemoteException;


}
