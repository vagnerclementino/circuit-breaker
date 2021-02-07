package main

import (
	"net/http"
	"strconv"
	"time"

	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
)

// Status is the server status
type Status int

// The server status values
const (
	DOWN Status = iota // 0
	UP                 // 1
)

func (s Status) String() string {
	names := [2]string{
		"DOWN",
		"UP",
	}
	if s < DOWN || s > UP {
		return "unknown"
	}
	return names[s]
}

// Price represents the product's price
type Price struct {
	ProductID int     `json:"product_id"`
	Price     float64 `json:"price"`
}

// ServerStatus show the current status from server
type ServerStatus struct {
	Status       `json:"status"`
	LastUpTime   time.Time `json:"last_up_time"`
	LastDownTime time.Time `json:"last_down_time"`
}

var serverStatusChannel = make(chan ServerStatus)

func manageServerStatus(c chan ServerStatus) {

	serverStatus := ServerStatus{
		Status:     UP,
		LastUpTime: time.Now(),
	}

	for {
		if serverStatus.Status == UP && time.Since(serverStatus.LastUpTime).Seconds() > 10 {
			serverStatus.Status = DOWN
			serverStatus.LastDownTime = time.Now()
		}
		if serverStatus.Status == DOWN && time.Since(serverStatus.LastDownTime).Seconds() > 30 {
			serverStatus.Status = UP
			serverStatus.LastUpTime = time.Now()
		}
		c <- serverStatus
	}
}

// VerifyServerStatus middleware verifies the server status
func VerifyServerStatus(next echo.HandlerFunc) echo.HandlerFunc {
	return func(c echo.Context) error {
		serverStatus := <-serverStatusChannel

		if serverStatus.Status == DOWN {
			return echo.NewHTTPError(http.StatusInternalServerError)
		}
		return next(c)
	}
}

func main() {
	// Echo instance
	e := echo.New()

	// Middleware
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())

	// Routes
	e.GET("/prices/products/:id", getProductPrice, VerifyServerStatus)
	e.GET("/status", getServerStatus)

	go manageServerStatus(serverStatusChannel)

	// Start server
	e.Logger.Fatal(e.Start(":1323"))

}

// Handler
func getProductPrice(c echo.Context) error {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		return echo.NewHTTPError(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, Price{
		ProductID: id,
		Price:     100.00,
	})
}

// Handler
func getServerStatus(c echo.Context) error {
	serverStatus := <-serverStatusChannel
	return c.JSON(http.StatusOK,
		struct {
			Status       string    `json:"status"`
			LastUpTime   time.Time `json:"last_up_time"`
			LastDownTime time.Time `json:"last_down_time"`
		}{
			Status:       serverStatus.Status.String(),
			LastUpTime:   serverStatus.LastUpTime,
			LastDownTime: serverStatus.LastDownTime,
		},
	)
}
