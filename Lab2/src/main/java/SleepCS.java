import org.apache.log4j.Logger;
import java.rmi.RemoteException;

public class SleepCS implements Runnable{
    private DA_Singhal_RMI singhal;
    private Token token;
    final static Logger logger = Logger.getLogger(DA_Singhal.class);

    public SleepCS(DA_Singhal_RMI singhal, Token token){
        this.singhal = singhal;
        this.token=token;
    }


    public void run(){
        try{
            singhal.processToken(token);
        }catch (RemoteException e){
            e.printStackTrace();
        }

    }

}
