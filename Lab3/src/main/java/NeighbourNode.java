import java.io.Serializable;

//this class store the information for a neighbouring node
public class NeighbourNode implements Serializable {
    private int index; // index of the node
    private int weight; //weight of the edge to this node
    private State_edge SE; //state of the edge to this node
    private MST_RMI node; // interface to invoke functions on this node

    public NeighbourNode(){
    }

    @Override
    public String toString() {
        return "Neighbour index "+ index + " with weight " + weight;
    }

    public void setNode(MST_RMI node) {
        this.node = node;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSE(State_edge SE) {
        this.SE = SE;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getIndex() {
        return index;
    }

    public int getWeight() {
        return weight;
    }

    public MST_RMI getNode() {
        return node;
    }

    public State_edge getSE() {
        return SE;
    }
}
