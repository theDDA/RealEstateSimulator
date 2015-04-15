import java.util.Random;

public class MainClass {
	
	static int startBankBalance = 41000;
	static int yearsMortgage = 8;
	static int interestRate = 4;
	
	static boolean allowGoingIntoDebt = false; //Can bank account go below zero?
	
	static int costOfBuyingAProperty = 10000;
	
	static double minimumRatioOfRentToHouseValue = 0.1; //When should you stop serial repayment and just
														//start waiting to buy the next house in cash?
	static int minHouseValue = 100000;
	static int maxHouseValue = 150000;
	
	static boolean serialRepayment = true;
	static boolean rentAppreciation = true;
	static int maxRentAppreciationPercent = 3;
	
	static boolean houseValueAppreciation = true;
	static int maxHouseValueAppreciationPercent = 3;
	
	static int goalHouses = 10;
	static int maxNumMortgages = 2;
	
	static double downPaymentPercent = 0.2;
	

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		
		
		
		printInitialConditions ();
		runIterations(1000, false);
		

		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("This took " + duration/1000000 + "ms.");
	}
	
	public static void printInitialConditions (){
		System.out.println("You start with " + startBankBalance + "$.");
		System.out.println("You want to own " + goalHouses + " houses between " +
							minHouseValue + "$ and " + maxHouseValue + "$.");
		System.out.println("You'll take " + yearsMortgage + " year mortgages with " + 
							interestRate + "% interest and " + downPaymentPercent*100 +
							"% down payment.");
		System.out.println("Your bank limits you to " + maxNumMortgages + " mortgages at the same time.");
		if (serialRepayment) System.out.println("You'll try to pay off your mortgages with your cash flow until your rent is " +
												minimumRatioOfRentToHouseValue*100 + "% of your next house price.");
		if (rentAppreciation) System.out.println("Your rent will appreciate yearly by maximum of " + maxRentAppreciationPercent + "%.");
		if (houseValueAppreciation) System.out.println("Your house value will appreciate yearly by maximum of " + maxHouseValueAppreciationPercent + "%.");

	}
	
	public static void calculateBestMinimumRatioOfRentToHouseValue (int iterations){
		float bestRatio = 0;
		float minMonths = maxNumMortgages*yearsMortgage*12;
		
		for(int j=1; j<100; j++){
			minimumRatioOfRentToHouseValue = j/100.0;
			float avgMonths = 0;
			

			for(int i=0; i<iterations;i++){
				avgMonths += (float) simulate(goalHouses,maxNumMortgages,false);
			}
			if (avgMonths/iterations<minMonths){
				minMonths=avgMonths/iterations;
				bestRatio = (float) minimumRatioOfRentToHouseValue;
			}
		}
		System.out.println("Best ratio: " + bestRatio + " (" + minMonths + " months).");
		
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
	
	public static int appreciateRent(int currentRent) {
		
		int rent = randInt(currentRent,currentRent+currentRent*maxRentAppreciationPercent/100);
		rent -= rent%10; 
		return rent;
					
	}
	
	public static int appreciateHouseValue(int currentValue) {
		
		int value = randInt(currentValue,currentValue+currentValue*maxHouseValueAppreciationPercent/100);
		value -= value%1000; 
		return value;
					
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
	
	public static boolean canYouBuyNewHouse(int downPayment,long bankAcc){

		if(bankAcc>downPayment) return true;
		else return false;
	}

	public static void runIterations(int n, boolean verbose){
		float avgMonths = 0;
		int bankrupcyCounter = 0;
		
		for(int i=0; i<n;i++){
			
			float temp = (float) simulate(goalHouses,maxNumMortgages,verbose);
			
			if(temp != -1){
				avgMonths += temp;
			}
			else {
				bankrupcyCounter++;
			}
						
		}
		
		if(bankrupcyCounter != 0)
			System.out.println("You went bankrupt " + (float)(bankrupcyCounter*100.0/n) +
								"% of the time.");
			
		
		System.out.println("Average time in months: " + avgMonths/(n-bankrupcyCounter));
	}

	public static int simulate(int goalHouses, int maxNumberOfMortgages, boolean verbose){
		
		House[] houses = new House[goalHouses];
		
		for(int i = 0; i<goalHouses; i++){
			houses[i] = new House();
		}
		
		long bankAcc = startBankBalance;
		
		int monthCounter = 0;
		int numberOfMortgages = 0;
		int housesPaidOff = 0;
		
		int lastHouseBought = -1; //so that when it increments, it starts at 0
		
		while (housesPaidOff < goalHouses){ //Until last house is owned in full.
			
			if (!allowGoingIntoDebt && bankAcc < 0){
				if(verbose) 
					System.out.println("Bankrupt at month #" + (monthCounter-1) + ".");
				return -1;
			}
			
			int rentThisMonth = 0;
			for (int i = 0; i <= lastHouseBought; i++){
				rentThisMonth += houses [i].rent;  //TODO: Taxes and expenses
			}
			bankAcc += rentThisMonth;
			
			//Pay mortgage:
			for (int i = 0; i <= lastHouseBought; i++){
				
				if (houses [i].paidOff == false){ //Don't do it if it's paid off.
					if (houses [i].monthsRemaining == 1){
						//Last payment, let's make this house done with.
						houses [i].monthsRemaining = 0;
						bankAcc -= houses [i].monthlyPayment;
						houses [i].monthlyPayment = 0;
						
						houses [i].paidOff = true;
						housesPaidOff++;
						
						numberOfMortgages--;
						
						//////////////////////
						if(verbose) 
							System.out.println("It's month #" + monthCounter +
							" and I paid off in full house #" +
							(i+1) +".");
						//////////////////////

					}
					
					bankAcc -= houses [i].monthlyPayment;
					houses [i].monthsRemaining -= 1;
				}				
			}
			
			//If number of mortgages is limited, focus on serial full payment
			int mortgagePaidThisMonth = 0;
			
			
			
			if(serialRepayment && 
					(lastHouseBought == houses.length-1 || rentThisMonth < houses[lastHouseBought+1].value * minimumRatioOfRentToHouseValue)){
				for (int i = 0; i <= lastHouseBought; i++){
					if (houses[i].paidOff == false){ //if it's not paid already
											//Have to cycle through all of them
											//because some get paid in cash while others
											//still have mortgage on them.
											//Made this if separate so cycles are not wasted
											//on needlessly checking other conditions.
						
						if(		maxNumberOfMortgages != 0
								&& (numberOfMortgages==maxNumberOfMortgages || lastHouseBought > (goalHouses - maxNumberOfMortgages))
								&& bankAcc > houses[i].monthlyPayment){
							//Prioritizes buying new houses if possible over 
							//just repaying.
							
							while(bankAcc > houses[i].monthlyPayment
									&& i < goalHouses
									&& houses [i].paidOff == false
									){
								
								if (houses [i].monthsRemaining == 1){
									//Again wrap things up if it's the last payment.
									houses [i].monthsRemaining = 0;
									bankAcc -= houses [i].monthlyPayment;
									mortgagePaidThisMonth += houses [i].monthlyPayment;
									houses [i].monthlyPayment = 0;
									houses [i].paidOff = true;
									housesPaidOff++;
									numberOfMortgages--;
									
									//////////////////////
									if(verbose) 
										System.out.println("It's month #" + monthCounter +
										" and I paid off in full house #" +
										(i+1) +".");
									//////////////////////

								}
								
								else{
									bankAcc -= houses [i].monthlyPayment;
									houses [i].monthsRemaining -= 1;
									mortgagePaidThisMonth += houses [i].monthlyPayment;
								}								
							}
						}
					}
				}
			}
			
			//////////////////////
			if(verbose) {
				int mortgageThisMonth = 0;
				for (int i=0;i<=lastHouseBought;i++){
					mortgageThisMonth+=houses[i].monthlyPayment;
				}
				System.out.println("Month " + monthCounter + ": " +
						"rent: " + rentThisMonth +"$, " + 
						"mortgage: " + mortgageThisMonth +"$, " +
						"extra mortgage paid: " + mortgagePaidThisMonth + "$, " +
						"bank balance: " + bankAcc + "$.");
			}
			//////////////////////
			
			
			//Buy more houses:
			while(	lastHouseBought<houses.length-1
					&& canYouBuyNewHouse(houses[lastHouseBought+1].downPayment+costOfBuyingAProperty,bankAcc)
					&& (numberOfMortgages<maxNumberOfMortgages || maxNumberOfMortgages == 0 || bankAcc>(houses[lastHouseBought+1].value+costOfBuyingAProperty))
					){
				
				if(bankAcc>(houses[lastHouseBought+1].value + costOfBuyingAProperty)
					&& (lastHouseBought < houses.length-1)){
					//Can I buy cash?
					
					lastHouseBought++;
					
					
					bankAcc -= (houses[lastHouseBought].value+costOfBuyingAProperty);
					houses[lastHouseBought].monthlyPayment = 0;
					houses[lastHouseBought].monthsRemaining = 0;
					houses[lastHouseBought].paidOff = true;
					housesPaidOff++;
					//////////////////////
					if(verbose)
						System.out.println("It's month #" + monthCounter +
						" and I bought house #" + (lastHouseBought+1) +
						" for " + houses[lastHouseBought].value + "$ with cash.");
					//////////////////////
				}
				else{
					
					lastHouseBought++;
					numberOfMortgages++;
					bankAcc -= (houses[lastHouseBought].downPayment+costOfBuyingAProperty);
					//////////////////////
					if(verbose)
						System.out.println("It's month #" + monthCounter +
								" and I bought house #" + (lastHouseBought+1) +
								" for " + houses[lastHouseBought].value + "$ and with down payment of " + 
								houses[lastHouseBought].downPayment + " with mortgage.");
					//////////////////////
				}
			}	
			
			if (rentAppreciation && monthCounter % 12 == 0){
				for(int i=0; i<houses.length;i++){
					houses[i].appreciateRent();
				}
			}
			
			if (houseValueAppreciation && monthCounter % 12 == 0){
				for(int i=0; i<houses.length;i++){
					houses[i].appreciateHouseValue();
				}
			}
			
			
			monthCounter++;
			}
		
		if (verbose){
			long netWorth = bankAcc;
			for (int i = 0; i<goalHouses;i++){
				netWorth += houses[i].value;
			}
			int totalRent = 0;
			for (int i = 0; i<goalHouses;i++){
				totalRent += houses[i].rent;
			}
			
			
			System.out.println("You are now worth " + netWorth + "$.");
			System.out.println("You receive " + totalRent + "$ in rent every month.");
			System.out.println("It took " + (monthCounter-1) + " months to get to this point.");
		}
		return monthCounter-1;

	}
	
}

