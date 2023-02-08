package de.wejago.hichi2influxDB;

import de.wejago.hichi2influxDB.service.MqttSubscriber2;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@SpringBootApplication
public class Hichi2influxDbApplication {

	public static void main(String[] args) throws MqttException {
		SpringApplication.run(Hichi2influxDbApplication.class, args);
//		MqttSubscriber subscriber = new MqttSubscriber("tcp://localhost:1883", "spring-boot-mqtt-subscriber", "iothon/testing/temperature");
//		subscriber.start();
		MqttSubscriber2 subscriber2 = new MqttSubscriber2("tcp://localhost:1883", "spring-boot-mqtt-subscriber", "iothon/testing/temperature");
		subscriber2.start();
	}

	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageProducer inbound() {
		MqttPahoMessageDrivenChannelAdapter adapter =
			new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883", "testClient",
			                                        "topic1", "topic2");
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println(message.getPayload());
			}

		};
	}
}
