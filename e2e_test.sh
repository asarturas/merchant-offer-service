#!/usr/bin/env bash
# start server in background and confirm that it's heartbeat route works as expected,
# which we assume is enough to indicate that app is working as we expect
trap "kill 0" EXIT
(sbt "api/runMain com.spikerlabs.offers.App")&
sleep 30
curl http://localhost:8080/heartbeat | grep ok