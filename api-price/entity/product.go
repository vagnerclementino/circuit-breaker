package entity

// Product represents the product's price
type Product struct {
	ProductID int     `json:"product_id"`
	Price     float64 `json:"price"`
}
