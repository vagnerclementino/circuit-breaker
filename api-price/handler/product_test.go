package handler

import (
	"errors"
	"fmt"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/labstack/echo/v4"
	"github.com/stretchr/testify/assert"
)

// TestGetProductPrice is a testing
func TestGetProductPrice(t *testing.T) {

	e := echo.New()
	req := httptest.NewRequest(http.MethodGet, "/", nil)
	rec := httptest.NewRecorder()
	c := e.NewContext(req, rec)
	c.SetPath("/users/:id")
	c.SetParamNames("id")

	var tests = []struct {
		title        string
		productID    int
		expectStatus int
		expectReturn string
		testFunc     func(productID, expectStatus int, expectReturn string) func(t *testing.T)
	}{
		{
			title:        "Should returns product's price",
			productID:    1,
			expectStatus: http.StatusOK,
			expectReturn: `
			{
				"product_id": 1,
				"price": 122.99
			}`,
			testFunc: func(productID, expectStatus int, expectReturn string) func(t *testing.T) {
				return func(t *testing.T) {
					c.SetParamValues(fmt.Sprintf("%d", productID))
					err := GetProductPrice(c)

					assert.NoError(t, err)

					assert.Equal(t, expectStatus, rec.Code)
					assert.JSONEq(t, expectReturn, rec.Body.String())
				}
			},
		},
		{
			title:        "Should returns not found when product not exists",
			productID:    -1,
			expectStatus: http.StatusNotFound,
			expectReturn: "Cannot find price to product with id: -1",
			testFunc: func(productID, expectStatus int, expectReturn string) func(t *testing.T) {
				return func(t *testing.T) {
					c.SetParamValues(fmt.Sprintf("%d", productID))
					err := GetProductPrice(c)

					assert.Error(t, err)

					var e *echo.HTTPError

					if errors.As(err, &e) {
						assert.Equal(t, expectStatus, e.Code)
						assert.Equal(t, expectReturn, e.Message)

					}
				}
			},
		},
	}

	for _, tc := range tests {
		t.Run(tc.title, tc.testFunc(tc.productID, tc.expectStatus, tc.expectReturn))
	}
}
