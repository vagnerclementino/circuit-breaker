package server

import (
	"time"

	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	"github.com/vagnerclementino/dojo-circuit-breaker/api-price/handler"
)

// Server is a struct which wraps a server instance
type Server struct {
	Stats
	Instance *echo.Echo
}

// New creates a server instance
func New() *Server {
	// Echo instance
	e := echo.New()

	// Middleware
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())

	return &Server{
		Instance: e,
	}
}

// Start starts the server execution
func (s *Server) Start() {

	// Routes
	s.Instance.GET("/prices/products/:id", handler.GetProductPrice, s.verifyServerStatus)
	s.Instance.GET("/status", s.getServerStatus)

	s.Status = UP
	s.LastUpTime = time.Now()

	s.Instance.Logger.Fatal(s.Instance.Start(":8080"))
}
