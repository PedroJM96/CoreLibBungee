package com.pedrojm96.core.bungee;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitmqMessagingService {

	private Connection connection;
    private Channel channel;
    
    private static String EXCHANGE_NAME = "pro";
	
	
    private CoreLog log;
    
	public RabbitmqMessagingService(CoreLog log, String exchange_name,String host,String virtual_host, int port,String username,String password) {
		this.log = log;
		EXCHANGE_NAME = exchange_name;
		ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtual_host);
        factory.setPort(port);
        factory.setHost(host);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(TimeUnit.SECONDS.toMillis(1));
        
        try{
        	connection = factory.newConnection();
        	channel = connection.createChannel();
        	log.error("Connection with rabbitmq");
        	
        }catch (IOException|TimeoutException exception){
        	log.error("Connection error with rabbitmq");
        	log.error(exception.getMessage());
        }
	}
	
	
	public void join(String canal, DeliverCallback deliverCallback){
        try {
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
			String nombre_de_cola = channel.queueDeclare().getQueue();
			channel.queueBind(nombre_de_cola, EXCHANGE_NAME, canal);
	        channel.basicConsume(nombre_de_cola, true, deliverCallback, consumerTag -> {});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }
	
	public void close(){
        try {
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }
	
	 public void send(String canal, JsonObject json) { 
		 try {
			 if(json.isJsonNull()) {
				 log.error("RabbitmqMessagingService.send json is null");
				 return;
			 }
			 String jsonString = json.toString();
			 if(jsonString == null) {
				 log.error("RabbitmqMessagingService.send jsontoString is null");
				 return;
			 }
			 if(jsonString.isEmpty()) {
				 log.error("RabbitmqMessagingService.send jsontoString is empty");
				 return;
			 }
			 channel.basicPublish(EXCHANGE_NAME,canal, null, json.toString().getBytes());
			 log.debug("[x] Sent '" + canal + "':'" + json.toString() + "'");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
