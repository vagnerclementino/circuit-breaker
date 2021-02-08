package main

import "github.com/vagnerclementino/dojo-circuit-breaker/api-price/server"

func main() {
	// Create a server
	server := server.New()

	// Start server
	server.Start()
}
