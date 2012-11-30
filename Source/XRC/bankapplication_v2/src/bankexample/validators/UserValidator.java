package bankexample.validators;

import bankexample.Account;

public class UserValidator implements AccountValidator{

	protected int _limit=0;
	protected Account _account;	
	protected int _counter = 0;//for regular user -
							   //every withdraw that make overdruft,or while in overdruft.  
	public UserValidator() {}
	public UserValidator(Account account) {
		super();
		this._account = account;
	}   

	public boolean validateWithdraw(int withdraw){
		boolean validWithdraw =	_account.getBalance() + _limit > withdraw;
		if(_account.getBalance() < withdraw)
			_counter++;
		return validWithdraw;
	}
	
	public int getCounter() {
		return _counter;
	}
}
