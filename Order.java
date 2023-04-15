package market;

public class Order{
	public int order_id;
	public OrderType order_type;
	public String ticker;
	public double quantity;
	public double cost;
	public double price;

	public Order(int order_id, OrderType type, String ticker, double quantity, double cost, double price){
		this.order_id = order_id;
		this.order_type = type;
		this.ticker = ticker;
		this.quantity = quantity;
		this.cost = cost;
		this.price = price;
	}

	public Order( Order o ){
		this.order_id = o.order_id;
		this.order_type = o.order_type;
		this.ticker = o.ticker;
		this.quantity = o.quantity;
		this.cost = o.cost;
		this.price = price;
	}
}

class OrderStatus extends Order{
	public boolean status;
	public OrderStatus(ExchangeOrder exo){
		super(exo.order);
		this.status = true;
	}
}

class ExchangeOrder{
	public Client client;
	public Order order;

	public ExchangeOrder(Order o, Client c){
		client = c;
		order = o;
	}
}

enum OrderType {
	buy, sell
}
