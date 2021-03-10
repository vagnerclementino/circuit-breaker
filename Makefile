include MakefileDocumentation

IMAGE_TAG ?= $(shell git rev-parse --short HEAD)

API_PRICE_SERVICE_NAME = api-price
API_PRICE_IMAGE_ID = vclementino/$(API_PRICE_SERVICE_NAME):$(IMAGE_TAG)

API_PRODUCT_SERVICE_NAME = api-product
API_PRODUCT_IMAGE_ID = vclementino/$(API_PRODUCT_SERVICE_NAME):$(IMAGE_TAG)

build-api-price-image:
	@docker build -t $(API_PRICE_IMAGE_ID) -f api-price/Dockerfile api-price/

build-api-product-image:
	@docker build -t $(API_PRODUCT_IMAGE_ID) -f api-product/Dockerfile api-product/

run: ##@application Run all containers.
	@docker-compose up --build

stop: ##@application Stop all containers.
	@docker-compose down

test: test-api-price test-api-product ##@tests Run all tests.

container-api-price: ##@helpers Runs bash inside api-price's container
	@docker exec -it $(API_PRICE_SERVICE_NAME) /bin/sh

container-api-product: ##@helpers Run bash inside api-product's container
	@docker exec -it $(API_PRODUCT_SERVICE_NAME) /bin/sh

test-api-price: ##@tests Run api-price tests.
	@cd api-price && go test -v ./...
	
test-api-product: ##@tests Run api-product tests.
	@cd api-product && ./gradlew clean test

lint-api-product: ##@helpers Run a code analysis.
	@cd api-product && ./gradlew check

log-api-price: ##@helpers Do a docker tail in api-price's logs.
	@docker logs -f $(API_PRICE_SERVICE_NAME) 

log-api-product: ##@helpers Do a docker tail in api-product's logs.
	@docker logs -f $(API_PRODUCT_SERVICE_NAME) 

.PHONY : run stop tail test lint container
