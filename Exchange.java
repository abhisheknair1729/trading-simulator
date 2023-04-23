package market;
import java.util.*;

public class Exchange{
	
	private Map<String, Double> prices;
	private Map<String, List<ExchangeOrder>> order_list;
	private static int exchange_order_count = 0;

	public Exchange(){
		prices = new HashMap<String, Double>();
		
		prices.put("ABC", 10.0);
		prices.put("XYZ", 10.0);

		order_list = new HashMap<String, List<ExchangeOrder>>();
		
		order_list.put("ABC", new ArrayList<ExchangeOrder>() );
		order_list.put("XYZ", new ArrayList<ExchangeOrder>() );
	}
	
	protected void finalize(){
		prices = null;
		order_list = null;
	}

	public synchronized double getCurrentPrice(String ticker){
		return prices.get(ticker);
	}

	public synchronized void addOrder(Order o, Client c){
		ExchangeOrder exo = new ExchangeOrder(o, c);
		String ticker = exo.order.ticker;
		int i = 0;
		for(i=0; i< order_list.get(ticker).size(); i++){
			ExchangeOrder tmp = order_list.get(ticker).get(i);
			if(tmp.order.order_type != exo.order.order_type){
				if(tmp.order.quantity == exo.order.quantity){
					OrderStatus os_tmp = new OrderStatus(tmp);
					OrderStatus os_exo = new OrderStatus(exo);
					
					if(tmp.order.order_type == OrderType.sell){
						if(tmp.order.price <= exo.order.price){
							os_exo.price = os_tmp.price;
							os_exo.cost  = os_tmp.cost;
							prices.put(ticker, os_tmp.price);
							exo.client.updateOrderStatus(os_exo);
							tmp.client.updateOrderStatus(os_tmp);
							exchange_order_count++;
                            break;
						}
					} else{
						if(exo.order.price <= tmp.order.price){
							os_tmp.price = os_exo.price;
							os_tmp.cost  = os_exo.cost;
							prices.put(ticker, os_exo.price);
							exo.client.updateOrderStatus(os_exo);
							tmp.client.updateOrderStatus(os_tmp);
                            exchange_order_count++;
							break;
						}
					}
				}
			}
		}

		if( i == order_list.get(ticker).size() ){
			order_list.get(ticker).add(exo);
		}else{
			order_list.get(ticker).remove(i);
		}
	}

	public synchronized void closeOfDay(){
		System.out.println("Completed " + Integer.toString(exchange_order_count) + " orders today");
        exchange_order_count = 0;
		
		for(String s: order_list.keySet())
		for(int i=0; i<order_list.get(s).size(); i++){
			ExchangeOrder exo = order_list.get(s).get(i);
			OrderStatus os = new OrderStatus(exo);
			os.status = false;
			exo.client.updateOrderStatus(os);
		}
		
		for(String s: order_list.keySet())
			order_list.put(s, new ArrayList());
	}

}
