import java.util.LinkedList;
import java.util.List;

public class Token {
    private List<Integer> TN =new LinkedList<Integer>();

    private List<state> TS =new LinkedList<state>();

    public void setTN(List<Integer> TN) {
        this.TN = TN;
    }

    /**
     *
     * @param index the index to assert number
     * @param num number of requests
     */
    public void setTN(int index, Integer num) {
        this.TN.set(index, num);
    }

    public void setTS(List<state> TS) {
        this.TS = TS;
    }

    public void setTS(int index,state state0) {
        this.TS.set(index,state0);
    }

    public List<Integer> getTN() {
        return TN;
    }

    public List<state> getTS() {
        return TS;
    }


}
