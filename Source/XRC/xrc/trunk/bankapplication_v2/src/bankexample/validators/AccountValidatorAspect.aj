package bankexample.validators;
import bankexample.*;
public aspect AccountValidatorAspect {
	
	private AccountValidator Account._validator;
	
	public void Account.setValidator(AccountValidator validator) {
		_validator = validator;
    }	
	public AccountValidator Account.getValidator( ) {
		return _validator;
    }	
	
	//before running withdraw run the aspect	
	void around(int sum): BlackListAdvice.pcWithdraw(sum){
		Account account = (Account) thisJoinPoint.getThis();
		if(account._validator.validateWithdraw(sum))
			proceed(sum);
		else
			System.out.println("witdraw validation failed!\n" +
					account.getOwner()+", your balance is "+ account.getBalance() +
					".\nyou are not allow to withdraw " + sum);
	} 
	
	//for account constructor in case in has not validator, put regular validator
	before(): execution(Account.new(..)){
		Account account = (Account) thisJoinPoint.getThis();
		if(account.getValidator()==null){
			account.setValidator(new UserValidator(account));
		}
		
	}
}
