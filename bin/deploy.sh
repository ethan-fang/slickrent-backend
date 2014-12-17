#!/usr/bin/env bash
mvn package

scp ./shareitemservice/target/shareitemservice-1.0-SNAPSHOT.jar ec2_share_with_xin041619:shareservice/shareservice.jar
scp ./shareitemservice/shareitemservice.yml ec2_share_with_xin041619:shareservice/shareitemservice_dev.yml

ssh -t ec2_share_with_xin041619 sudo restart shareservice
