# hichi2influx

### Installation
1. Download the project
2. Create `.env` file in the main project directory using the provided `.env.example`. 
   1. Set proper values for Influx DB configuration.
   2. Set proper credentials for MQTT server (mosquitto).
      `echo -n "" > users`
   ```
   docker run --rm -v `pwd`/mosquitto.conf:/mosquitto/config/mosquitto.conf \
      -v `pwd`/users:/mosquitto/config/users eclipse-mosquitto \
      mosquitto_passwd -b /mosquitto/config/users <USER> <PASSWORD>
   ```
   Check that the file is updated: `deployment/users.conf`
3. Run `docker-compose up`

### Influx DB installation
https://docs.influxdata.com/influxdb/v1.4/introduction/installation/
