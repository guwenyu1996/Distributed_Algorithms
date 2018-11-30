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
     * the arrary to store the state of all processes
     */
    private List<state> state_array =new LinkedList<state>();

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
        ///initialize the state array
        for(int i =0;i<processNum;i++){
            if(i<=index){
                if(index == 0){
                    state_array.add(state.H);
                }else{
                    state_array.add(state.R);
                }
            }else{
                state_array.add(state.O);
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
        state_array.set(index, state.E);

        CS();

        state_array.set(index, state.O);

        token.setTS(index, state.O);

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
            this.state_array.set(index, state.H);
        }else{
            sendToken(checkState(),token);
        }

    }

    /**
     * check whether there is a state R, if there is, return the position, else return -1
     * @return
     */
    private int checkState(){
        for(int i=0;i<processNum;i++){
            if(this.state_array.get(i)==state.R){
                return i;
            }
        }
        return -1;
    }


    /**
     * send a request to the destination
     * @param des index of destination
     */
    public  void sendRequest(int des) throws RemoteException {

    }

    /**
     * receive a request
     * @param src index of source process
     * @param r request number
     */
    public void receiveRequest(int src, int r) throws RemoteException {

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
