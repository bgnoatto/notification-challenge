#!/bin/bash
docker compose -f compose.test.yaml up -d --build --force-recreate --wait

mvn test

docker compose -f compose.test.yaml down
