package com.srirama.tms.listener;

import java.util.concurrent.BlockingQueue;

public interface DataListener<T> {
	
	public BlockingQueue<T> getQueue();
	
	public void start();
	
	public void stop();

}
