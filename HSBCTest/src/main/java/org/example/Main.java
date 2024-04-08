package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static int BUFFER_LEN = 2048;
    private static final AtomicBoolean END_OF_FILE = new AtomicBoolean(false);

    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        if (args.length != 2) {
            System.exit(256);
        }

        String inputFilePath = args[0];
        String outputFilePath = args[1];
        List<Byte> buffer = new ArrayList<>();
        printTime();

        try (RandomAccessFile inputFile = new RandomAccessFile(inputFilePath, "r");
             RandomAccessFile outputFile = new RandomAccessFile(outputFilePath, "rw")) {

            Thread writerThread = new Thread(new WriterThread(outputFile, buffer, END_OF_FILE));
            Thread readerThread = new Thread(new ReaderThread(inputFile, buffer, END_OF_FILE));

            writerThread.start();
            readerThread.start();

            writerThread.join();
            readerThread.join();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        printTime();
    }

    private static void printTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTime = currentTime.format(formatter);
        System.out.println("Current time with milliseconds: " + formattedTime);
    }

}