#!/bin/bash

#compile maven project
mvn compile

# Start Redis server in the background
redis-server --daemonize yes

#Confirm if redis-server has started
redis-cli ping

#Run your Payments Service
mvn exec:java -Dexec.mainClass="org.example.PaymentsAPI"





