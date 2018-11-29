import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
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

    final static Logger logger = Logger.getLogger(Singhal.class);

    public Singhal(int processNum, int index) throws RemoteException {
        this.processNum = processNum;
        this.index = index;
    }


    /**
     * send a token to destination
     * @param des index of destination
     */
    public void sendToken (int des) throws RemoteException {

    }

    /**
     * receive a token
     * @param token the token to be received
     */
    public void receiveToken(Token token) throws RemoteException {

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
