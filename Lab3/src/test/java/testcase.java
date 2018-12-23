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

    }

    void randomize(Integer array[]){
        Random r = new Random(36);
        for(int i =0; i <array.length;i++ ){
            int position1 = r.nextInt(array.length);
            int position2 = r.nextInt(array.length);

            Integer temp = array[position1];
            array[position1] = array[position2];
            array[position2] = temp;
        }
    }
}
