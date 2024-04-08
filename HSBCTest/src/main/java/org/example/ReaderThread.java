package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReaderThread implements Runnable {
    private final RandomAccessFile inputFile;
    private final List<Byte> buffer;
    private final AtomicBoolean endOfFile;

    public ReaderThread(RandomAccessFile randomAccessFile, List<Byte> buffer, AtomicBoolean endOfFile) {
        this.inputFile = randomAccessFile;
        this.buffer = buffer;
        this.endOfFile = endOfFile;
    }

    public void run() {
        try {
            long fileSize = inputFile.length();
            long position = fileSize - 1;
            while (position >= 0) {
                inputFile.seek(position);
                byte feed = inputFile.readByte();
                feedToBuffer(feed);
                position--;
            }
            setEndOfFile();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    void feedToBuffer(byte feed) throws InterruptedException {
        synchronized (buffer) {
            if (buffer.size() == Main.BUFFER_LEN) {
                buffer.wait();
            }
            buffer.add(feed);
            buffer.notify();
        }
    }

    void setEndOfFile() {
        this.endOfFile.set(true);
        synchronized (buffer) {
            buffer.notify();
        }
    }
}
