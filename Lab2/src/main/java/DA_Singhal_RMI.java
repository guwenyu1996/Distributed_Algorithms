import java.rmi.Remote;
import java.rmi.RemoteException;


public interface DA_Singhal_RMI extends Remote{


    void sendToken (int desId, Token token) throws RemoteException ;

    void receiveToken(Token token) throws RemoteException ;

    void sendRequest() throws RemoteException ;

    void receiveRequest(int src, int r) throws RemoteException ;

    void requestCS() throws RemoteException;

    void processToken(Token token) throws RemoteException;

}
