import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ReturnMessage implements Serializable {
    Map<MessageType,Integer> messageCount;
    private  int in_branch;
    private int merge;
    private int absorb;
    private int weight; ///weight of the in_branch

    public ReturnMessage(int in_branch, int weight, Map<MessageType,Integer> messageCount, int merge, int absorb){
        this.messageCount = messageCount;
        this.merge = merge;
        this.absorb = absorb;
        this.in_branch = in_branch;
        this.weight = weight;
    }

    public int getAbsorb() {
        return absorb;
    }

    public int getMerge() {
        return merge;
    }


    public int getIn_branch() {
        return in_branch;
    }

    public Map<MessageType, Integer> getMessageCount() {
        return messageCount;
    }

    public int getWeight() {
        return weight;
    }
}
