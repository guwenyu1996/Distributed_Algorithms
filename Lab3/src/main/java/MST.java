import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Map of data structure from pseudo code to it
 * j -> src
 * SE(x) -> SE.get(x),getSE()
 * w(x) -> SE.get(x).getWeight()
 * if you want to invoke a function on node k, SE.get(k).getNode().foo()
 */

public class MST extends UnicastRemoteObject implements MST_RMI, Runnable{

    /**
     * since there is not Infinite in integer and i do not like float, let's define 9999 as INF, and NIL as -1
     */
    private final int INF = 9999;
    private final int NIL = -1;

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
    int best_weight;
    /**
     * number of report messages expected
     */
    int find_count;


    /**
     *  instead of storing the information of the edges, i prefer storing the
     *  information of the nodes on the other side of the edge
     *  the SE(j) in the ppt is stored in this map
     */
    private Map<Integer, Neighbour_node> SE;

    private Queue<Connect_massage> connect_queue;


    final static Logger logger = Logger.getLogger(MST.class);

    public MST(int processNum, int index)throws RemoteException {
        this.processNum = processNum;
        this.index = index;
        SE = new HashMap<Integer, Neighbour_node>();
        connect_queue = new LinkedList<Connect_massage>();
    }


    /**
     * /unction to inform this node the index of neighbouring node, and the weight of the edge to
     *  this node, thus we can construct a graph, should ONLY be invoked by the test function
     * @param SE
     */
    public void construct_key( Map<Integer, Neighbour_node> SE)  throws RemoteException{
        this.SE = SE;
    }

    /**
     * receive a initial message, start finding the MOE
     * @param src index of the source
     * @param level level of the sender
     * @param fragment_name fracment name of the sender
     * @param s state of the sender
     */
    public  void receive_initiate(int src, int level, int fragment_name, State_node s) throws RemoteException{

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

    public void receive_report (int src, int weight) throws RemoteException{
        if(src != this.in_branch){
            find_count --;
            if(weight < best_weight){
                best_weight = weight;
                best_edge = src;
            }
            report();
        }else{
            if(this.SN == State_node.Find){
                this.connect_queue.add(new Connect_massage(src, weight)); //put the message int he queue
            }else{
                if(weight > this.best_edge){
                    change_root();
                }else{
                    if((weight == INF) & (best_weight ==INF)){
                        return;
                    }
                }
            }
        }
    }

    public void receive_change_root(int src) throws RemoteException{
        change_root();
    }

    public void receive_connect(int src, int level) throws RemoteException{
        if(this.SN == State_node.Sleeping){
            wakeup();
        }

        if(level < this.LN){
            SE.get(src).setSE(State_edge.In_MST);
            SE.get(src).getNode().receive_initiate(this.index, this.LN, this.FN, this.SN);
            if(this.SN == State_node.Find){
                this.find_count --;
            }
        }else{
            if(SE.get(src).getSE() == State_edge.P_in_MST){
                //append message to queue, but in practice we think we do not need to
            }else{
                //use the weight of the common edge as the fragment name
                SE.get(src).getNode().receive_initiate(this.index, this.LN +1, this.SE.get(src).getWeight(), State_node.Find);
            }
        }
    }


    /**
     * Function for thread
     */
    public void run() {
        logger.info("Run process " + index);
    }

    /**
     * function to send report
     */
    private void report() throws RemoteException{
        if((this.find_count == 0) & (this.test_edge == NIL) ){
            this.SN =  State_node.Found;
            this.SE.get(this.in_branch).getNode().receive_report(this.index,this.best_weight);
        }
    }

    /**
     * function to change root
     */
    private void change_root() throws RemoteException{
        if(this.SE.get(this.best_edge).getSE() == State_edge.In_MST){
            this.SE.get(this.best_edge).getNode().receive_change_root(this.index);
        }else{
            this.SE.get(this.best_edge).getNode().receive_connect(this.index,this.LN);
            this.SE.get(best_edge).setSE(State_edge.In_MST);
        }
    }

    /**
     * function to wakeup
     */
    private void wakeup(){

    }
}
