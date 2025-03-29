#!/bin/sh
echo "Waiting for Kafka..."
sleep 30
echo "Python Producer"
python3 producer.py
