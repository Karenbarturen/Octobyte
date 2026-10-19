#!/bin/bash
java --enable-native-access=ALL-UNNAMED -Desp32.serial.default-port=ttyUSB1 -jar target/octobyte-backend-1.0.0.jar
