/**
 * since the connect message can be appended to the queue and be postponed,
 * i use this class to store the arguments of a connect function
 */
public class Connect_massage {
    int src;
    int level;
    public Connect_massage(int src, int level){
        this.src = src;
        this.level = level;
    }
}
