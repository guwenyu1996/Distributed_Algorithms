//this class store the information for a neighbouring node
public class Neighbour_node {
    private int index; // index of the node
    private int weight; //weight of the edge to this node
    private State_edge SE; //state of the edge to this node
    private MST_RMI node; // interface to invoke functions on this node

    void Neighbour_node(int index, int weight, State_edge SE, MST_RMI node){
        this.index = index;
        this.weight = weight;
        this.SE = SE;
        this.node = node;
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
