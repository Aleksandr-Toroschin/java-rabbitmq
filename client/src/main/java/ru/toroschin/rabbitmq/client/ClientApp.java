package ru.toroschin.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ClientApp {
    private static final String PREFIX = "set_topic ";
    private static final String EXCHANGE_NAME = "itNews";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = getTopic();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueChannelName = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queueChannelName);

        channel.queueBind(queueChannelName, EXCHANGE_NAME, queueName);

        System.out.println("Оформлена подписка на тему: "+queueName);
        System.out.println(" [*] Ждем новостей");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Получена новость: '" + message + "'");
        };

        channel.basicConsume(queueChannelName, true, deliverCallback, consumerTag ->
        {
        });
    }

    public static String getTopic() {
        Scanner scanner = new Scanner(System.in);
        String message = "";
        System.out.println("Введите команду:");
        if (scanner.hasNextLine()) {
            message = scanner.nextLine();
            if (message.startsWith(PREFIX)) {
                message = message.substring(PREFIX.length());
            } else {
                System.out.println("Неверная команда");
                getTopic();
            }
        }
        return message;
    }

}
