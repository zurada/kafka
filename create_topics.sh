#!/bin/bash

docker-compose exec kafka1 kafka-topics   --create   --bootstrap-server localhost:9092   --replication-factor 3   --partitions 3   --topic demoInput
docker-compose exec kafka1 kafka-topics   --create   --bootstrap-server localhost:9092   --replication-factor 3   --partitions 3   --topic demoOutput
docker-compose exec kafka1 kafka-topics   --create   --bootstrap-server localhost:9092   --replication-factor 3   --partitions 3   --topic demoDlq
