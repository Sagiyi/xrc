package bankexample;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
public class LimitedUserTester{
	@Test
	public void withdraw(){				
		String user = "black Mamba";			
		Account account = new Account(user, 50);		
		BlackList.getInstance().add(user);
		account.withdraw(50);//expect to display error, and avoid the withdraw.
		System.out.println("balance of " + account.getOwner() + " is "+ account.getBalance());
		assertTrue(account.getBalance()==50);
	}
	
	@Test
	public void smallWithdraw(){				
		String user = "black Mamba";			
		Account account = new Account(user, 50);		
		BlackList.getInstance().add(user);
		account.withdraw(5);//expect to display warning, but commit the withdraw.
		System.out.println("balance of " + account.getOwner() + " is "+ account.getBalance());
		assertTrue(account.getBalance()==45);
	}
	
	@Test
	public void performence(){
		Calendar timeStart = Calendar.getInstance();
		String user = "black Mamba";			
		Account account = new Account(user, 50);		
		BlackList.getInstance().add(user);
		account.withdraw(5);//expect to display warning, but commit the withdraw.
		Calendar timeEnd = Calendar.getInstance();
		long duration = timeEnd.getTimeInMillis() - timeStart.getTimeInMillis();
		System.out.println("duration is " + duration);
		assertTrue(duration < 1500); 
	}
}
