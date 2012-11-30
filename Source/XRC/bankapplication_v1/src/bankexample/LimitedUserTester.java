package bankexample;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
public class LimitedUserTester{
	@Test
	public void withdrow(){				
		String user = "black Mamba";			
		Account account = new Account(user, 50);		
		BlackList.getInstance().add(user);
		account.withdraw(50);//expect to display error, and avoid the withdraw.
		System.out.println("balance of " + account.getOwner() + " is "+ account.getBalance());
		assertTrue(account.getBalance()==50);
	}
}
