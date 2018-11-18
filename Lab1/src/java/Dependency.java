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

    public Map<Integer, List<Integer>> getDependency() {
        return dependency;
    }

    public void setDependency(Map<Integer, List<Integer>> dependency) {
        this.dependency = dependency;
    }

    /**
     * Update the denpendency of a node by using the information from new messsage, ONLY use this function if this class is dependency in the node
     * @param owner_number the node number of the owner
     * @param dependencyInput the dependency of a the new message
     */
    public void updateDependency(long owner_number, Map<Integer,List<Integer>> dependencyInput){
        for(int i = 0; i < node_number; i++){
            if(i != owner_number && dependencyInput.containsValue(i)){
                if(dependency.containsValue(i)){ //if there exists a dependency of i, update it
                    for(int j = 0; j < node_number ; j ++){
                        if(dependency.get(i).get(j)<dependencyInput.get(i).get(j))
                            dependency.get(i).set(j, dependencyInput.get(i).get(j));
                    }
                }else{//if not, insert it
                    dependency.put(i,dependencyInput.get(i));
                }

            }
        }
    }

    /**
     *
     * Chcek whether this message can be delivered, ONLY use this function if it is a dependency in the message
     * @param node the node index of the node which receives this message
     * @param ts the timestamp of the node which receives this message
     * @return true if it can be delivered
     */
    public boolean checkDependency(long node, List<Integer>  ts){
        //if there is no dependency for this node
        if(!dependency.containsKey(node)){
            return true;
        }
        for(int i = 0; i < node_number; i++) {
            if (dependency.get(node).get(i) > ts.get(i)) {
                return false;
            }
        }
        return  true;
    }
}
