import org.apache.log4j.Logger;
import java.rmi.RemoteException;

public class SleepCS implements Runnable{
    private Singhal_RMI singhal;
    private Token token;
    final static Logger logger = Logger.getLogger(Singhal.class);

    public SleepCS(Singhal_RMI singhal, Token token){
        this.singhal = singhal;
        this.token=token;
    }


    public void run(){
        try{
            singhal.proessToken(token);
        }catch (RemoteException e){
            e.printStackTrace();
        }

    }

}
