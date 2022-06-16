package mom;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


public class Subscriber implements MessageListener {

 	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
 	
 	private MomListener momListener;

	public Subscriber() {
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			Connection connection = connectionFactory.createConnection();
			connection.start();
			
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination dest = session.createTopic("CHAT_SPY");
			MessageConsumer subscriber = session.createConsumer(dest);
			subscriber.setMessageListener(this);	
			
			this.momListener = new MomListener();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 	
	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				momListener.receiveMessage(((TextMessage) message).getText());
			} catch (Exception e) {
				e.printStackTrace();			
			}
		}
	}
}
