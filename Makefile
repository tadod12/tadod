TAG = "MAKE"

# All services
.PHONY: compose.stop-all
compose.stop-all:
	docker-compose -f docker-compose-cdc.yaml down
	docker-compose -f docker-compose-lh.yaml down
	docker-compose -f docker-compose-cron.yaml down

.PHONY: compose.start-all
compose.start-all:
	docker-compose -f docker-compose-cdc.yaml up -d
	docker-compose -f docker-compose-lh.yaml up -d
	docker-compose -f docker-compose-cron.yaml up -d

# CDC
.PHONY: mysql.shell
mysql.shell:
	mycli -u root -p admin

.PHONY: compose.cdc
compose.cdc:
	docker-compose -f docker-compose-cdc.yaml up -d

.PHONY: debezium.register
debezium.register:
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.tlc_yellow.json
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.tlc_green.json
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.tlc_fhv.json

.PHONY: debezium.register.yellow
debezium.register.yellow:
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.tlc_yellow.json

.PHONY: debezium.register.green
debezium.register.green:
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.tlc_green.json

.PHONY: debezium.register.fhv
debezium.register.fhv:
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.tlc_fhv.json


# Lakehouse
.PHONY: compose.lakehouse
compose.lakehouse:
	docker-compose -f docker-compose-lh.yaml up -d

.PHONY: compose.clean
compose.clean:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Cleaning container volumes ('docker/volume')"
	@ rm -rf docker/volume
	@ docker container prune -f
	@ docker volume prune -f
	@ echo ""
	@ echo ""
