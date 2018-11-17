package java;

import java.rmi.RemoteException;
import java.util.Map;

public class Schiper_Eggli_Sandoz implements Schiper_Eggli_Sandoz_interface{
    
    long nodes;
    Map<Integer, String> port; //map each node with its port number

    public void send(int node, Message message) throws RemoteException{

    }

    public void receive(Message message) throws RemoteException{

    }

    /**
     * 
     * @param nodes number of nodes in the system
     * @param port map each node with its port number
     */
    Schiper_Eggli_Sandoz(int nodes, Map<Integer, String> port){
        this.nodes = nodes;
        this.port = port;
    }

}

