package main

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/labstack/echo/v4"
	"github.com/stretchr/testify/assert"
)

// TestGetProductPrice is a testing
func TestGetProductPrice(t *testing.T) {

	// Given
	e := echo.New()
	req := httptest.NewRequest(http.MethodGet, "/", nil)
	rec := httptest.NewRecorder()
	c := e.NewContext(req, rec)

	// When
	c.SetPath("/users/:id")
	c.SetParamNames("id")
	c.SetParamValues("1")

	// Then

	if assert.NoError(t, getProductPrice(c)) {
		expectedJSON := ""
		b, err := json.Marshal(Price{
			ProductID: 1,
			Price:     100.00,
		})
		if assert.NoError(t, err) {
			expectedJSON = string(b)
		}
		assert.Equal(t, http.StatusOK, rec.Code)
		assert.JSONEq(t, expectedJSON, rec.Body.String())
	}
}
