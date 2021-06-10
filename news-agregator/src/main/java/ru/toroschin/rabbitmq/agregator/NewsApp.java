package ru.toroschin.rabbitmq.agregator;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NewsApp {
    public static void main(String[] args) throws IOException, TimeoutException {
        ScannerSender scannerSender = new ScannerSender();
        scannerSender.run();
    }

}
