import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Singhal_RMI extends Remote{


    void sendToken (int desId, Token token) throws RemoteException ;

    void receiveToken(Token token) throws RemoteException ;

    void sendRequest() throws RemoteException ;

    void receiveRequest(int src, int r) throws RemoteException ;

    void runCS() throws RemoteException ;

    void CS() throws RemoteException ;

}
