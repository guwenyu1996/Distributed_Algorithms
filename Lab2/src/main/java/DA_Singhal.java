import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DA_Singhal extends UnicastRemoteObject implements DA_Singhal_RMI, Runnable{

    /**
     * The total number of process in the system
     */
    private int processNum;

    /**
     * The index of current process
     */
    private int index;

    /**
     * the arrary to store the State of all processes
     */
    private List<State> state_array =new LinkedList<State>();

    private List<Integer> request_number =new LinkedList<Integer>();

    /**
     * List of all servers in the system
     */
    private Map<Integer, DA_Singhal_RMI> processList;


    private List<DA_Singhal_RMI> queue;
    /**
     * Map of url of remote process and its process index.
     */
    private Map<Integer, String> port;

    final static Logger logger = Logger.getLogger(DA_Singhal.class);

    public DA_Singhal(int processNum, int index) throws RemoteException {
        this.processNum = processNum;
        this.index = index;
 
        ///initialize the state array
        if(index == 0){
            state_array.add(State.H);
            for(int i=1;i<processNum;i++){
                state_array.add(State.O);
            }
        }else{
            for(int i=0;i<processNum;i++){
                if(i<index){
                    state_array.add(State.R);
                }else{
                    state_array.add(State.O);
                }
            }
        }

        for(int i=0;i<processNum;i++){
            request_number.add(0);
        }

        processList = new HashMap<Integer, DA_Singhal_RMI>();
        port = new HashMap<Integer, String>();
        String[] urls = ProcessManager.readConfiguration();
        for(int i = 0; i < urls.length; i ++)
            port.put(i, urls[i]);
    }


    /**
     * Grant permission to a targeted process by sending a token
     * @param desId
     * @param token
     * @throws RemoteException
     */
    public void sendToken (int desId, Token token) throws RemoteException {
        checkProcess(desId);

        processList.get(desId).receiveToken(token);

    }

    /**
     * receive a token
     * @param token the token to be received
     */
    public void receiveToken(Token token) throws RemoteException {
        logger.info("into receiveToken" + "state array:" + this.state_array + "request numbers "+ this.request_number);

        checkProcess(index);

        SleepCS delayedProcess = new SleepCS(processList.get(index),token);
        new Thread(delayedProcess).start();

        logger.info("exit receiveToken" + "state array:" + this.state_array + "request numbers "+ this.request_number);
    }

    /**
     * Process a token
     * @param token
     * @throws RemoteException
     */
    public void processToken(Token token) throws RemoteException{
        logger.info("into processToken" + "state array:" + this.state_array + "request numbers "+ this.request_number);

        state_array.set(index, State.E);

        CS();

        state_array.set(index, State.O);
        token.setTS(index, State.O);

        // update knowledge for both token and process
        for(int i = 0; i < processNum; i ++){
            if(this.request_number.get(i) > token.getTN().get(i)){
                token.setTN(i, this.request_number.get(i));
                token.setTS(i, this.state_array.get(i));

                // update knowledge for waiting list of token
                if(state_array.get(i) == State.R)
                    token.addPossibleOwner(i);
            }else{
                this.request_number.set(i, token.getTN().get(i));
                this.state_array.set(i, token.getTS().get(i));
            }
        }

        // update information for waiting list for token
        int next = token.retrieveNextOwner();
        if(next == -1){
            this.state_array.set(index, State.H);
        }else{
            sendToken(next, token);
        }
        logger.info("exit processToken" + "state array:" + this.state_array + "request numbers "+ this.request_number);
    }

    /**
     * Send requests to processes that might hold the token
     */
    public void sendRequest() throws RemoteException {
        logger.info("into send request" + " state:" + this.state_array + " reqNum "+ this.request_number);

        this.state_array.set(index, State.R);
        this.request_number.set(index, request_number.get(index) + 1);


        for(int i = 0; i < processNum; i ++){
            if(i == index)
                continue;

            if(state_array.get(i) == State.R){
                checkProcess(i);
                processList.get(i).receiveRequest(index, request_number.get(index));
            }
        }

        logger.info("exit send request" + " state:" + this.state_array + " reqNum "+ this.request_number);
    }

    /**
     * The entry function for requesting permissions
     * @throws RemoteException
     */
    public void requestCS() throws RemoteException{
        Token token_temp = new Token();

        if(this.state_array.get(index) == State.H) {
            token_temp.setTN(this.request_number);
            token_temp.setTS(this.state_array);

            receiveToken(token_temp);
        }else if(this.state_array.get(index) == State.O){
            sendRequest();
        }
    }


    /**
     * receive a request
     * @param srcId index of source process
     * @param reqNum request number
     */
    public void receiveRequest(int srcId, int reqNum) throws RemoteException {
        logger.info("into receiveRequest from "+ srcId + " state :" + this.state_array + "reqNum "+ this.request_number);
        checkProcess(srcId);
        request_number.set(srcId, reqNum);

        switch (state_array.get(index)){
            case O:{
                state_array.set(srcId, State.R);
                break;
            }
            case E:{
                state_array.set(srcId, State.R);
                break;
            }
            case R:{
                if(state_array.get(srcId) != State.R){
                    state_array.set(srcId, State.R);
                    processList.get(srcId).receiveRequest(index, request_number.get(index));
                }
                break;
            }
            case H:{
                state_array.set(srcId, State.R);
                state_array.set(srcId, State.O);

                Token token = new Token();
                token.setTN(request_number);
                token.setTS(state_array);

                sendToken(srcId, token);
                break;
            }
        }

        logger.info("exit receiveRequest" + "state array:" + this.state_array + "req Num "+ this.request_number);
    }


    /**
     * Critical section
     */
    public void CS(){
        logger.info("Process" + index+" enters the critical section ");
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        logger.info("Process" + index+" exits the critical section ");
    }

    /**
     * Check whether the reference to remote process has been initialized or not
     * @param index the index of process
     */
    private void checkProcess(int index){
        //if the process of index node is not initialized, initialize it
        if(!processList.containsKey(index)){
            try {
                DA_Singhal_RMI newProcess = (DA_Singhal_RMI) Naming.lookup(port.get(index));
                processList.put(index, newProcess);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            } catch (NotBoundException e3) {
                e3.printStackTrace();
            }
        }
    }

    /**
     * Function for thread
     */
    public void run() {
        logger.info("Run process " + index);
    }


}
