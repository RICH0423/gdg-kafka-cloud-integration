# gdg-kafka-cloud-integration

### [slide](https://www2.slideshare.net/RICHLEE11/gdg-taipei-2020-cloud-and-onpremises-applications-integration-using-eventdriven)

### Pre-requisites
- [Install the Cloud SDK](https://cloud.google.com/sdk/docs/quickstart)
- [Cloud PubSub Connector](https://github.com/GoogleCloudPlatform/pubsub/tree/master/kafka-connector)
- Add an environment variable named **GOOGLE_APPLICATION_CREDENTIALS**
```bash
// in connect-distributed.sh
export GOOGLE_APPLICATION_CREDENTIALS=/Users/rich/tools/kafka_2.13-2.4.0/app-project-294009-9253ce1d027b.json
```

- Start zookeeper & Kafka broker
```bash
./bin/zookeeper-server-start.sh config/zookeeper.properties
./bin/kafka-server-start.sh config/server.properties
```

- Start Kafka Connect Cluster (distributed mode)

```bash
./bin/connect-distributed.sh config/connect-distributed.properties

```
---

### Cloud PubSub Connector

- Create pusub topics
```bash
gcloud pubsub topics create cps-iot-input
gcloud pubsub topics create cps-iot-output
gcloud pubsub subscriptions create --topic cps-iot-input input-subscription-1
gcloud pubsub subscriptions create --topic cps-iot-output output-subscription-1
```

- create Cloud PubSub sink connector

- create cps-sink-connector1
  - Method: **POST**
  - URL: ```/connectors```
  - Headers： Content-Type:application/json
  - Body:
```json
{
    "name": "cps-sink-connector1",
    "config": {
          "connector.class": "com.google.pubsub.kafka.sink.CloudPubSubSinkConnector",
          "tasks.max": "1",
          "topics": "iot-topic",
          "cps.topic": "cps-iot-input",
          "cps.project": "app-project-294009",
          "value.converter": "org.apache.kafka.connect.storage.StringConverter",
          "key.converter.schemas.enable": false,
          "value.converter.schemas.enable": false
    }
}
```

- produce record to **iot-topic**

```bash
./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic iot-topic
>{ "id":"1", "temperature": "100F" }
```

- create Cloud PubSub source connector

- create cps-source-connector1
  - Method: **POST**
  - URL: ```/connectors```
  - Headers： Content-Type:application/json
  - Body:
```json
{
    "name": "cps-source-connector1",
    "config": {
          "connector.class": "com.google.pubsub.kafka.source.CloudPubSubSourceConnector",
          "tasks.max": "1",
          "kafka.topic": "iot-processed-topic",
          "cps.subscription": "subscription2",
          "cps.project": "app-project-294009",
          "value.converter": "org.apache.kafka.connect.converters.ByteArrayConverter",
          "key.converter.schemas.enable": false,
          "value.converter.schemas.enable": false
    }
}
```

- to consume records from iot-processed-topic

```bash
./bin/kafka-console-consumer.sh --bootstrap-server=localhost:9092  --from-beginning --topic iot-processed-topic
```


### Reference
- [Make Kafka serverless by connecting it to Google Cloud Functions](https://dev.to/vtatai/make-kafka-serverless-by-connecting-it-to-google-cloud-functions-2ahh)
- [Source and Sink for Kafka and PubSub](https://medium.com/musings-in-the-clouds/source-and-sink-for-kafka-and-pubsub-2a3565ce9578)
- [Streaming Kafka Messages to Google Cloud Pub/Sub](https://medium.com/@alexandredallalba/streaming-kafka-messages-to-google-cloud-pub-sub-f3ce7ef425b0)
