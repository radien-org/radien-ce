package io.radien.ms.usermanagement.mdb;


import java.util.ArrayList;
import java.util.List;


import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * The @MessageDriven annotation is pretty powerful and can replace much of the configuration
 * formally done either using the API or XML files.  In this case we are indicating that this
 * MBD responds to messages sent to a Topic named "SpiderTopic". Using the the 
 * @ActiveConfigProperty you can specify everything from Acknowledgement Mode to Message 
 * Selectors.
 */
@MessageDriven(
	activationConfig = { 
		@ActivationConfigProperty(
				propertyName = "destinationType", 
				propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty( 
				propertyName = "destination", 
				propertyValue = "SpiderTopic") 
	}
)

/*
 * The SpiderMBD receives a URL for a web page, downloads the page, and then extracts
 * all of the URLs for images (<img src>) as well as all the links (<a href>). The image
 * URLs are then sent, via JMS, to the ImageQueue and the links are sent to the LinkQueue
 * to be processed by other components.
 * 
 */
public class SpiderMDB implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(SpiderMDB.class);

	/*
	 * The javax.jms.MessageListener interface must be implemented by MDBs. It declares 
	 * the onMessage(Message message) method which is called when a messages is delivered to 
	 * MBD from the Topic or Queue to which it is subscribed.
	 * 
	 * 
	 * In the case of a Topic, every MDB listening to the same
	 * Topic gets a copy of the message. In the case of a Queue, the MDBs 
	 * wait in a queue (thus the name). When a message is delivered to the Queue
	 * the MDB in the front gets the message and moves to the back of the 
	 * queue so that the next MDB in line gets the next message.
	 */
    public void onMessage(Message message) {

        try {
        	
            /* 
             * Messages received from a Queue or Topic are delivered as a Message object. 	
             * There are a few different kinds Message objects (text, binary, object, 
             * IO stream). In this case we are expecting a text message which is one of
             * the simplest. JMS Topics and Queues support the same message types.
             */
        	TextMessage msg = (TextMessage)message;
        	log.error(msg.getText());
        	if (msg.getText().equals("spider")){
        		forwardList(new ArrayList<>(),"nextQueue");
			}
        	/*
        	 * The JSoup library is free on GitHub and is used to parse out elements
        	 * of any HTML page.  The Parser object is a wrapper this example uses
        	 * to hide the details of the JSoup API calls which are not important to this
        	 * tutorial.  When the Parser is created it will actually download
        	 * and store the HTML page and then parse it to get all the URLs from the 
        	 * HTML <img src/> tags and the <a href/> elements.
        	 */

			
			/*
			 * The links from the <img src/> and <a href/> elements are passed to the
			 * forwardList( ) method which will take care of sending them to the  
			 * Queue specified in its second parameter.
			 */


	        
		} catch (Exception e) {
			e.printStackTrace();
		} 
        
    }
    
    /*
     * This method creates a JMS producer which is then used to send each URL (either 
     * a link or image) to the Queue named in the second parameter.
     * 
     * In this case we are using the ActiveMQ TCP JMS library which connects to an external
     * ActiveMQ instance.
     */
    private void forwardList(List<String> items, String queueName) throws JMSException {

		/*  Every JMS provider (every library that implements the JMS API)
		 *  will have its own implementation of the javax.jms.ConnectionFactory.
		 *
		 *  The purpose of the ConnectionFactory is to create a network connection
		 *  to a specific JMS broker, such as ActiveMQ, or a specific protocol,
		 *  such as TCP.  This allows the JMS library to send and receive messages
		 *  over a network from the broker.
		 *
		 *  In this case we are using the Apache TCP JMS library which is specific
		 *  to the protocol, OpenWire. OpenWire is only one of ten protocols currently
		 *  supported by ActiveMQ.
		 */
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		try (Connection connection = factory.createConnection("admin", "admin")) {
			connection.start();

			/*  Every JMS Connection can have multiple sessions which manage things like
			 *  transactions and message persistence separately.  In practice multiple sessions
			 *  are not used much by developers but may be used in more sophisticated
			 *  application servers to conserve resources.
			 */
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			/*
			 * A Destination is a Queue, identified by the 'queueName', to which a URL is sent.
			 */
			Destination destination = session.createQueue(queueName);

			/*
			 * A MessageProducer is specific to a destination - it can only send
			 *  messages to one Topic or Queue.
			 */
			MessageProducer producer = session.createProducer(destination);

			/*
			 * This for loop simply sends each URL in the items List<String>
			 * as the body of a TextMessage type sent to the specific Queue
			 */
			for (String item : items) {

				/* Every time you want to send a message to a Queue or Topic you have to create
				 * a Message object. There are a few different kinds (text, binary, object,
				 * IO stream). In this case we are just sending a text message which is one of
				 * the simplest. JMS Topics and Queues support the same message types.
				 */
				TextMessage itemMsg = session.createTextMessage(item);
				producer.send(itemMsg);
			}
		}
	}

}
