import com.sun.org.apache.bcel.internal.generic.RETURN;
import edu.uci.ics.jung.graph.util.EdgeType;
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

    final boolean isLocal = true;

    final static Logger logger = Logger.getLogger(testcase.class);
    List<MST_RMI> processes = new ArrayList<MST_RMI>();



    int num_nodes =30;

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
            for(int i = 0; i < urls.length;i++){
                if(isLocal){
                    MST process_rmi = new MST(urls.length, i);
                    logger.info("create server at" + urls[i]);
                    new Thread(process_rmi).start();
                    Naming.bind(urls[i], process_rmi);
                    processes.add(process_rmi);
                }else{
                    process = (MST_RMI)Naming.lookup(urls[i]);
                    processes.add(process);
                }

            }

        }catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (NotBoundException e4){
            e4.printStackTrace();
        }catch (AlreadyBoundException e5){
            e5.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
    }

    @Test
    public void test1() throws RemoteException{
        int edgeNum = num_nodes * (num_nodes - 1) / 2;

        Integer weights[] = new Integer[edgeNum];
        for(int i =0; i < edgeNum ; i ++)
            weights[i] = i+ num_nodes;

        randomize(weights);

        SimpleGraphView graph = new SimpleGraphView();
        List<Map<Integer, NeighbourNode>> nodes = new LinkedList<Map<Integer, NeighbourNode>>();
        for(int i =0; i< num_nodes; i++){
            nodes.add(new HashMap<Integer, NeighbourNode>());
            graph.addVertex(i);
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

                graph.addEdge(i, j, weights[weight_select], EdgeType.UNDIRECTED);

                weight_select++;
            }
        }

        // Draw complete graph
        graph.draw();

        // Print
        for(int i =0; i < num_nodes; i++){
            logger.info("the graph of node " + i +" is: " + nodes.get(i).toString());
        }
        
        for(int i=0; i < num_nodes; i++) {
            processes.get(i).reset();
            processes.get(i).construct_key(nodes.get(i));
        }

        long start = System.currentTimeMillis();


        processes.get(0).start();
        processes.get(1).start();

        processStatistic(num_nodes);

        long end = System.currentTimeMillis();
        logger.info( "The task takes" + (end -start) + "milliseconds");


        drawTree(num_nodes);

        try{
            while(true){
                Thread.sleep(5000);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void drawTree(int nodesNum){
        try{

            SimpleGraphView graph = new SimpleGraphView();

            // add vertex
            for(int i = 0; i < nodesNum; i ++)
                graph.addVertex((Integer)i);

            // add edge
            for(int i=0; i < nodesNum; i++) {
                ReturnMessage returnMessage = processes.get(i).getStatistic();
                if(!graph.isRepeated(i, returnMessage.getWeight()))
                    graph.addEdge(i, returnMessage.getIn_branch(), returnMessage.getWeight(), EdgeType.DIRECTED);
            }

            graph.draw();
        }catch (RemoteException e){
            logger.error(e.getMessage());
        }
    }

    void processStatistic(int num_nodes) throws RemoteException{
        Map<MessageType,Integer> messageCount = new HashMap<MessageType, Integer>();

        messageCount =new HashMap<MessageType, Integer>();
        messageCount.put(MessageType.INITIATE,0);
        messageCount.put(MessageType.ACCEPT,0);
        messageCount.put(MessageType.REJECT,0);
        messageCount.put(MessageType.CHANGE_ROOT,0);
        messageCount.put(MessageType.CONNECT,0);
        messageCount.put(MessageType.TEST,0);
        messageCount.put(MessageType.REPORT,0);

        int in_branch = 0;
        int merge = 0;
        int absorb = 0;

        for(int i=0; i < num_nodes; i++) {
            ReturnMessage returnMessage = processes.get(i).getStatistic();
            absorb += returnMessage.getAbsorb();
            merge  += returnMessage.getMerge();


            int count =messageCount.get(MessageType.INITIATE) + returnMessage.getMessageCount().get(MessageType.INITIATE);
            messageCount.put(MessageType.INITIATE,count);

            count = messageCount.get(MessageType.ACCEPT) + returnMessage.getMessageCount().get(MessageType.ACCEPT);
            messageCount.put(MessageType.ACCEPT,count);

            count = messageCount.get(MessageType.REJECT) + returnMessage.getMessageCount().get(MessageType.REJECT);
            messageCount.put(MessageType.REJECT,count);

            count = messageCount.get(MessageType.CHANGE_ROOT) + returnMessage.getMessageCount().get(MessageType.CHANGE_ROOT);
            messageCount.put(MessageType.CHANGE_ROOT,count);

            count =messageCount.get(MessageType.CONNECT) + returnMessage.getMessageCount().get(MessageType.CONNECT);
            messageCount.put(MessageType.CONNECT,count);

            count = messageCount.get(MessageType.TEST) + returnMessage.getMessageCount().get(MessageType.TEST);
            messageCount.put(MessageType.TEST,count);

            count = messageCount.get(MessageType.REPORT) + returnMessage.getMessageCount().get(MessageType.REPORT);
            messageCount.put(MessageType.REPORT,count);


            logger.info("information of process: " + i  + " P" +i + " is connected to P" + returnMessage.getIn_branch() + " with weight of " + returnMessage.getWeight() + "the level is "+ returnMessage.getLevel() +"whether core " + returnMessage.getCore());
        }
        logger.info( " Number of merge: " + merge/2 + " Number of abort: " + absorb +" Message statistic" + messageCount);

    }


    void randomize(Integer array[]){
        Random r = new Random(12);
        for(int i =0; i <array.length;i++ ){
            int position1 = r.nextInt(array.length);
            int position2 = r.nextInt(array.length);

            Integer temp = array[position1];
            array[position1] = array[position2];
            array[position2] = temp;
        }
    }
}
