package bankexample.validators;

import bankexample.Account;
import java.io.Serializable;
public aspect VipValidatorAspect {
//  declare parents : VipValidator extends SpecialUserValidator;//comment out ,for the bug
	declare parents : VipValidator extends UserValidator;//uncomment for the bug

 
	//we don't need alarm for VIP.  
	public VipValidator.new (Account account) {		
		super(account);
		_limit = 2000;	
//		_alarmLimit = -10000;//comment out ,for the bug
	}
	  
	//override the VipValidator.validateWithdraw
	boolean around(int sum): 
		execution(* VipValidator.validateWithdraw(int)) && args(sum){
		VipValidator account = (VipValidator) thisJoinPoint.getThis();
		return account.superValidateWithdraw(sum);
	}  
	 
	public boolean VipValidator.superValidateWithdraw(int sum){
		return super.validateWithdraw(sum);
	} 
	
}
