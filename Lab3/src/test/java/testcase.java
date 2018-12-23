import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class testcase {

    final static Logger logger = Logger.getLogger(testcase.class);
    List<MST_RMI> processes = new ArrayList<MST_RMI>();

    Map<MessageType,Integer> messageCount;
    int in_branch;
    int merge;
    int absorb;

    @Before
    public void Initialize(){
        File directory = new File("");
        System.out.println(directory.getAbsolutePath());

        // initialize node property
        PropertiesConfiguration config = new PropertiesConfiguration();
        try{
            config.read(new FileReader("./src/main/resources/url.properties"));
        }catch(IOException e1){
            logger.error("Failed to read configurations. Throw by IOException");
            e1.printStackTrace();
        }catch (ConfigurationException e2){
            logger.error("Failed to read configurations. Throw by ConfigurationException");
            e2.printStackTrace();
        }

        String[] urls = config.getStringArray("node_url");

        try{
            LocateRegistry.createRegistry(1099);

            MST_RMI process;

            for(String url: urls){
                process = (MST_RMI)Naming.lookup(url);
                processes.add(process);
            }

        }catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (NotBoundException e4){
            e4.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        messageCount =new HashMap<MessageType, Integer>();
        messageCount.put(MessageType.INITIATE,0);
        messageCount.put(MessageType.ACCEPT,0);
        messageCount.put(MessageType.REJECT,0);
        messageCount.put(MessageType.CHANGE_ROOT,0);
        messageCount.put(MessageType.CONNECT,0);
        messageCount.put(MessageType.TEST,0);
        messageCount.put(MessageType.REPORT,0);

        merge = 0;
        absorb = 0;
    }

    @Test
    public void test1() throws RemoteException{
        int num_nodes = 5;
        int edgeNum = num_nodes * (num_nodes - 1) / 2;

        Integer weights[] = new Integer[edgeNum];
        for(int i =0; i < edgeNum ; i ++)
            weights[i] = i+ num_nodes;

        randomize(weights);

        List<Map<Integer, NeighbourNode>> nodes = new LinkedList<Map<Integer, NeighbourNode>>();
        for(int i =0; i< num_nodes; i++){
            nodes.add(new HashMap<Integer, NeighbourNode>());
        }

        int weight_select = 0;
        for(int i=0; i < num_nodes; i++){
            for(int j = i+1; j< num_nodes; j++){
                NeighbourNode node1 = new NeighbourNode();
                node1.setWeight(weights[weight_select]);
                node1.setIndex(j);
                node1.setSE(State_edge.P_in_MST);
                node1.setNode(processes.get(j));
                nodes.get(i).put(j,node1);

                NeighbourNode node2 = new NeighbourNode();
                node2.setWeight(weights[weight_select]);
                node2.setSE(State_edge.P_in_MST);
                node2.setIndex(i);
                node2.setNode(processes.get(i));
                nodes.get(j).put(i,node2);

                weight_select++;
            }
        }

        for(int i =0; i < num_nodes; i++){
            logger.info("the graph of node " + i +" is: " + nodes.get(i).toString());
        }


        for(int i=0; i < num_nodes; i++) {
            processes.get(i).reset();
            processes.get(i).construct_key(nodes.get(i));
        }

        processes.get(0).start();
        processes.get(2).start();
        processes.get(3).start();

        for(int i=0; i < num_nodes; i++) {
            ReturnMessage returnMessage = processes.get(i).getStatistic();
            this.absorb += returnMessage.getAbsorb();
            this.merge  += returnMessage.getMerge();

            int count =this.messageCount.get(MessageType.INITIATE) + returnMessage.getMessageCount().get(MessageType.INITIATE);
            this.messageCount.put(MessageType.INITIATE,count);

            count =this.messageCount.get(MessageType.ACCEPT) + returnMessage.getMessageCount().get(MessageType.ACCEPT);
            this.messageCount.put(MessageType.ACCEPT,count);

            count =this.messageCount.get(MessageType.REJECT) + returnMessage.getMessageCount().get(MessageType.REJECT);
            this.messageCount.put(MessageType.REJECT,count);

            count =this.messageCount.get(MessageType.CHANGE_ROOT) + returnMessage.getMessageCount().get(MessageType.CHANGE_ROOT);
            this.messageCount.put(MessageType.CHANGE_ROOT,count);

            count =this.messageCount.get(MessageType.CONNECT) + returnMessage.getMessageCount().get(MessageType.CONNECT);
            this.messageCount.put(MessageType.CONNECT,count);

            count =this.messageCount.get(MessageType.TEST) + returnMessage.getMessageCount().get(MessageType.TEST);
            this.messageCount.put(MessageType.TEST,count);

            count =this.messageCount.get(MessageType.REPORT) + returnMessage.getMessageCount().get(MessageType.REPORT);
            this.messageCount.put(MessageType.REPORT,count);

            logger.info("information of process: " + i +" Message statistic" + messageCount + " P" +"i" + " is connected to P" + returnMessage.getIn_branch() + " with weight of " + returnMessage.getWeight());
        }
        logger.info( " Number of merge: " + this.merge/2 + " Number of abort: " + this.absorb);
    }

    void randomize(Integer array[]){
        Random r = new Random(7);
        for(int i =0; i <array.length;i++ ){
            int position1 = r.nextInt(array.length);
            int position2 = r.nextInt(array.length);

            Integer temp = array[position1];
            array[position1] = array[position2];
            array[position2] = temp;
        }
    }
}
