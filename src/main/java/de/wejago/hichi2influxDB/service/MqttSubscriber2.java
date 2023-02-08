package de.wejago.hichi2influxDB.service;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttSubscriber2 implements MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(MqttSubscriber2.class);

    private final MqttClient client;
    private final String topic;

    public MqttSubscriber2(String broker, String clientId, String topic) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        this.client = new MqttClient(broker, clientId, persistence);
        this.topic = topic;
    }

    public void start() {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            client.connect(connectOptions);
            client.subscribe(topic);
            log.info("Subscribed to topic: " + topic);
        } catch (MqttException e) {
            log.error("Error subscribing to MQTT topic", e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error("MQTT connection lost", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.info("Received message on topic " + topic + ": " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("Delivery complete for message: " + token.getMessageId());
    }
}
