import org.apache.log4j.Logger;

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

    private final int INF = Integer.MAX_VALUE;
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
    private int LN;
    /**
     * name of the current fragment it is part of
     */
    private int FN;

    /**
     * state of the node (find/found)
     */
    private State_node SN;

    /**
     * index of node on the other side of the edge towards core
     */
    private int in_branch;
    /**
     * index of node on the other side of the test edge
     */
    private int test_edge;
    /**
     * index of the node on the other side of the best edge, best edge is the local candidate of the MOE
     */
    private int best_edge;
    /**
     * weight tof the current candidate MOE
     */
    private int best_weight;
    /**
     * number of report messages expected
     */
    private int find_count;

    private boolean halt;

    private boolean isTest;

    private int received, delieved;

/////for statistic only
    private List<Integer> connected; //store the indexes of connected process
    private Map<MessageType,Integer> messageCount;
    private int merge;
    private int absorb;

    /**
     *  instead of storing the information of the edges, i prefer storing the
     *  information of the nodes on the other side of the edge
     *  the SE(j) in the ppt is stored in this map
     */
    private Map<Integer, NeighbourNode> SE;

    private Queue<Message> queue;

    final static Logger logger = Logger.getLogger(MST.class);

    public MST(int processNum, int index)throws RemoteException {
        this.processNum = processNum;
        this.index = index;
        reset();
    }

    public void reset() throws RemoteException{
        SN = State_node.Sleeping;
        SE = new HashMap<Integer, NeighbourNode>();
        LN = 0;
        FN = index;
        queue = new LinkedList<Message>();
        halt = false;
        find_count = 0;
        test_edge = NIL;
        best_weight = INF;
        isTest = false;
        received = 0;
        delieved = 0;

        best_edge = 0;
        in_branch = 0;

        queue.clear();


        messageCount =new HashMap<MessageType, Integer>();
        messageCount.put(MessageType.INITIATE,0);
        messageCount.put(MessageType.ACCEPT,0);
        messageCount.put(MessageType.REJECT,0);
        messageCount.put(MessageType.CHANGE_ROOT,0);
        messageCount.put(MessageType.CONNECT,0);
        messageCount.put(MessageType.TEST,0);
        messageCount.put(MessageType.REPORT,0);

        in_branch=0;
        merge = 0;
        absorb = 0;

        logger.info("-----------------------------reset-----------------------------");

    }

    public ReturnMessage getStatistic()throws RemoteException{
        while(halt == false){
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                logger.info(e.getMessage());
            }
        }
        return new ReturnMessage(in_branch, SE.get(in_branch).getWeight(), messageCount, merge, absorb);
    }

    private class Receive extends Thread{
        private Message message;

        public Receive(Message msg){
            super();
            message = msg;
        }

        @Override
        public void run(){

            try{
                synchronized(this){
                    message.setSequence(received);
                    received ++;
                }

                while(message.getSequence() != delieved){
                    Thread.sleep(100);
                }

                switch (message.getType()){
                    case INITIATE:  deliver_initiate(message.getSrc(),message.getLevel(),message.getFragment(),message.getState());break;
                    case TEST:      deliver_test(message.getSrc(), message.getLevel(),message.getFragment());break;
                    case ACCEPT:    deliver_accept(message.getSrc());break;
                    case REJECT:    deliver_reject(message.getSrc());break;
                    case REPORT:    deliver_report(message.getSrc(),message.getWeight());break;
                    case CONNECT:   deliver_connect(message.getSrc(),message.getLevel());break;
                    case CHANGE_ROOT: deliver_change_root();break;

                }

                //after processing the message  plus it
                handleQueue();
                delieved ++;


            }catch(InterruptedException e){
                logger.error(e.getMessage());
            }catch(RemoteException e){
                logger.error(e.getMessage());
            }

        }
    }


    public void receive_message(Message msg) throws RemoteException{
        logger.info("Receive a message " + msg.getType() + " from P" + msg.getSrc());

        messageCount.put(msg.getType(),messageCount.get(msg.getType())+1);

        Receive receive = new Receive(msg);
        receive.start();
    }


    /**
     * /unction to inform this node the index of neighbouring node, and the weight of the edge to
     *  this node, thus we can construct a graph, should ONLY be invoked by the test function
     * @param SE
     */
    public void construct_key( Map<Integer, NeighbourNode> SE)  throws RemoteException{
        this.SE = SE;
    }

    /**
     * If receives an initial message, propagate new information of tree to nodes in tree and
     * start searching for MOE to nodes are likely in tree.
     * @param src index of the source
     * @param level level of the sender
     * @param fragment_name fragment name of the sender
     * @param s state of the sender
     */
    public void deliver_initiate(int src, int level, int fragment_name, State_node s) throws RemoteException{

        logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                "P" + src + " Initiate Msg: level = " + level + " frag = " + fragment_name + " state = " + s);
        LN = level;
        FN = fragment_name;
        SN = s;
        in_branch = src;
        best_edge = NIL;
        best_weight = INF;

        for (Map.Entry<Integer, NeighbourNode> iter : SE.entrySet()){
            NeighbourNode neighbour = iter.getValue();

            if(neighbour.getSE() == State_edge.In_MST && neighbour.getIndex()!= src){

                Message msg = new Message(MessageType.INITIATE,index);
                msg.setLevel(LN);
                msg.setFragment(FN);
                msg.setState(SN);
                neighbour.getNode().receive_message(msg);
                if(s == State_node.Find)
                    find_count ++;
            }
        }

        // searching for MOE
        if(s == State_node.Find)
            test();
    }

    /**
     * Find a MOE.
     * @throws RemoteException
     */
    public void test() throws RemoteException{
        // search for MOE: a neighbour which has a minimal weight and is not examined
        int minNeigh = -1, min = Integer.MAX_VALUE;
        for (Map.Entry<Integer, NeighbourNode> iter : SE.entrySet()){

            NeighbourNode neighbour = iter.getValue();
            if(neighbour.getSE() == State_edge.P_in_MST && neighbour.getWeight() < min){
                min = neighbour.getWeight();
                minNeigh = neighbour.getIndex();
            }
        }

        // if such neighbour node is founded, send test message
        // if no, means all neighbours have been tested, report the best edge right now
        if(minNeigh != -1){
            logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                    "Test: Send Test Msg to P" + minNeigh);
            test_edge = minNeigh;

            Message msg = new Message(MessageType.TEST,index);
            msg.setLevel(LN);
            msg.setFragment(FN);
            SE.get(minNeigh).getNode().receive_message(msg);
        }
        else{
            logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                    "Test: no MOE");
            //fixme
            best_weight = INF;
            test_edge = NIL;
            report();
        }
    }

    /**
     *
     * @param src
     * @param level
     * @param fragment_name
     * @throws RemoteException
     */
    public boolean deliver_test(int src, int level, int fragment_name) throws RemoteException{
        boolean isDeliverd = true;
        logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                "Receive P" + src + " Test Msg: level = " + level + " frag = " + fragment_name);

        if(SN == State_node.Sleeping){
            wakeup();
        }

        if(level > LN){
            isDeliverd = false;
            Message msg = new Message(MessageType.TEST, src);
            msg.setLevel(level);
            msg.setFragment(fragment_name);
            queue.add(msg);

            logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                    "P" + src + " Test Msg is postponed.");
        }else{ // absorb
            // absorb subtree which is not in same fragment
            if(fragment_name != FN){
                logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                        "P" + src + " Test Msg is accepted.");

                Message msg = new Message(MessageType.ACCEPT,index);
                SE.get(src).getNode().receive_message(msg);
            }
                // Reject if neighbour node is in same fragment
            else{
                if(fragment_name != FN) {
                    Message msg = new Message(MessageType.ACCEPT,index);
                    SE.get(src).getNode().receive_message(msg);
                }else{
                    if (SE.get(src).getSE() == State_edge.P_in_MST)
                        SE.get(src).setSE(State_edge.Not_in_MST);

                    //fixme
                    if (test_edge != src){
                        Message msg = new Message(MessageType.REJECT,index);
                        SE.get(src).getNode().receive_message(msg);
                    }
                    else{
                        test();
                    }

                }
            }
        }

        return isDeliverd;
    }

    /**
     * If receive an accept, absorbs a subtree.
     */
    public void deliver_accept(int src) throws RemoteException{
        logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                "Receive P" + src + " Accept Msg");
        test_edge = NIL;
        int propose = SE.get(src).getWeight();
        if( propose < best_weight){
            best_edge = src;
            best_weight = propose;
            logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                    " bestW = " + best_weight + " bestE = " + best_edge);
        }
        report();
    }

    public void deliver_reject(int src) throws RemoteException{
        logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                "Receive P" + src + " Reject Msg");
        if(SE.get(src).getSE() == State_edge.P_in_MST)
            SE.get(src).setSE(State_edge.Not_in_MST);

        test();
    }

    /**
     * If a node receives report from children, compare the best weight
     * from the children and from itself, and report to its parents.
     *
     * If a node receives report from core node, determine to merge two
     * subtrees or end constructing.
     * @param src
     * @param weight
     * @throws RemoteException
     */
    public void deliver_report (int src, int weight) throws RemoteException{
        logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                "Receive P" + src + "Report Msg with weight = " + weight + "bestW = " + best_weight);

        if(src != this.in_branch){
            // receive report from own subtree
            find_count --;
            if(weight < best_weight){
                best_weight = weight;
                best_edge = src;
            }
            report();
        }else if(SN == State_node.Find){
            Message msg = new Message(MessageType.REPORT,src);
            msg.setWeight(weight);
            queue.add(msg);
        }else{
            // receive report from the other side of core edge
            if(weight > this.best_weight){
                change_root();
                // fixme
            }else if(weight == INF && best_weight == INF && halt == false){
                logger.info("halt");
                halt();
            }
        }
    }

    /**
     * Receive a change root message.
     * @throws RemoteException
     */
    public void deliver_change_root() throws RemoteException{
        logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                "Receive Change Root Msg");
        change_root();
    }

    private void change_root() throws RemoteException{
        if(this.SE.get(this.best_edge).getSE() == State_edge.In_MST){
            // propagate change root message if it does know the real best edge
            Message msg = new Message(MessageType.CHANGE_ROOT,index);
            this.SE.get(this.best_edge).getNode().receive_message(msg);
        }else{
            // try to merge two subtrees
            Message msg = new Message(MessageType.CONNECT,index);
            msg.setLevel(LN);
            this.SE.get(this.best_edge).getNode().receive_message(msg);
            this.SE.get(best_edge).setSE(State_edge.In_MST);
        }
    }

    /**
     * Receive a connect message.
     * If levels of two nodes are different, absorb one with lower level.
     * If same, merge two nodes.
     * @param src
     * @param level
     * @throws RemoteException
     */
    public boolean deliver_connect(int src, int level) throws RemoteException{
        boolean isDelivered = true;
        logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                "Receive P" + src + " Connect Msg with level = " + level);

        if(this.SN == State_node.Sleeping){
            wakeup();
        }

        if(level < this.LN){
            absorb ++;

            SE.get(src).setSE(State_edge.In_MST);

            Message msg = new Message(MessageType.INITIATE,index);
            msg.setLevel(LN);
            msg.setFragment(FN);
            msg.setState(SN);
            SE.get(src).getNode().receive_message(msg);
            if(this.SN == State_node.Find){
                this.find_count ++;
            }
        }else{
            // merge two subtrees
            if(SE.get(src).getSE() == State_edge.P_in_MST){
                isDelivered = false;
                Message msg = new Message(MessageType.CONNECT, src);
                msg.setLevel(LN);
                queue.add(msg);
            }else{
                Message msg = new Message(MessageType.INITIATE,index);
                msg.setLevel(LN+1);
                msg.setFragment(this.SE.get(src).getWeight());
                msg.setState(State_node.Find);
                merge ++;
                SE.get(src).getNode().receive_message(msg);
            }
        }

        return isDelivered;
    }


    public void receive_print(int src) throws RemoteException{
        if(halt)
            return;

        logger.info("------- Construct MST Finished --------");
        halt = true;

        for (Map.Entry<Integer, NeighbourNode> iter : SE.entrySet()) {
            NeighbourNode neighbour = iter.getValue();
            if(neighbour.getSE() == State_edge.In_MST) {
                logger.info("P" + index + " ------- P" + neighbour.getIndex() + " weight = "
                        + neighbour.getWeight() + " parent = " + neighbour.getNode().getIn_branch());

                if(src != neighbour.getIndex())
                    neighbour.getNode().receive_print(index);
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
     * Report the best edge to the parent node.
     */
    private void report() throws RemoteException{
        if(this.find_count == 0 && this.test_edge == NIL){
            logger.info("< " + LN + ", " + FN + ", " + SN + ", " + find_count + ", " + test_edge + " > " +
                    "Send Report to P" + in_branch + " bestW = " + best_weight);
            this.SN =  State_node.Found;

            Message msg = new Message(MessageType.REPORT,index);
            msg.setWeight(this.best_weight);
            this.SE.get(this.in_branch).getNode().receive_message(msg);

        }
    }

    /**
     * Activate a node by send a connect message to MOE.
     */
    private void wakeup() throws RemoteException{
        logger.info("Wake up");

        // searching for neighbour nodes with minimal weight
        int min = Integer.MAX_VALUE;
        int minNeigh = 0;
        for (Map.Entry<Integer, NeighbourNode> iter : SE.entrySet()){
            NeighbourNode neighbour = iter.getValue();
            if(neighbour.getWeight() < min){
                min = neighbour.getWeight();
                minNeigh = neighbour.getIndex();
            }
        }

        // update adjacent edge
        SE.get(minNeigh).setSE(State_edge.In_MST);
        LN = 0;
        SN = State_node.Found;
        find_count = 0;

        Message msg = new Message(MessageType.CONNECT,index);
        msg.setLevel(LN);
        SE.get(minNeigh).getNode().receive_message(msg);
    }

    /**
     * Awake the node
     */
    public void start() throws RemoteException{
        if(SN == State_node.Sleeping)
            wakeup();
    }

    public State_node getSN() {
        return SN;
    }


    private void handleQueue() throws RemoteException{

        for(int i = 0; i < queue.size(); i ++){
            Message msg = queue.poll();
            if(msg == null)
                break;

            switch(msg.getType()){
                case CONNECT:{
                    if(deliver_connect(msg.getSrc(), msg.getLevel())) {
                        logger.info("Handle Queue Connect msg from P" + msg.getSrc());
                        handleQueue();
                    }
                    break;
                }
                case TEST: {
                    if(deliver_test(msg.getSrc(), msg.getLevel(), msg.getFragment())){
                        logger.info("Handle Queue Test msg from P" + msg.getSrc());
                        handleQueue();
                    }
                    break;
                }
                case REPORT: {
                    logger.info("Handle Queue Report msg from P" + msg.getSrc());
                    deliver_report(msg.getSrc(), msg.getWeight());
                    break;
                }
            }
        }
    }

    private void halt()throws RemoteException{
        receive_print(index);
    }


    public int getIn_branch() {
        return in_branch;
    }


}
