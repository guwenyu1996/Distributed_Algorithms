import java.awt.event.MouseWheelEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface Schiper_Eggli_Sandoz_interface extends Remote  {

    public void send(int node, Message message) throws RemoteException;

    public void receive(Message message) throws RemoteException;

}
