package main

import (
	"net/http"
	"strconv"
	"time"

	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
)

// Price represents the product's price
type Price struct {
	ProductID int     `json:"product_id"`
	Price     float64 `json:"price"`
}

// ServerStatus show the current status from server
type ServerStatus struct {
	Status       string    `json:"status"`
	LastUpTime   time.Time `json:"last_up_time"`
	LastDownTime time.Time `json:"last_down_time"`
}

var serverStatusChannel = make(chan ServerStatus)

func manageServerStatus(c chan ServerStatus) {

	serverStatus := ServerStatus{
		Status:     "UP",
		LastUpTime: time.Now(),
	}

	for {
		if serverStatus.Status == "UP" && time.Now().Sub(serverStatus.LastUpTime).Seconds() > 10 {
			serverStatus.Status = "DOWN"
			serverStatus.LastDownTime = time.Now()
		}
		if serverStatus.Status == "DOWN" && time.Now().Sub(serverStatus.LastDownTime).Seconds() > 30 {
			serverStatus.Status = "UP"
			serverStatus.LastUpTime = time.Now()
		}
		// fmt.Println(fmt.Sprintf("Current status of server: %s", serverStatus.Status))
		c <- serverStatus
	}
}

func main() {
	// Echo instance
	e := echo.New()

	// Middleware
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())

	// Routes
	e.GET("/prices/products/:id", getProductPrice)
	e.GET("/status", getServerStatus)

	go manageServerStatus(serverStatusChannel)

	// Start server
	e.Logger.Fatal(e.Start(":1323"))

}

// Handler
func getProductPrice(c echo.Context) error {

	serverStatus := <-serverStatusChannel

	if serverStatus.Status == "DOWN" {
		return echo.NewHTTPError(http.StatusInternalServerError)
	}

	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		return echo.NewHTTPError(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, Price{
		ProductID: id,
		Price:     100.00,
	})
}

func getServerStatus(c echo.Context) error {
	serverStatus := <-serverStatusChannel
	return c.JSON(http.StatusOK, serverStatus)
}
