## Reference:
- [https://stackoverflow.com/questions/66564818/provider-side-maching-not-work-on-request-body](https://stackoverflow.com/questions/66564818/provider-side-maching-not-work-on-request-body)
- [Pact matching features](https://docs.pact.io/getting_started/matching)
- [YouTube - Live Coding - Kafka Avro Producer and Consumer](https://www.youtube.com/watch?v=T3vfHQIpfdw)
- https://docs.confluent.io/platform/current/get-started/platform-quickstart.html
- 

````bash
curl -X POST "https://arpangroup.pactflow.io/api/v1/pacts/provider/{provider}/consumer/{consumer}/version/{version}/environment" \
  -H "Authorization: Bearer your-pact-broker-token" \
  -d '{"environment": "dev"}'
````

## Overview:


## Step1: Run DockerCompose file
````bash
docker-compose -f .\docker-compose-kafka.yml up -d
````
1. Navigate to Control Center at http://localhost:9021
2. Click the controlcenter.cluster tile.
<img src="images/img.png"/>

navigate to http://localhost:9021/clusters
<img src="images/broker_1.png"/>


## Step2: Create Kafka topics for storing your data
Navigate to Topics and create a new topic ("student-data")
<img src="images/topic_creation.jpg"/>