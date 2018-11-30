import java.util.LinkedList;
import java.util.List;

public class Token {

    private List<Integer> TN;

    private List<State> TS;

    public Token(){
        TN = new LinkedList<Integer>();
        TS =new LinkedList<State>();
    }

    public void setTN(List<Integer> TN) {
        this.TN.clear();

        for(Integer i: TN)
            this.TN.add(new Integer(i));
    }

    /**
     *
     * @param index the index to assert number
     * @param num number of requests
     */
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


}
