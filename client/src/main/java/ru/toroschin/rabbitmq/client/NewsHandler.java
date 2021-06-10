package ru.toroschin.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class NewsHandler {
    private static final String EXCHANGE_NAME = "itNews";
    private static final String SET_COMMAND = "set_topic ";
    private static final String DEL_COMMAND = "del_topic ";
    private ConnectionFactory factory;
    private Map<String, String> topics;
    private Connection connection;
    private Channel channel;

    public void run() throws IOException, TimeoutException {
        topics = new HashMap<>();
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        Scanner scanner = new Scanner(System.in);
        String message = "";
        while (true) {
            System.out.println("Введите команду:");
            if (scanner.hasNextLine()) {
                message = scanner.nextLine();
                if (message.startsWith(SET_COMMAND)) {
                    message = message.substring(SET_COMMAND.length());
                    subscribe(message);
                } else if (message.startsWith(DEL_COMMAND)) {
                    message = message.substring(DEL_COMMAND.length());
                    unsubscribe(message);
                } else {
                    System.out.println("Неверная команда");
                }
            }
        }
    }

    public void subscribe(String topic) throws IOException, TimeoutException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueChannelName = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queueChannelName);

        channel.queueBind(queueChannelName, EXCHANGE_NAME, topic);

        System.out.println("Оформлена подписка на тему: " + topic);
        System.out.println(" [*] Ждем новостей");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Получена новость: '" + message + "'");
        };

        channel.basicConsume(queueChannelName, true, deliverCallback, consumerTag ->
        {
        });
        topics.put(topic, queueChannelName);
    }

    public void unsubscribe(String topic) throws IOException {
        channel.queueDelete(topics.get(topic));
        topics.remove(topic);
    }

}
