package thesis.delivery.testsuite.markerscoverage;

public class MyClass { 
	public void declareSoften() throws Exception{
		System.out.println("Throws runtime SoftException instead of the declared Exception");
	}	
	public void declareWarning() throws Exception{
		System.out.println("warning :-P");
	}	
	public void declareError() throws Exception{
		System.out.println("error");
	} 
	public void declareAnnotatnion(){
		System.out.println("is annotated?");
	}
	public void arroundMe(){     
		System.out.println("is arround");
	}	
	public void mixMe(){ 
		System.out.println("is mixd");
	}	
	public void beforeMe(){  
		System.out.println("is before");
	}	 
	public void afterMe(){
		System.out.println("is after");
	}

	//TODO: verify hint message in history/compare is fine both in class and aspect version.
	public void afterWithParams(int x, int y){
		System.out.println("is after only, I have params [x=" + x +",y="+y+"]");
	} 
	 
	public void doBeforeFrom2aspects() throws Exception{
		System.out.println("do before from 2 aspects?"); 
	}
	public static void main(String[] args) throws Exception {
		MyClass owner = new MyClass();
		owner.afterWithParams(2,5);
		owner.doBeforeFrom2aspects();
		owner.declareWarning(); 
	}
		
}
