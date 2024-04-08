package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class WriterThreadTest {

    private final List<Byte> buffer = new ArrayList<>();

    @Test
    public void testRun() throws IOException, InterruptedException {
        RandomAccessFile outputFile = mock(RandomAccessFile.class);
        AtomicBoolean endOfFile = new AtomicBoolean(false);
        WriterThread writerThread = new WriterThread(outputFile, buffer, endOfFile);

        Thread thread = new Thread(writerThread);
        thread.start();

        // Add some bytes to the buffer
        for (int i = 0; i < 10; i++) {
            synchronized (buffer) {
                buffer.add((byte) i);
                buffer.notify();
            }
        }
        endOfFile.set(true);
        synchronized (buffer) {
            buffer.notify();
        }

        // Wait for the thread to finish writing
        thread.join();
        // Verify that the bytes were written to the output file exactly once
        Mockito.verify(outputFile, times(1)).write((byte) 0);
        Mockito.verify(outputFile, times(1)).write((byte) 1);
        Mockito.verify(outputFile, times(1)).write((byte) 2);
        Mockito.verify(outputFile, times(1)).write((byte) 3);
        Mockito.verify(outputFile, times(1)).write((byte) 4);
        Mockito.verify(outputFile, times(1)).write((byte) 5);
        Mockito.verify(outputFile, times(1)).write((byte) 6);
        Mockito.verify(outputFile, times(1)).write((byte) 7);
        Mockito.verify(outputFile, times(1)).write((byte) 8);
        Mockito.verify(outputFile, times(1)).write((byte) 9);

        //verify that unwanted integer was not called out
        Mockito.verify(outputFile, times(0)).write((byte) 10);


    }

    @Test
    public void testRunWithEmptyBuffer() throws InterruptedException {
        RandomAccessFile outputFile = mock(RandomAccessFile.class);
        buffer.clear();
        AtomicBoolean endOfFile = new AtomicBoolean(false);

        WriterThread writerThread = new WriterThread(outputFile, buffer, endOfFile);

        Thread thread = new Thread(writerThread);
        thread.start();

        endOfFile.set(true);
        synchronized (buffer) {
            buffer.notify();
        }
        // Wait for the thread to finish without adding anything to the buffer
        thread.join();


        // Verify that nothing was written to the output file
        Mockito.verifyNoInteractions(outputFile);
    }
}
