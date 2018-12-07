import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Token implements Serializable {

    private List<Integer> TN;
    private List<State> TS;
    private Queue<Integer> queue;

    public Token(){
        TN = new LinkedList<Integer>();
        TS =new LinkedList<State>();
        queue = new LinkedList<Integer>();
    }

    public void setTN(List<Integer> TN) {
        this.TN.clear();

        for(Integer i: TN)
            this.TN.add(new Integer(i));
    }

    public void setTN(int index, Integer num) {
        this.TN.set(index, num);
    }

    public void setTS(List<State> TS) {
        this.TS.clear();

        for(State s: TS){
            switch (s){
                case E: this.TS.add(State.E); break;
                case H: this.TS.add(State.H); break;
                case R: this.TS.add(State.R); break;
                case O: this.TS.add(State.O); break;
            }
        }
    }

    public void setTS(int index,State state0) {
        this.TS.set(index,state0);
    }

    public List<Integer> getTN() {
        return TN;
    }

    public List<State> getTS() {
        return TS;
    }

    /**
     * Get the owner of token in next round
     * @return the index of process
     *         -1 if queue is empty
     */
    public int retrieveNextOwner(){
        if (queue.isEmpty())
            return -1;
        else
            return queue.poll();
    }

    /**
     * Add possible owner of token into waiting queue
     * @param index
     */
    public void addPossibleOwner(int index){
        if (!queue.contains(index))
           queue.add(index);
    }


}
