import java.rmi.RemoteException;

/**
 * The class to implement the delay of receiving
 */
public class DelayedReceive implements Runnable{
    /*destination of sending*/
    private DA_Schiper_Eggli_Sandoz_RMI des;
    private Message message;

    /**
     * Store the destination and message, then invoke method run()
     * @param des index of destination server
     * @param message message to be sent
     */
    public DelayedReceive(DA_Schiper_Eggli_Sandoz_RMI des, Message message){
        this.des = des;
        this.message = message;
    }

    public void run(){
        try{
            Thread.sleep(message.getDelay());
            des.receive(message);
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (
                RemoteException e1) {
            e1.printStackTrace();
        }

    }

}
