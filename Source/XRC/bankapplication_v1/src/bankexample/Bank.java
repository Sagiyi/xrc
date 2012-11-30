package bankexample;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class Bank {
	
	private Map<String , Account> accounts = new HashMap<String,Account>();	
	
	public Account getAccount(String user){
		return accounts.get(user);
	}
	
	public Account addAccount(String user, Account account){
		return accounts.put(user, account);
	}
	

	@Test
	public void test(){				
		String user = "black Mamba";			
		Account account = new Account(user, 0);		
		account.deposit(50);
		
		BlackList.getInstance().add(user);
		
		account.withdraw(50);//expect to display error, and avoid the withdraw.
		System.out.println("balance of " + account.getOwner() + " is "+ account.getBalance());
		assertTrue(account.getBalance()==50);
	} 
	

	public static void main(String[] args) {		
		Bank bank = new Bank();
				
		String user = "black Mamba";			
		Account account = new Account(user, 0);
		bank.addAccount(user, account);
		account.deposit(50);
				
		Account account2 = bank.getAccount(user);
		account2.withdraw(50);
		System.out.println("balance of " + account.getOwner() + " is "+ account.getBalance());
		
		BlackList.getInstance().add(user);
		
		account2.withdraw(50);//expect to display error, and avoid the withdraw.
		System.out.println("balance of " + account.getOwner() + " is "+ account.getBalance());
		assertTrue(account2.getBalance()==0);
	}	
}
