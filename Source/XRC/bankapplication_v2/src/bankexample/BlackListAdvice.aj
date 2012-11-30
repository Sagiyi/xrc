package bankexample;

public aspect BlackListAdvice  {		

	public pointcut pcWithdraw(int sum): execution(void withdraw(int)) && args(sum);
	
	void around(int sum): execution(void withdraw(int)) && args(sum){		
		Account account = (Account) thisJoinPoint.getThis();		
		if(BlackList.getInstance().contains(account.getOwner())){
			System.out.println("limited user!!!");
			if( 5 * sum <= account.getBalance()){		
				proceed(sum);	 
			} 
		} else {	
			proceed(sum);
		}
	}
	
	before(int sum): pcWithdraw(sum){
		Account account = (Account) thisJoinPoint.getThis();
		validateAccount(account); //e.g. retrieve some details of the user, write to log its details and time...
	}

	private void validateAccount(Account account){
		try {			
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
