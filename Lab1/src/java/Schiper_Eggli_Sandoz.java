import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Schiper_Eggli_Sandoz implements Schiper_Eggli_Sandoz_interface{
    
    long nodes; ///number of nodes in the system
    long index; //the index of this node
    Dependency dependency = new Dependency();
    List<Integer> ts = new LinkedList<Integer>();

    Map<Integer, String> port; //map each node with its port number
    List<Message> undelivered= new LinkedList<Message>(); //buffer the received but unsolved messages

    public void send(int node, Message message) throws RemoteException{

    }

    public void receive(Message message) throws RemoteException{
        if(message.checkDependency(ts)){
            deliver(message);
        }else{
            undelivered.add(message);
        }
    }

    /**
     * check whether other messages can be delivered recursively
     * @param message the message to be delivered
     */
    private void deliver(Message message){
        System.out.println(message.content);
        for(int i =0; i < undelivered.size();i ++){
            if(undelivered.get(i).checkDependency(ts)){
                deliver(undelivered.remove(i));
            }
        }
    }

    /**
     * Update the dependency by using the
     * @param dependencyInput
     */
    private void updateDpendency(Dependency dependencyInput){
            dependency.updateDependency(index, dependencyInput.dependency);
    }

    /**
     * update the time stamp of this node by using the new time stamp from message
     * @param tsInput
     */
    private void updateTimestamp(List<Integer> tsInput){
        for(int i =0; i < nodes; i++){
            if(ts.get(i) < tsInput.get(i)){
                ts.set(i , tsInput.get(i));
            }
        }
    }

    /**
     * 
     * @param nodes number of nodes in the system
     * @param port map each node with its port number
     */
    Schiper_Eggli_Sandoz(int nodes, int index, Map<Integer, String> port){
        this.index =index;
        this.nodes = nodes;
        this.port = port;
    }

}
