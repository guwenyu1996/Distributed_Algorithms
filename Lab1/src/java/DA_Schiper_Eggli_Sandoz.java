package java;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.util.*;
import java.rmi.Naming;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

public class DA_Schiper_Eggli_Sandoz implements DA_Schiper_Eggli_Sandoz_RMI {

    /**
     * The total number of process in the system
     */
    private int processNum;

    /**
     * The index of current process
     */
    private int index;

    /**
     * The timestamp of the current process
     */
    private List<Integer> ts;

    /**
     * The local buffer within the current process.
     * Map to store the most up-to-date timestamp the current process knows for all other processes.
     */
    private Map<Integer, List<Integer>> localBuffer;

    /**
     * List of all messages the process received.
     */
    private List<Message> receivedMessage;

    /**
     * List of all messages the process received but could not delivered
     * due to the delay of some previous messages.
     */
    private List<Message> pendingMessage;

    /**
     * List of all messages the process received and delivered.
     */
    private List<Message> deliveredMessage;

    /**
     * List of all servers in the system
     */
    private Map<Integer,DA_Schiper_Eggli_Sandoz_RMI> processList;

    private Map<Integer, String> port; //map each node with its port number

    final static Logger logger = Logger.getLogger(DA_Schiper_Eggli_Sandoz.class);

    /**
     *
     * @param processNum number of nodes in the system
     * @param port map each node with its port number
     */
    public DA_Schiper_Eggli_Sandoz(int processNum, int index, Map<Integer, String> port){
        this.index =index;
        this.processNum = processNum;
        this.port = port;
        ts = new ArrayList<>();
    }

    public void send(int node, Message message, long delay) throws RemoteException{
        //if the process of index node is not initialized, initialize it
        if(!processList.containsKey(node)){
            try {
                DA_Schiper_Eggli_Sandoz_RMI newProcess = (DA_Schiper_Eggli_Sandoz_RMI) Naming.lookup(port.get(node));
                processList.put(node, newProcess);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            } catch (NotBoundException e3) {
                e3.printStackTrace();
            }
        }

        // add synchronized block
        synchronized (this){
            increaseTimestamp();
            message.setBuffer(this.localBuffer);
            message.setTs(this.ts);
        }

        ///delay the sending of message
        try{
            Thread.sleep(delay);
        } catch (InterruptedException e1){
            e1.printStackTrace();
        }

        try {
            processList.get(node).receive(message);
            localBuffer.put(node, ts);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * {@inheritDoc}
     */
    public synchronized void receive(Message message) throws RemoteException{

        // check whether the message could be delivered
        if(isDeliveryReady(message)){
            deliver(message);

            // check the message in pending list could be delivered
            Message temp = null;
            while(checkPendingList(temp)){
                deliver(temp);
                pendingMessage.remove(temp);
            }
        }else
            pendingMessage.add(message);
    }

    /**
     * Check whether there exists a message in pending list could be delivered.
     * @param message the pointer to message could be delivered
     * @return true if the message could be delivered right away
     *         false if all messages in pending list could not delivered
     */
    private boolean checkPendingList(Message message){
        boolean result = false;

        for(Message msg: pendingMessage){
            if(isDeliveryReady(msg)){
                message = msg;
                result = true;
                break;
            }
        }

        return result;
    }


    /**
     *
     */
    private void deliver(Message message){
        processMessage(message);

        // update local clock
        ts = mergeClocks(ts, message.getTs());

        // update local buffer
        mergeBuffer(message.getBuffer());
    }

    /**
     *
     * @param message
     */
    private void processMessage(Message message){
        logger.info("Deliver message \" " + message.content + " \" in process " + index);
        deliveredMessage.add(message);
        increaseTimestamp();
    }

    /**
     * Increase the local timestamp by 1.
     */
    private void increaseTimestamp(){
        ts.set(index, ts.get(index) + 1);
    }

    /**
     * Check whether the received message could be delivered or not.
     * @param message the message to be checked
     * @return
     */
    private boolean isDeliveryReady(Message message){

        Map<Integer, List<Integer>> messageBuffer = message.getBuffer();

        if (!messageBuffer.containsKey(index))
            return true;

        List<Integer> messageClock = messageBuffer.get(index);
        boolean result = true;
        for(int i = 0; i < processNum; i ++){
            if(messageClock.get(i) > ts.get(i)) {
                result = false;
                break;
            }
        }

        return result;
    }


    /**
     * Merge the local buffer with a buffer in the message received.
     * Compare the clock for processes with same index. Skip the clock for current process.
     * @param messageBuffer buffer accompanied in the message
     */
    private void mergeBuffer(Map<Integer, List<Integer>> messageBuffer){
        Map<Integer, List<Integer>> maxBuffer = new HashMap<>();

        for(Map.Entry<Integer, List<Integer>> list: messageBuffer.entrySet()){
            int processId = list.getKey();

            if(processId == index)
                continue;

            if(localBuffer.containsKey(processId))
                maxBuffer.put(processId, mergeClocks(localBuffer.get(processId), list.getValue()));
            else
                maxBuffer.put(processId, list.getValue());
        }

        localBuffer.clear();
        localBuffer = maxBuffer;
    }

    /**
     * Return a new vector clock represents the maximization of two vector clocks.
     * Comparison is made between values with same index.
     * @param clock1
     * @param clock2
     * @return a new clock merging clock1 and clock2
     */
    private List<Integer> mergeClocks(List<Integer> clock1, List<Integer> clock2){
        List<Integer> maxClock = new ArrayList<>();
        for (int i = 0; i < clock1.size(); i ++)
            maxClock.add(Math.max(clock1.get(i), clock2.get(i)));

        return maxClock;
    }

}
