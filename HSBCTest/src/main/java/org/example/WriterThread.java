package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WriterThread implements Runnable {

    private final RandomAccessFile outputFile;
    private final List<Byte> buffer;
    private final AtomicBoolean endOfFile;

    //Using low level sync apis for queue intentionally
    //Alternate way would be to use BlockingQueue implementation

    public WriterThread(RandomAccessFile randomAccessFile, List<Byte> buffer, AtomicBoolean endOfFile) {
        this.outputFile = randomAccessFile;
        this.buffer = buffer;
        this.endOfFile = endOfFile;
    }

    public void run() {
        while (!endOfFile.get() || !buffer.isEmpty()) {
            synchronized (buffer) {
                if (!buffer.isEmpty()) {
                    try {
                        outputFile.write(buffer.getFirst());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    buffer.removeFirst();
                    buffer.notify();
                } else {
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }

    }
}

