package java;

import java.awt.event.MouseWheelEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface DA_Schiper_Eggli_Sandoz_RMI extends Remote  {

    public void send(int node, Message message) throws RemoteException;

    /**
     * Receive a process from a remote process.
     * @param message message to receive
     */
    public void receive(Message message);

    /**
     * Deliver a process. All the process should be delivered in a casual order.
     * @param message
     */
    public void deliver(Message message);

}
