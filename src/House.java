import java.util.Random;

public class House {
	public int value = 0;
	public int downPayment = 0;
	public int monthlyPayment = 0;
	public int rent = 0;
	public int monthsRemaining = 0;
	public boolean paidOff = false;
	
	
	House(){
		
		value = generateHouse(MainClass.minHouseValue,MainClass.maxHouseValue);
		downPayment = houseDownpayment(value,MainClass.downPaymentPercent);
		monthlyPayment = calculateMonthlyPayment(value-downPayment,MainClass.yearsMortgage,MainClass.interestRate);
		rent = generateRent(value);
		monthsRemaining = MainClass.yearsMortgage * 12;
		
		paidOff = false;
		
	}
	
	
	public static int generateHouse(int min, int max) {
		min /= 1000;
		max /= 1000;
		int HouseValue = randInt(min,max)*1000;		
		return HouseValue;

	}
	
	public static int generateRent(int houseValue) {
		
		houseValue /= 100;
		int rent = randInt(houseValue-100,houseValue+100);
		rent -= rent%10; 
		return rent;
					
	}
	
	public void appreciateRent() {
		int newRent = randInt(rent,rent+rent*MainClass.maxRentAppreciationPercent/100);
		newRent -= newRent%10; 
		rent = newRent;

	}
	
	public void appreciateHouseValue() {
		
		int newValue = randInt(value,value+value*MainClass.maxHouseValueAppreciationPercent/100);
		newValue -= newValue%1000; 
		value = newValue;
					
	}
	
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public static int houseDownpayment(int HouseValue, double downPaymentPercent){
		
		float downpayment = HouseValue/100;
		downpayment = (float) Math.ceil(downpayment*downPaymentPercent);
		
		
		return (int) downpayment*100;
				
	}

	public static int calculateMonthlyPayment(
	         int loanAmount, int termInYears, double interestRate) {
	          
	         // Convert interest rate into a decimal
	         // eg. 6.5% = 0.065
	          
	         interestRate /= 100.0;
	          
	         // Monthly interest rate 
	         // is the yearly rate divided by 12
	          
	         double monthlyRate = interestRate / 12.0;
	          
	         // The length of the term in months 
	         // is the number of years times 12
	          
	         int termInMonths = termInYears * 12;
	          
	         // Calculate the monthly payment
	         // Typically this formula is provided so 
	         // we won't go into the details
	          
	         // The Math.pow() method is used 
	         // to calculate values raised to a power
	          
	         double monthlyPayment = 
	            (loanAmount*monthlyRate) / 
	               (1-Math.pow(1+monthlyRate, -termInMonths));
	          
	         return (int) monthlyPayment;
	      }

	
}
