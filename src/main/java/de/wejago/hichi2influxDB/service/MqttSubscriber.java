package de.wejago.hichi2influxDB.service;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(MqttSubscriber2.class);

    private final String broker;
    private final String clientId;
    private final String topic;

    public MqttSubscriber(String broker, String clientId, String topic) {
        this.broker = broker;
        this.clientId = clientId;
        this.topic = topic;
    }

    public void start() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        MemoryPersistence persistence = new MemoryPersistence();
        logger.info("Started listening for messages in start()...");

        try {
            IMqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            mqttClient.connect(connectOptions);
            logger.info("Emo MQTT: just before the subscribe");
            mqttClient.subscribe(topic, (topic, message) -> {
                MqttMessage mqttMessage = (MqttMessage) message;
                logger.info("Received message: " + new String(mqttMessage.getPayload()));
            });
            logger.info("Emo MQTT: right after the subscribe");
        } catch (MqttException e) {
            logger.error("Error subscribing to MQTT topic", e);
        }
    }
}
