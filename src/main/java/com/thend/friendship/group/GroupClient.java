package com.thend.friendship.group;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;


public class GroupClient {
	
	private static final Log logger = LogFactory.getLog(GroupClient.class);
	
	private JChannel channel = null;
	
	public GroupClient(String clusterName) {
		try {
			channel = new JChannel();
			channel.setReceiver(new GroupReceiver());
			channel.connect(clusterName);
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					channel.close();
				}
			}));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public void unicast(Address addr, Object obj) {
		try {
			channel.send(new Message(addr, obj));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public void multicast(Object obj) {
		try {
			channel.send(new Message(null, obj));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public List<Address> getChannelMembers() {
		 return channel.getView().getMembers();
	}
	
	static class GroupReceiver extends ReceiverAdapter {
		@Override
		public void receive(Message msg) {
			System.out.println("received msg from " + msg.getSrc() + ": " + msg.getObject());
		}
	}
	
	public static void main(String[] args) {
		Thread t1 = new Thread(new Runnable() {
			
			public void run() {
				GroupClient client = new GroupClient("testCluster");
			}
		});
		Thread t2 = new Thread(new Runnable() {
			
			public void run() {
				GroupClient client = new GroupClient("testCluster");
				client.multicast("hello world, i am t2");
				List<Address> members = client.getChannelMembers();
				client.unicast(members.get(0), "hello");
			}
		});
		try {
			t1.start();
			t1.join();
			t2.start();
			Thread.sleep(1000*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
