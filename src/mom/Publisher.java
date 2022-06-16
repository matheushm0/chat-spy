package mom;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	private MessageProducer publisher;
	private Session session;
	
	public void sendMessage(String messageToSend) {
		TextMessage message;
		try {
			message = session.createTextMessage();
			message.setText(messageToSend);
			publisher.send(message);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Publisher() {
		try {
			String topic = "CHAT_SPY";
			
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			Connection connection = connectionFactory.createConnection();
			connection.start();
			
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination dest = session.createTopic(topic);
			
			publisher = session.createProducer(dest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
