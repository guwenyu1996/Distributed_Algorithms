import java.io.Serializable;
import java.util.*;

public class Message implements Serializable {

    private int destId;                         // id of destination process
    private int srcId;                          // id of source process
    private int delay;                          // the delay for receiving current message

    private String content;                     // content of message
    private List<Integer> ts;
    private Map<Integer, List<Integer>> buffer;

    public Message(int srcId, int destId, int delay){
        this.destId = destId;
        this.srcId = srcId;
        this.delay = delay;

        ts = new ArrayList<Integer>();
        buffer = new HashMap<Integer, List<Integer>>();
    }

    public int getSrcId() {
        return srcId;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDestId() {
        return destId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<Integer, List<Integer>> getBuffer() {
        return buffer;
    }

    public void setBuffer(Map<Integer, List<Integer>> buffer) {
        this.buffer.clear();

        for(Map.Entry<Integer, List<Integer>> iter: buffer.entrySet())
            this.buffer.put(new Integer(iter.getKey()), copyList(iter.getValue()));
    }

    public List<Integer> getTs() {
        return ts;
    }

    public void setTs(List<Integer> clock) {
        this.ts.clear();

        this.ts = copyList(clock);
    }


    private List<Integer> copyList(List<Integer> list){
        List<Integer> temp = new ArrayList<Integer>();

        for(Integer i: list)
            temp.add(new Integer(i));

        return temp;
    }


}
