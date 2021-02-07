package main

import (
	"fmt"
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

var isOutOfService bool
var serviceChannel = make(chan bool)

func outOfService() {
	lastUpTime := time.Now()
	lastDownTime := time.Now()
	for {
		if !isOutOfService && time.Now().Sub(lastUpTime).Seconds() > 10 {
			isOutOfService = true
			lastDownTime = time.Now()

		}
		if isOutOfService && time.Now().Sub(lastDownTime).Seconds() > 30 {
			isOutOfService = false
			lastUpTime = time.Now()
		}
		fmt.Println(fmt.Sprintf("Current value of out of service: %t", isOutOfService))
		serviceChannel <- isOutOfService
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

	go outOfService()

	// Start server
	e.Logger.Fatal(e.Start(":1323"))

}

// Handler
func getProductPrice(c echo.Context) error {

	isServiceOut := <-serviceChannel

	if isServiceOut {
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
