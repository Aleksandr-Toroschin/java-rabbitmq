package ru.toroschin.rabbitmq.agregator;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ScannerSender {
    private ConnectionFactory factory;
    private static final String EXCHANGE_NAME = "itNews";

    public void run() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        Scanner scanner = new Scanner(System.in);
        String message = "";
        String queueName;
        while (true) {
            System.out.println("Введите текст сообщения:");
            if (scanner.hasNextLine()) {
                message = scanner.nextLine();
                queueName = message.substring(0, message.indexOf(" "));
                message = message.substring(queueName.length()+1, message.length());
                publish(message, queueName);
                System.out.println("Опубликовано сообщение по теме "+queueName);
            }
        }
    }

    public void publish(String message, String queue_name) throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.basicPublish(EXCHANGE_NAME, queue_name, null, message.getBytes());

//            channel.queueDeclare(queue_name, false, false, false, null);
//            channel.basicPublish("", queue_name, null, message.getBytes());

    }
}
