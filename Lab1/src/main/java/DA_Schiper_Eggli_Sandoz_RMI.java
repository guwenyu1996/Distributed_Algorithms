import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DA_Schiper_Eggli_Sandoz_RMI extends Remote  {

    public void send(int node, Message message, int delay) throws RemoteException;

    /**
     * Receive a process from a remote process.
     * @param message message to receive
     */
    public void receive(Message message) throws RemoteException;

    public int getIndex();
}
