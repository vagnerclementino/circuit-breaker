include MakefileDocumentation

IMAGE_TAG ?= $(shell git rev-parse --short HEAD)

API_PRICE_SERVICE_NAME = api-price
API_PRICE_IMAGE_ID = vclementino/$(API_PRICE_SERVICE_NAME):$(IMAGE_TAG)

API_PRODUCT_SERVICE_NAME = api-product
API_PRODUCT_IMAGE_ID = vclementino/$(API_PRODUCT_IMAGE_TAG):$(IMAGE_TAG)

build-api-price-image:
	@docker build -t $(API_PRICE_IMAGE_ID) -f api-price/Dockerfile api-price/

build-api-product-image:
	@docker build -t $(API_PRODUCT_IMAGE_ID) -f api-product/Dockerfile api-product/

run: ##@application Run all containers.
	@docker-compose up --build

stop: ##@application Stop all containers.
	@docker-compose down

test: test-api-price test-api-product

container-api-price: ##@helpers Runs bash inside api-price's container
	@docker exec -it api-price /bin/sh

container-api-product: ##@helpers Run bash inside api-product's container
	@docker exec -it api-product /bin/sh

test-api-price: ##@tests Run all tests.
	@cd api-price && go test -v ./...
	
test-api-product: ##@tests Run all tests.
	@cd api-product && ./gradlew clean test

lint-api-price: ##@helpers Run a code analysis.

lint-api-product: ##@helpers Run a code analysis.

log-api-price: ##@helpers Do a docker tail in kong's logs.
	@docker logs -f api-price 

log-api-product: ##@helpers Do a docker tail in kong's logs.
	@docker logs -f api-product

.PHONY : run stop tail test lint container
