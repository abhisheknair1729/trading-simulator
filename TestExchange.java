package market;
import java.util.*;

public class TestExchange {
	
	public static void main(String[] args){
		Client C1 = new Client();
		C1.addFunds(1000);

		Map<String, Double> m = new HashMap<String, Double>();
		m.put("ABC", 100.0);
		m.put("XYZ", 300.0);

		Client C2 = new Client(m, 300);
		
		Exchange exc = new Exchange();
		C1.setExchange(exc);
		C2.setExchange(exc);
		System.out.println("Start of Day:");
		C1.printInfo();
		C2.printInfo();
		
        C1.start();
        C2.start();
        /*
		double priceABC = exc.getCurrentPrice("ABC");
		exc.addOrder(C1.createOrder(OrderType.buy, "ABC", 5.0, priceABC), C1);
		exc.addOrder(C2.createOrder(OrderType.sell, "ABC", 5.0, 12.0), C2);
		exc.addOrder(C1.createOrder(OrderType.buy, "ABC", 5.0, 12.0), C1);
		*/
        try{
            C1.t.join();
            C2.t.join();
        } catch(Exception e){
            System.out.println("Threading Exception");
        }
		System.out.println("End of Day:");
		exc.closeOfDay();
		C1.printInfo();
		C2.printInfo();

	}
}

