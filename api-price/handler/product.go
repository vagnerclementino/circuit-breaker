package handler

import (
	"net/http"
	"strconv"

	"github.com/labstack/echo/v4"
	"github.com/vagnerclementino/dojo-circuit-breaker/api-price/entity"
)

// GetProductPrice handles route /prices/products/:id
func GetProductPrice(c echo.Context) error {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		return echo.NewHTTPError(http.StatusInternalServerError, err.Error())
	}

	return c.JSON(http.StatusOK, entity.Product{
		ProductID: id,
		Price:     100.00,
	})
}
