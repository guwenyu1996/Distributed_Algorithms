import java.rmi.RemoteException;

public class DelayedReceive implements Runnable{
    /*destination of sending*/
    private DA_Schiper_Eggli_Sandoz_RMI des;
    private Message message;
    public DelayedReceive(DA_Schiper_Eggli_Sandoz_RMI des, Message message){
        this.des = des;
        this.message = message;
    }

    public void run(){
        try{
            Thread.sleep(message.getDelay());
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        try{
            des.receive(message);
        } catch (
        RemoteException e1) {
            e1.printStackTrace();
        }
    }

}
