/**
 * since the connect message can be appended to the queue and be postponed,
 * i use this class to store the arguments of a connect function
 */
public class Message {

    private MessageType type;
    private int src;
    private int level;
    private int weight;
    private int fragment;
    private int sequence;

    public Message(MessageType type, int src){
        this.src = src;
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public int getFragment() {
        return fragment;
    }

    public int getWeight() {
        return weight;
    }

    public int getLevel() {
        return level;
    }

    public int getSrc() {
        return src;
    }

    public void setFragment(int fragment) {
        this.fragment = fragment;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

}
