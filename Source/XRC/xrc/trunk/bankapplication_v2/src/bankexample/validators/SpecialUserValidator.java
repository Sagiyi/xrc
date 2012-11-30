package bankexample.validators;

import java.util.Calendar;

import bankexample.Account;



public class SpecialUserValidator extends UserValidator implements AccountValidator{
	
	protected int _alarmLimit = 1000;
	protected int _alarmStayOnTime = 3000;//in milis
	protected long _alarmTime = Calendar.getInstance().getTimeInMillis() - _alarmStayOnTime - 1;

	
	public SpecialUserValidator() {}
	
	public SpecialUserValidator(Account account) {		
		super(account);
		_limit = 1000;	
	}

	/* (non-Javadoc)
	 * @see accountexample.validators.AccountValidator#validateWithdraw(int)
	 */
	@Override
	public boolean validateWithdraw(int withdraw){
		boolean result = super.validateWithdraw(withdraw);
		if(_account.getBalance() + _limit <= withdraw + _alarmLimit)
			alarm();
		//for special user - we count only the times it fail to withdraw.
		//we fix the counter (not best way, but it's for demostration)
		if(_account.getBalance() < withdraw 
		&& _account.getBalance() + _limit >= withdraw)
			_counter--;
		return result;
	}
	
	private void alarm(){		
		System.out.println("alarm for " + _account.getOwner());
		_alarmTime = Calendar.getInstance().getTimeInMillis();
	}

	public boolean isAlarmed(){
		long timeSinceLastAlarm = Calendar.getInstance().getTimeInMillis() - _alarmTime;
		return timeSinceLastAlarm < _alarmStayOnTime;
	}
		
}
