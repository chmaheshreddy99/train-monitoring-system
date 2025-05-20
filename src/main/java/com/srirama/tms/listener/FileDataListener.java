package com.srirama.tms.listener;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Service;

import com.srirama.tms.DataConstants;

@Service(DataConstants.FILE_DATA_LISTENER)
public class FileDataListener implements DataListener<String>{
	
	private static final Path STORAGE_DIR = Paths.get("pcb-data");
	
    private String fileName;
    
    private final BlockingQueue<String> queue;
    
    private volatile boolean running = false;
    
    private Thread workerThread;

    public FileDataListener() {
        this.fileName = "data.txt";
        this.queue = new LinkedBlockingQueue<>();
    }

    public void start() {
        if (workerThread != null && workerThread.isAlive()) return;

        running = true;
        workerThread = new Thread(this::watchFile, "FileRecordWatcher");
        workerThread.start();
    }

    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    @Override
    public BlockingQueue<String> getQueue() {
        return queue;
    }

    private void watchFile() {
        File file = STORAGE_DIR.resolve(fileName).toFile();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long lastKnownPosition = file.length(); // Start from the end of file
            raf.seek(lastKnownPosition);

            while (running) {
                long fileLength = file.length();
                if (fileLength > lastKnownPosition) {
                    raf.seek(lastKnownPosition);
                    String line;
                    while ((line = raf.readLine()) != null) {
                        queue.put(line);
                    }
                    lastKnownPosition = raf.getFilePointer();
                }
                Thread.sleep(32); // Small delay to avoid CPU hog
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
