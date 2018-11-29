import java.util.LinkedList;
import java.util.List;

public class Token {
    private List<Integer> TN =new LinkedList<Integer>();

    private List<state> TS =new LinkedList<state>();

    public void setTN(List<Integer> TN) {
        this.TN = TN;
    }

    public void setTS(List<state> TS) {
        this.TS = TS;
    }

    public List<Integer> getTN() {
        return TN;
    }

    public List<state> getTS() {
        return TS;
    }
}
