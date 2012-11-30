package bankexample.validators;


public class VipValidator 
 
	implements AccountValidator{	

	public VipValidator() {
	}
 
	public boolean validateWithdraw(int withdraw){
		return true;
	}   
}
