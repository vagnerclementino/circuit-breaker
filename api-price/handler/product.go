package handler

import (
	"fmt"
	"net/http"
	"strconv"

	"github.com/labstack/echo/v4"
	"github.com/vagnerclementino/dojo-circuit-breaker/api-price/entity"
)

var prices = map[int]*entity.Product{
	1: {
		ProductID: 1,
		Price:     100.00,
	},
	2: {
		ProductID: 2,
		Price:     200.00,
	},
	3: {
		ProductID: 3,
		Price:     300.00,
	},
}

// GetProductPrice handles route /prices/products/:id
func GetProductPrice(c echo.Context) error {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		return echo.NewHTTPError(http.StatusInternalServerError, err.Error())
	}

	price, ok := prices[id]

	if !ok {
		fmt.Println("not found")
		return echo.NewHTTPError(http.StatusNotFound, fmt.Sprintf("Cannot find price to product with id: %d", id))
	}
	return c.JSON(http.StatusOK, price)
}
