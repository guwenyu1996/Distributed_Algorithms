import java.util.*;

/**
 * store the dependency of each node
 */
public class Dependency {
    long node_number=0; //the node number
    Map<Integer,List<Integer>> dependency= new HashMap<Integer, List<Integer>>(){};

    /**
     *
     * @param nodes the number of nodes
     */
    public void setNode_number(long nodes) {
        this.node_number = nodes;
    }

    public long getNode_number() {
        return node_number;
    }


    public boolean checkDepency(long node, List<Integer>  ts){
        for(int i = 0; i < node_number; i++) {
            if (dependency.get(node).get(i) > ts.get(i)) {
                return false;
            }
        }
        return  true;
    }
}
