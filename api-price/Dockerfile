#
# builder image
#
FROM golang:1.13-alpine3.11 as builder
RUN mkdir -p /build
ADD . /build/

WORKDIR /build
RUN CGO_ENABLED=0 GOOS=linux go build -a -o api-price .
# do not need extldflags set to static, because no external linker (CGO disabled)
#RUN CGO_ENABLED=0 GOOS=linux go build -a -ldflags '-extldflags "-static"' -o golang-memtest .


#
# generate clean, final image for end users
#
FROM alpine:3.11.3

RUN mkdir -p /app

WORKDIR /app

# copy golang binary into container
COPY --from=builder /build/api-price .

RUN ls /app/

# executable
ENTRYPOINT [ "/app/api-price" ]
# arguments that can be overridden
# 3Mb, 300 milliseconds between allocation
CMD [ "3", "300" ]
