

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class DA_Schiper_Eggli_Sandoz implements DA_Schiper_Eggli_Sandoz_RMI, Runnable {

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
    private Map<Integer, DA_Schiper_Eggli_Sandoz_RMI> processList;

    private Map<Integer, String> port; //map each node with its port number

    final static Logger logger = Logger.getLogger(DA_Schiper_Eggli_Sandoz.class);

    /**
     *
     * @param processNum
     * @param index
     */
    public DA_Schiper_Eggli_Sandoz(int processNum, int index){
        this.index =index;
        this.processNum = processNum;
        ts = new ArrayList<Integer>();
        receivedMessage = new ArrayList<Message>();
        pendingMessage = new ArrayList<Message>();
        deliveredMessage = new ArrayList<Message>();
        port = new HashMap<Integer, String>();

        String[] urls = DA_Schiper_Eggli_Sandoz_main.readConfiguration();
        for(int i = 0; i < urls.length; i ++)
            port.put(i, urls[i]);

        logger.info("Initialize process " + index + " of " + processNum);

    }

    /**
     *
     * @param destId
     * @param message
     * @param delay in milli
     * @throws RemoteException
     */
    public void send(int destId, Message message, int delay) throws RemoteException{

        //if the process of index node is not initialized, initialize it
        if(!processList.containsKey(destId)){
            try {
                DA_Schiper_Eggli_Sandoz_RMI newProcess = (DA_Schiper_Eggli_Sandoz_RMI) Naming.lookup(port.get(destId));
                processList.put(destId, newProcess);
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
            logger.info("Send Message from P" + index + " to P" + destId +
                    " with buffer " + message.getBuffer() +
                    " and timestamp " + message.getTs());
        }

        // delay the sending of message
        try{
            Thread.sleep(delay);
            processList.get(destId).receive(message);
            localBuffer.put(destId, ts);
        } catch (InterruptedException e1){
            e1.printStackTrace();
        } catch (RemoteException e2){
            e2.printStackTrace();
        }

    }

    /**
     * {@inheritDoc}
     */
    public synchronized void receive(Message message) throws RemoteException{

        receivedMessage.add(message);
        logger.info("P" + message.getDestId() + " receive a message from P" + message.getSrcId() +
                " with buffer " + message.getBuffer());

        // check whether the message could be delivered
        if(isDeliveryReady(message)){
            deliver(message);

            // check the message in pending list could be delivered
            Message temp = null;
            while(checkPendingList(temp)){
                deliver(temp);
                pendingMessage.remove(temp);
            }
        }else{
            pendingMessage.add(message);
            logger.info("P" + message.getDestId() + " postpone a message from P" + message.getSrcId() +
                    " with buffer " + message.getBuffer() + " at current state " + ts);
        }

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
     * Deliver a process.
     * @param message
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
        logger.info("Deliver message \" " + message.getContent() + " \" in process " + index);
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
     * @return true if message could be delivered right away
     *         false then the message will be put into pending list
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
        Map<Integer, List<Integer>> maxBuffer = new HashMap<Integer, List<Integer>>();

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
        List<Integer> maxClock = new ArrayList<Integer>();
        for (int i = 0; i < clock1.size(); i ++)
            maxClock.add(Math.max(clock1.get(i), clock2.get(i)));

        return maxClock;
    }

    /**
     * Function for thread
     */
    public void run(){
        logger.info("Run process " + index);
    }


}
