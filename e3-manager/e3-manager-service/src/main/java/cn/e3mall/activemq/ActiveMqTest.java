package cn.e3mall.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import javax.jms.*;

public class ActiveMqTest {

    /**
     * 点到点形式发送消息
     */
    @Test
    public void  testQueueProduct() throws Exception{
        //1、创建一个工厂对象，需要指定服务的ip及端口
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
        //2、使用工厂对象创建一个Connection对象
        Connection connection = connectionFactory.createConnection();
        //3、开启连接，调用Connection对象的start方法
        connection.start();
        //4、创建一个Session对象
        //第一个参数：是否开启事物。如果开启，第二个参数无意义。一般不开启。
        //第二个参数：应答模式。自动或手动。一般自动。
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //5、使用Session对象创建一个Destination对象。两种形式queue、topic，现在用queue
        Queue queue = session.createQueue("test-queue");
        //6、使用Session对象创建一个Producer对象
        MessageProducer producer = session.createProducer(queue);
        //7、创建一个Message对象，可以使用TextMessage
        /*TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("hello Activemq");*/
        TextMessage textMessage = session.createTextMessage("hello Activemq");
        //8、发送消息
        producer.send(textMessage);
        //9、关闭资源
        producer.close();
        session.close();;
        connection.close();
    }

    @Test
    public void testQueueConsumer() throws Exception{
        //创建一个ConnectionFactory对象连接MQ服务器
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
        //创建一个连接
        Connection connection = connectionFactory.createConnection();
        //开启连接
        connection.start();
        //使用Connection对象创建一个Session对象
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //创建一个Destination对象，queue
        Queue queue = session.createQueue("spring-queue");
        //使用Session对象创建一个消费者对象
        MessageConsumer consumer = session.createConsumer(queue);
        //接收消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                //打印结果
                TextMessage textMessage = (TextMessage) message;
                try {
                    String text = textMessage.getText();
                    System.out.println(text);
                }catch (JMSException e){
                    e.printStackTrace();
                }
            }
        });
        //等待接收消息
        System.in.read();
        //关闭资源
        consumer.close();
        session.close();
        connection.close();
    }

    @Test
    public void testTopicProducer() throws Exception{
        //1、创建一个工厂对象，需要指定服务的ip及端口
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
        //2、使用工厂对象创建一个Connection对象
        Connection connection = connectionFactory.createConnection();
        //3、开启连接，调用Connection对象的start方法
        connection.start();
        //4、创建一个Session对象
        //第一个参数：是否开启事物。如果开启，第二个参数无意义。一般不开启。
        //第二个参数：应答模式。自动或手动。一般自动。
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //5、使用Session对象创建一个Destination对象。两种形式queue、topic，现在用topic
        Topic topic = session.createTopic("test-topic");
        //6、使用Session对象创建一个Producer对象
        MessageProducer producer = session.createProducer(topic);
        //7、创建一个Message对象，可以使用TextMessage
        /*TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("hello Activemq");*/
        TextMessage textMessage = session.createTextMessage("topic message");
        //8、发送消息
        producer.send(textMessage);
        //9、关闭资源
        producer.close();
        session.close();;
        connection.close();
    }

    @Test
    public void testTopicConsumer() throws Exception{
        //创建一个ConnectionFactory对象连接MQ服务器
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
        //创建一个连接
        Connection connection = connectionFactory.createConnection();
        //开启连接
        connection.start();
        //使用Connection对象创建一个Session对象
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //创建一个Destination对象，topic
        Topic topic = session.createTopic("test-topic");
        //使用Session对象创建一个消费者对象
        MessageConsumer consumer = session.createConsumer(topic);
        //接收消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                //打印结果
                TextMessage textMessage = (TextMessage) message;
                try {
                    String text = textMessage.getText();
                    System.out.println(text);
                }catch (JMSException e){
                    e.printStackTrace();
                }
            }
        });
        System.out.println("topic消费者启动");
        //等待接收消息
        System.in.read();
        //关闭资源
        consumer.close();
        session.close();
        connection.close();
    }
}
