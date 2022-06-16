package chat;

import java.rmi.Naming;

import chat.ui.SpyWindow;
import rmi.Server;

public class SpyChatMain {

	public static void main(String[] args) {
		try {			
			Server server = new Server();
			Naming.rebind("//localhost/ServerRef", server);
			
			System.out.println("Waiting Connections...");
		}
		catch (Exception e) {
			System.out.println("Exception - main()");
		}
		
		new SpyWindow();
	}
	
}
