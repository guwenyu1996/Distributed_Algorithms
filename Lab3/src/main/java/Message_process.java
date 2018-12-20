public class Message_process implements Runnable {

    MessageType messagetype;
    MST_RMI node;
    int src;
    int level;
    int fragment;
    State_node state;
    int weight;

    public Message_process(MessageType messagetype, MST_RMI node, int src, int level, int fragment, State_node state, int weight){
        this.messagetype = messagetype;
        this.node = node;
        this.src = src;
        this.level = level;
        this.fragment = fragment;
        this.state = state;
        this.weight = weight;
    }

    public void run() {
 //       switch (messagetype)
    }
}
