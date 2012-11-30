package bankexample;

public aspect BlackListAdvice  {	
	void around(int sum): execution(void withdrow(int)) && args(sum){		
		Account account = (Account) thisJoinPoint.getThis();		
		if(BlackList.getInstance().contains(account.getOwner())){
			System.out.println("limited user!!!");			
		} else {	
			proceed(sum);
		}
	}
	
}
