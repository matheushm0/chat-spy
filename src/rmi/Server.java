package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import mom.Publisher;

public class Server extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = 1L;
	
	private Publisher publisher;

	public Server() throws RemoteException {
		super();
		
		System.out.println("----Starting Server----");
		
		this.publisher = new Publisher();
	}

	@Override
	public void sendMessageToTopic(String watchedWords, String username, String content) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\nO usuario " + username + " enviou uma mensagem que contem uma das palavras monitoradas");
		sb.append("\nA mensagem foi: " + content );
		sb.append("\n\n----- Palavras Monitoradas -----\n\n");
		sb.append(watchedWords);
		sb.append("\n\n-----------------------------------------");
		
		publisher.sendMessage(sb.toString());
	}
}
