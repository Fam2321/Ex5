/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enterprise_ex5_producer;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 *
 * @author 60050257
 */
public class Main {
    @Resource(mappedName = "jms/SimpleJMSTopic")
    private static Topic topic;
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/SimpleJMSQueue")
    private static Queue queue;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection connection = null;
        String msg = "\0";

        if ((args.length < 1) || (args.length > 2)) {
            System.err.println(
                    "Program takes one or two arguments: "
                    + "<dest_type> [<number-of-messages>]");
            System.exit(1);
        }

        String destType = args[0];
        System.out.println("Destination type is " + destType);

        if (!(destType.equals("queue") || destType.equals("topic"))) {
            System.err.println("Argument must be \"queue\" or " + "\"topic\"");
            System.exit(1);
        }

        Destination dest = null;

        try {
            if (destType.equals("queue")) {
                dest = (Destination) queue;
            } else {
                dest = (Destination) topic;
            }
        } catch (Exception e) {
            System.err.println("Error setting destination: " + e.toString());
            System.exit(1);
        }

        
        try {
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(dest);
            TextMessage message = session.createTextMessage();
            Scanner scn = new Scanner(System.in);
            while (true) {
                System.out.print("Enter Live Score: ");
                msg = scn.nextLine();
                if(((msg.equals("q")) || (msg.equals("Q")))) break;
                message.setText(msg);
                producer.send(message);
            }
            producer.send(session.createMessage());
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
