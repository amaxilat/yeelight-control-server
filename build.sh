#!/usr/bin/env bash
docker build -t yeelight-control-server:0.0.1 .
docker tag yeelight-control-server:0.0.1 qopbot/yeelight-control-server:0.0.1
#docker login
docker push yeelight-control-server/jre8-base:0.0.1
