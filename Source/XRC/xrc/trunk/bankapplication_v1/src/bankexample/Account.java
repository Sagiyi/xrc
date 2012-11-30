package bankexample;
public class Account{		
  private int balance;
  private String owner;	
  public Account(
	  String owner, 
	  int balance){
    super();
    this.owner = owner;		
    this.balance = balance;
  } 	 
  public void deposit(int sum){
	balance += sum;	}	
  public void withdraw(int sum){
	balance -= sum;	}		
  public String getOwner() {
	return owner;	}	
  public int getBalance(){
	return balance;	}	
}
