import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Singhal extends UnicastRemoteObject implements Singhal_RMI, Runnable{

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
    private Map<Integer, Singhal_RMI> processList;

    /**
     * Map of url of remote process and its process index.
     */
    private Map<Integer, String> port;

    final static Logger logger = Logger.getLogger(Singhal.class);

    public Singhal(int processNum, int index) throws RemoteException {
        this.processNum = processNum;
        this.index = index;
        ///initialize the State array
        for(int i =0;i<processNum;i++){
            if(i<=index){
                if(index == 0){
                    state_array.add(State.H);
                }else{
                    state_array.add(State.R);
                }
            }else{
                state_array.add(State.O);
            }

            request_number.add(0);
        }
    }


    /**
     * send a token to destination
     * @param desId index of destination
     */
    public void sendToken (int desId, Token token) throws RemoteException {
        //if the process of index node is not initialized, initialize it
        if(!processList.containsKey(desId)){
            try {
                Singhal_RMI newProcess = (Singhal_RMI) Naming.lookup(port.get(desId));
                processList.put(desId, newProcess);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            } catch (NotBoundException e3) {
                e3.printStackTrace();
            }
        }

        processList.get(desId).receiveToken(token);

    }

    /**
     * receive a token
     * @param token the token to be received
     */
    public void receiveToken(Token token) throws RemoteException {
        state_array.set(index, State.E);

        CS();

        state_array.set(index, State.O);

        token.setTS(index, State.O);

        for(int i =0;i<processNum;i++){
            if(this.request_number.get(i)<token.getTN().get(i)){
                token.setTN(i,this.request_number.get(i));
                token.setTS(i, this.state_array.get(i));
            }else{
                this.request_number.set(i,token.getTN().get(i));
                this.state_array.set(i,token.getTS().get(i));
            }
        }

        if(checkState()==-1){
            this.state_array.set(index, State.H);
        }else{
            sendToken(checkState(),token);
        }

    }

    /**
     * check whether there is a State R, if there is, return the position, else return -1
     * @return
     */
    private int checkState(){
        for(int i=0;i<processNum;i++){
            if(this.state_array.get(i)==State.R){
                return i;
            }
        }
        return -1;
    }


    /**
     * send a request
     */
    public void sendRequest() throws RemoteException {

        this.state_array.set(index, State.R);
        this.request_number.set(index, request_number.get(index) + 1);

        for(int i = 0; i < processNum; i ++){
            if(i == index)
                continue;

            if(state_array.get(i) == State.R)
                processList.get(i).receiveRequest(index, request_number.get(index));
        }
    }



    /**
     * receive a request
     * @param srcId index of source process
     * @param reqNum request number
     */
    public void receiveRequest(int srcId, int reqNum) throws RemoteException {
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
            }
            case H:{
                state_array.set(srcId, State.R);
                state_array.set(srcId, State.O);

                Token token = new Token();
                token.setTN(request_number);
                token.setTS(state_array);

                sendToken(srcId, token);
            }
        }

    }

    public void runCS() throws RemoteException {

    }

    public void CS() throws RemoteException {

    }

    /**
     * Function for thread
     */
    public void run() {
        logger.info("Run process " + index);
    }

    public void test() throws RemoteException{
        logger.warn("Test - Process " + index);
    }


}
