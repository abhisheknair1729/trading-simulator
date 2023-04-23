package market;
import java.util.*;
import java.lang.Math.*;

public class Client implements Runnable {
	
	private Map<String, Double> quantity; 
	private int id;
	private static int next_id = 0;
	
	private double money;

	private int order_id;
	private Map<Integer, Order> pending_orders;
	private Map<Integer, Order> completed_orders;
	
	private Exchange ex;
    
    public Thread t;

	public Client(Map quantity, double money){	
		this.id = next_id++;
		this.quantity = new HashMap<String, Double>();
		this.quantity.putAll(quantity);
		
		this.money = money;

		this.order_id = 0;

		pending_orders = new HashMap<Integer, Order> ();
		completed_orders = new HashMap<Integer, Order> ();
	}

	public Client(){
		this.id = next_id++;
		this.quantity = new HashMap<String, Double>();	
		
		this.money = 0;

		this.order_id = 0;
		
		pending_orders = new HashMap<Integer, Order> ();
		completed_orders = new HashMap<Integer, Order> ();
	}
		
	protected void finalize(){
		this.quantity = null;
		this.ex = null;
		this.pending_orders = null;
		this.completed_orders = null;
	}
	
	public synchronized void setExchange( Exchange ex ){
		this.ex = ex;
	}

	public synchronized Exchange getExchange(){
		return this.ex;
	}

	public synchronized void addFunds( double funds ){
		this.money += funds;
	}
	
	public synchronized double viewFunds(){
		return money;
	}
	
	public synchronized void printInfo(){
		System.out.println("Client " + id + " has the following holdings");
		System.out.println("Cash: " + money);
		List<String> equities = new ArrayList<String>(quantity.keySet());
		for(String s: equities){
			System.out.println(s+": "+quantity.get(s));
		}
	}

	public synchronized Order createOrder( OrderType type, String ticker, double quantity, double price ){
		
		double cost = price*quantity;
		Order o = new Order( order_id++, type, ticker, quantity, cost, price );
		
        try{
		if( type == OrderType.sell ){
			if( this.quantity.get(ticker) < quantity ){
				System.out.println("Cannot issue sell order because of"
						+ " insufficient holdings\n");
				order_id--;
				return null;
			}else{	
				double temp = this.quantity.get(ticker);
				this.quantity.put(ticker, temp - quantity);		
			}
		}

		if( type == OrderType.buy ){
			if( this.money < cost ){
				System.out.println("Cannot issue buy order because of"
						+ " insufficient funds\n");
				order_id--;
				return null;
			}else{
				this.money -= cost;
			}
		}
        }
        catch (Exception e){
            return null;
        }
		
		pending_orders.put(o.order_id, o);	
		return o;
	}
	
	// return status value
	public synchronized boolean updateOrderStatus( OrderStatus os ){
		Order o = pending_orders.get(os.order_id);
		if(os.status == true){
			completed_orders.put( os.order_id, pending_orders.get(os.order_id) );
			pending_orders.put(os.order_id, null);
			
			if(os.order_type == OrderType.sell){
				this.money += os.cost;
			}else{
				this.money -= (os.cost - o.cost);
				double temp;
				try{
					temp = this.quantity.get(o.ticker);
				} catch(NullPointerException n){
					temp = 0;
				}
				this.quantity.put(o.ticker, temp + o.quantity);
			}	
		} else{
			if(os.order_type == OrderType.sell){
				double temp = this.quantity.get(o.ticker);
				this.quantity.put(o.ticker, temp + o.quantity);
			}else{
				this.money += o.cost;
			}
			pending_orders.put(os.order_id, null);
		}
		
		return os.status;
	}
    
    public void generate_random_orders(){
        
        int i;
        
        for(i=0; i<100; i++){
            
            OrderType type = Math.random()<0.5?OrderType.buy:OrderType.sell;            
            
            double price = Math.random()<0.5?10:11;
            double quantity = Math.random()<0.5?5:15;

            String ticker = Math.random()<0.5?"ABC":"XYZ";
            
            Order o = createOrder(type, ticker, quantity, price);
            if( o!= null )
                ex.addOrder(o, this);
        }

    }

    public void run() {
        generate_random_orders();
    }

    public void start() {
        if( t == null ){
            t = new Thread(this, "Client " + Integer.toString(id));
            t.start();
        }
    }

}
			

	








