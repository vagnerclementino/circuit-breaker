package server

import (
	"net/http"
	"time"

	"github.com/labstack/echo/v4"
)

// verifyServerStatus middleware verifies the server status
func (s *Server) verifyServerStatus(next echo.HandlerFunc) echo.HandlerFunc {
	return func(c echo.Context) error {

		if s.Stats.Status == UP {
			if time.Since(s.Stats.LastUpTime).Seconds() > 10 {
				s.Stats.Status = DOWN
				s.Stats.LastDownTime = time.Now()
				return echo.NewHTTPError(http.StatusInternalServerError)
			}
			return next(c)
		}

		if s.Stats.Status == DOWN {
			if time.Since(s.Stats.LastDownTime).Seconds() > 30 {
				s.Stats.Status = UP
				s.Stats.LastUpTime = time.Now()
				return next(c)
			}
			return echo.NewHTTPError(http.StatusInternalServerError)
		}
		return next(c)
	}
}
