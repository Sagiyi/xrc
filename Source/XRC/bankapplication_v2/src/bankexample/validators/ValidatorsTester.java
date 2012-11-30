package bankexample.validators;
import bankexample.*;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
public class ValidatorsTester {

	@Test
	public void test(){
		Account userAccount = new Account("John Smith", 1000);
		Account specialAccount = new Account("Lord Dracula", 1000);
		Account vipAccount = new Account("King David", 1000);
		
		AccountValidator userAccountValidator = new UserValidator(userAccount);
		AccountValidator specialUserValidator = new SpecialUserValidator(specialAccount);
		AccountValidator vipAccountValidator = new VipValidator(vipAccount);
		
		//john  can't witdraw
		userAccount.setValidator(userAccountValidator);
		userAccount.withdraw(1500);
		
		//lord will get an alarm
		specialAccount.setValidator(specialUserValidator);
		specialAccount.withdraw(1500);
		//lord can't withdraw
		specialAccount.setValidator(specialUserValidator);
		specialAccount.withdraw(1500);

		//vip has no problem (before fix will get an alarm, later not alarmed)
		vipAccount.setValidator(vipAccountValidator);
		vipAccount.withdraw(2500);
		//vip can't withdraw
		vipAccount.setValidator(vipAccountValidator);
		vipAccount.withdraw(2500);
	}
	
	@Test
	public void testRegularUser(){
		Account userAccount = new Account("John Smith", 1000);
		UserValidator userAccountValidator = new UserValidator(userAccount);
		//john  can't witdraw
		userAccount.setValidator(userAccountValidator);
		userAccount.withdraw(1500);
		assertTrue(userAccount.getBalance()==1000);
		userAccount.withdraw(1500);
		assertTrue(userAccountValidator.getCounter()==2);
	}
	
	@Test
	public void testSpecialAccount(){
		Account specialAccount = new Account("Lord Dracula", 1000);
		SpecialUserValidator specialUserValidator = new SpecialUserValidator(specialAccount);
		assertTrue(specialUserValidator.isAlarmed() == false);
		//lord will get an alarm
		specialAccount.setValidator(specialUserValidator);
		specialAccount.withdraw(1500);
		assertTrue(specialAccount.getBalance()==-500);
		//lord can't withdraw
		specialAccount.setValidator(specialUserValidator);
		specialAccount.withdraw(1500);
		assertTrue(specialAccount.getBalance()==-500);
		assertTrue(specialUserValidator.getCounter() == 1);
		assertTrue(specialUserValidator.isAlarmed() == true);
	}
	
	@Test
	public void testVipAccount(){
		Account vipAccount = new Account("King David", 1000);
		VipValidator vipAccountValidator = new VipValidator(vipAccount);
		//vip has no problem (before fix will get an alarm, later not alarmed)
		vipAccount.setValidator(vipAccountValidator);
		vipAccount.withdraw(2500);
		assertTrue(vipAccount.getBalance()==-1500);
		//vip can't withdraw
		vipAccount.setValidator(vipAccountValidator);
		vipAccount.withdraw(2500);
		assertTrue(vipAccount.getBalance()==-1500);
		assertTrue(vipAccountValidator.getCounter() == 1);//we do need counter
//		assertTrue(vipAccountValidator.isAlarmed() == false);//but what about the alarm?
	}
}
