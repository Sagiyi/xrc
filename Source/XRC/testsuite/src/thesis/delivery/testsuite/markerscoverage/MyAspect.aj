package thesis.delivery.testsuite.markerscoverage;

public aspect MyAspect { 

	pointcut myPointcut() : execution(void doBeforeFrom2aspects());
//	 declare soft: Exception: execution(void main(String[]));
	 declare soft: Exception: execution(void declareSoften());
 	 declare warning: execution(void declareWarning()): "you are warned";
	 declare error: execution(void declareError()): "error";

	 declare @method : public void declareAnnotatnion(): @MyAnnotation();
  
	 declare parents: MyClass extends Parent;
//	 declare parents: MyClass implements MyInterface;
//	 
//	 int MyClass.f;
//	 public void MyClass.AnotherMethod(){
//		 System.out.println("from aspect");
//	 }	 
	 private void MyInterface.privateNotAbstractMethodInInterface(){
		 System.out.println("I'm possible!!!");
	 }
	   
	 declare precedence: OtherAspect, MyAspect;
	
	 before(): myPointcut(){
		 System.out.println("before");
	 }  
	 
	 after(MyClass mc, int x, int y) returning: 
		 execution(void MyClass.afterWithParams(int,int))
		 && target(mc)
		 && args(x, y) {
		 System.out.println("publishedTarget "+ mc + " afterWithParams aspect used params (" + x + ", " + y + ")");
	 }  

//	 after(FigureElement fe, int x, int y) returning:
//	        call(void FigureElement.afterOnly(int, int))
//	        && target(fe)
//	        && args(x, y) {
//	    System.out.println(fe + " moved to (" + x + ", " + y + ")");
//	}

	 
	 void around(): execution(void arroundMe()){
		 System.out.println("before arround");
		 proceed();
		 System.out.println("after arround");
	 }   
	 
	 before(): execution(public void mixMe())
	 ||execution(public void beforeMe()){
			System.out.println("before mixd");
	 }
	 after(): execution(public void mixMe())
		 ||execution(public void afterMe()){
			System.out.println("after mixd");
	 }
	 
//	 static void sourceAndTarget(){
//		 System.out.println("???");
//	 }
//	 
//	 before(): execution(* sourceAndTarget()){?????
//		 sourceAndTarget();
//	 }
}
