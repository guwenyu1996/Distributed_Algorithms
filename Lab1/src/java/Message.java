package java;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Message {
    String content = "";
    long target = 0;  //the target node
    long nodes = 0; ///number of target in the system


    List<Integer>  ts = new LinkedList<Integer>();
    private Map<Integer, List<Integer>> buffer;

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



    public void setTarget(long target) {
        this.target = target;
    }

    public List<Integer> getTs() {
        return ts;
    }

    public String getContent() {
        return content;
    }

    public Map<Integer, List<Integer>> getBuffer() {
        return buffer;
    }

    public void setBuffer(Map<Integer, List<Integer>> buffer) {
        this.buffer = buffer;
    }



}
