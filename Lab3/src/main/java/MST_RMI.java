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
    void receive_initiate(int src, int level, int fragment_name, State_node s) throws RemoteException;

    void receive_test(int src, int level, int fragment_name) throws RemoteException;

    /**
     * receive a accept message
     */
    void receive_accept(int src) throws RemoteException;

    void receive_reject(int src) throws RemoteException;

    void receive_report (int src, int weight) throws RemoteException;

    void receive_change_root() throws RemoteException;

    void receive_connect(int src, int level) throws RemoteException;

    void start() throws RemoteException;

    void receive_print(int src) throws RemoteException;

    State_node getSN()throws RemoteException;

    void test_print() throws RemoteException;
}
