import java.util.List;
import java.util.Map;

public class Message {

    private int destId;                         // id of destination process
    private int srcId;                          // id of source process
    private int delay;                          // the delay for receiving current message

    private String content;                     // content of message
    private List<Integer> ts;
    private Map<Integer, List<Integer>> buffer;

    public Message(int destId, int srcId, int delay){
        this.destId = destId;
        this.srcId = srcId;
        this.delay = delay;
    }

    public int getSrcId() {
        return srcId;
    }

    public int getDelay() {
        return delay;
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
        this.buffer = buffer;
    }

    public List<Integer> getTs() {
        return ts;
    }

    public void setTs(List<Integer> ts) {
        this.ts = ts;
    }


}
