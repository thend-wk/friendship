package com.thend.friendship.mq;

import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Address;

public abstract class RabbitMQ {
	
	protected String uri;
	protected String exchange;
	protected String routingKey;
	protected Address[] addrArr;
	
	protected Address[] getRabbitAddr(String clusterAddr) {
		List<Address> addList = new ArrayList<Address>();
		String[] hostPortPairs = clusterAddr.split(",");
		for(String hostPortPair : hostPortPairs) {
			String[] items = hostPortPair.split(":");
			if(items.length > 1) {
				Address addr = new Address(items[0], Integer.parseInt(items[1]));
				addList.add(addr);
			}
		}
		return addList.toArray(new Address[0]);
	}
}
