package com.thend.friendship.mq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitMQListener {

	private static final Log logger = LogFactory.getLog(RabbitMQListener.class);

	private String uri;

	private String exchange;

	private String routingKey;

	public RabbitMQListener(String uri, String exchange, String routingKey) {
		this.uri = uri;
		this.exchange = exchange;
		this.routingKey = routingKey;
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				start();
			}
		});
		t.setDaemon(true);
		t.start();
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public void start() {
		ConnectionFactory factory = new ConnectionFactory();
		QueueingConsumer consumer = null;
		try {
			factory.setUri(uri);
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exchange, "direct");
			String queueName = channel.queueDeclare().getQueue();

			channel.queueBind(queueName, exchange, routingKey);

			consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);
			logger.info(String.format("Start to listen RabbitMQ:[%1s], Exchange:[%2s], RoutingKey:[%3s]", 
					uri, exchange, routingKey));
		} catch (Exception e) {
			logger.warn("监听队列无法启动，请查看相关配置是否正常", e);
			throw new RuntimeException("监听队列无法启动，请查看相关配置是否正常", e);
		}

		while (true) {
			try {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				logger.info("receive message : " + message);
			} catch (Exception e) {
				logger.warn("消息处理时出现异常", e);
			}
		}
	}

}
