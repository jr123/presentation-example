HTTP GATEWAY
============
To install and run:
* mvn install
* vertx install org.example~http-gateway~0.1-SNAPSHOT
* vertx runMod org.example~http-gateway~0.1-SNAPSHOT -cluster

The POST request URL is in the format: http://localhost:8080?address=<address>&sendOrPublish=<send|publish>