import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface MST_RMI extends Remote{


    void construct_key(Map<Integer, NeighbourNode> nn) throws RemoteException;
    /**
     * receive a initial message, start finding the MOE
     * @param src index of the source
     * @param level level of the sender
     * @param fragment_name fracment name of the sender
     * @param s state of the sender
     */

    /**
     * receive a accept message
     */

    void receive_message(Message msg)  throws RemoteException;

    void start() throws RemoteException;

    void receive_print(int src) throws RemoteException;

    State_node getSN() throws RemoteException;

    int getIn_branch() throws RemoteException;

    void reset() throws RemoteException;

    ReturnMessage getStatistic()throws RemoteException;
}
