package com.thend.friendship.mq;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQSender {
	
	private static final Log logger = LogFactory.getLog(RabbitMQSender.class);
	
	private String rabbitMQUri;

	private String exchangeName;
	
	private Address[] addrArr;
	
	private String routingKey;

	private Channel channel;

	private Connection connection;
	
	public RabbitMQSender(String rabbitMQUri, String exchangeName, Address[] addrArr) {
		this.rabbitMQUri = rabbitMQUri;
		this.exchangeName = exchangeName;
		this.addrArr = addrArr;
		start();
        // 退出时执行
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	release();
            }
        });
	}
	
	private void init() {
		ConnectionFactory factory = new ConnectionFactory();
		try {
			factory.setUri(rabbitMQUri);
			connection = factory.newConnection(addrArr);
		} catch (Exception e) {
			logger.error("与RabbitMQ创建连接时出现异常:", e);
		}
	}

	private void start() {
		// 启动时新建一个RabbitMQ的队列，利用Route模式，通过配置exchange来将消息转到对应的HostServer
		try {
			//初始化
			init();
			channel = connection.createChannel();
			channel.exchangeDeclare(exchangeName, "direct");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("与RabbitMQ通信发生异常:", e);
		}
	}
	
	public void release() {
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			logger.error("关闭RabbitMQ时发生异常:", e);
		}
	}
	
	public void dispatch(String msgJson) {
		try {
			channel.basicPublish(exchangeName, routingKey, null, msgJson.getBytes("UTF-8"));
			logger.info("send message, route:[" + routingKey + "], " + "message:[" + msgJson + "]");
		} catch (AlreadyClosedException e) {
			logger.error(e.getMessage());
			//重新初始化
			start();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
}
