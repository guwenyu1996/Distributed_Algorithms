import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MST extends UnicastRemoteObject implements MST_RMI, Runnable{

    /**
     * The total number of process in the system
     */
    private int processNum;

    /**
     * The index of current process
     */
    private int index;

    /**
     * level of the current fragment it is part of
     */
    int LN;
    /**
     * name of the current fragment it is part of
     */
    int FN;
    /**
     * state of the node (find/found)
     */
    State_node SN;
    /**
     * index of node on the other side of the edge towards core
     */
    int in_branch;
    /**
     * index of node on the other side of the test edge
     */
    int test_edge;
    /**
     * index of the node on the other side of the best edge, best edge is the local candidate of the MOE
     */
    int best_edge;
    /**
     * weight tof the current candidate MOE
     */
    int bes_weight;
    /**
     * number of report messages expected
     */
    int find_count;


    /**
     *  instead of storing the information of the edges, i prefer storing the
     *  information of the nodes on the other side of the edge
     */
    private Map<Integer, Neighbour_node> nn;

    private Queue<Connect_massage> connect_queue;


    final static Logger logger = Logger.getLogger(MST.class);

    public MST(int processNum, int index)throws RemoteException {
        this.processNum = processNum;
        this.index = index;
        nn = new HashMap<Integer, Neighbour_node>();
        connect_queue = new LinkedList<Connect_massage>();
    }


    /**
     * /unction to inform this node the index of neighbouring node, and the weight of the edge to
     *  this node, thus we can construct a graph, should ONLY be invoked by the test function
     * @param nn
     */
    public void construct_key( Map<Integer, Neighbour_node> nn)  throws RemoteException{
        this.nn = nn;
    }

    /**
     * receive a initial message, start finding the MOE
     * @param src index of the source
     * @param level level of the sender
     * @param fragment_name fracment name of the sender
     * @param s state of the sender
     */
    public  void receive_initiate(int src, int level, int fragment_name, State_node s)  throws RemoteException{

    }

    public void receive_test(int src, int level, int fragment_name){

    }

    /**
     * receive a accept message
     */
    public void receive_accept(int src){

    }

    public void receive_reject(int src){

    }

    public void receive_report (int src, int weight){

    }

    public void receive_change_root(int src){

    }

    public void receive_connect(int src, int level){

    }


    /**
     * Function for thread
     */
    public void run() {
        logger.info("Run process " + index);
    }

}
