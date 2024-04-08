package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ReaderThreadTest {

    @Mock
    private RandomAccessFile mockRandomAccessFile;

    private List<Byte> buffer;
    private AtomicBoolean endOfFile;
    private ReaderThread readerThread;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        buffer = new ArrayList<>();
        endOfFile = new AtomicBoolean(false);
        readerThread = new ReaderThread(mockRandomAccessFile, buffer, endOfFile);
    }

    @Test
    public void testRunReadsFileCorrectly() throws IOException {
        int[] testData = {65, 66, 67}; // ASCII values for 'A', 'B', 'C'
        when(mockRandomAccessFile.length()).thenReturn((long) testData.length);
        //Mock the read in reverse order
        when(mockRandomAccessFile.read()).thenReturn(testData[2], testData[1], testData[0]);

        readerThread.run();

        assertEquals(testData[0], (int) buffer.get(2));
        assertEquals(testData.length, buffer.size());
    }

    @Test
    public void testRunHandlesIOException() throws IOException {
        when(mockRandomAccessFile.length()).thenThrow(new IOException("Simulated IO exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> readerThread.run());
        assertEquals(IOException.class, exception.getCause().getClass());
    }

    @Test
    public void testFeedToBuffer() throws InterruptedException {
        byte testData = 65;

        readerThread.feedToBuffer(testData);

        assertEquals(testData, buffer.getFirst());
    }

    @Test
    public void testSetEndOfFile() {
        readerThread.setEndOfFile();

        assertTrue(endOfFile.get());
    }
}
