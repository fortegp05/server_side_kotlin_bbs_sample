#!/bin/sh
docker-compose exec db bash -c "chmod 0775 docker-initdb/initDb.sh"
docker-compose exec db bash -c "./docker-initdb/initDb.sh"