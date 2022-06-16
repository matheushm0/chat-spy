package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	public void sendMessageToTopic(String word, String username, String content) throws RemoteException;
}
