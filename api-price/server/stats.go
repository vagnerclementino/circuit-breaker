package server

import (
	"net/http"
	"time"

	"github.com/labstack/echo/v4"
)

type (
	// Status is the server status
	Status int

	// Stats is serve stats data
	Stats struct {
		Status
		LastUpTime   time.Time
		LastDownTime time.Time
	}
)

// The server status values
const (
	DOWN Status = iota // 0
	UP                 // 1
)

var serverStatsChannel = make(chan Stats)

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

func manageServerStatus(s *Server, c chan Stats) {
	for {
		select {
		case stats := <-c:
			if stats.Status == UP && time.Since(stats.LastUpTime).Seconds() > 10 {
				stats.Status = DOWN
				stats.LastDownTime = time.Now()
			}
			if stats.Status == DOWN && time.Since(stats.LastDownTime).Seconds() > 30 {
				stats.Status = UP
				stats.LastUpTime = time.Now()
			}
			c <- stats
		}
	}
}

// getServerStatus handles /status route
func (s *Server) getServerStatus(c echo.Context) error {

	return c.JSON(http.StatusOK,
		struct {
			Status       string    `json:"status"`
			LastUpTime   time.Time `json:"last_up_time"`
			LastDownTime time.Time `json:"last_down_time"`
		}{
			Status:       s.Status.String(),
			LastUpTime:   s.LastUpTime,
			LastDownTime: s.LastDownTime,
		},
	)
}
