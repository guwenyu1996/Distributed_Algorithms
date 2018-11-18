import java.util.LinkedList;
import java.util.List;
public class Message {
    String content = "";
    long target = 0;  //the target node
    long nodes = 0; ///number of target in the system
    List<Integer>  ts = new LinkedList<Integer>();
    Dependency dependency = new Dependency();

    /**
     *
     * @param nodes number of nodes in the system
     */
    public Message(int nodes){
        this.nodes = nodes;
        //initialize the time stamp
        for(int i =0;i<nodes;i++){
            ts.add(0);
        }
    }

    public void setTs(List<Integer> ts) {
        this.ts = ts;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public void setTarget(long target) {
        this.target = target;
    }

    public List<Integer> getTs() {
        return ts;
    }

    public String getContent() {
        return content;
    }

    public Dependency getDependency() {
        return dependency;
    }

    /**
     *
     * @param ts time stamp of the target node
     * @return whether this massage can be delivered
     */
    public boolean checkDependency(List<Integer>  ts){
        return dependency.checkDependency(target,ts);
    }

}
