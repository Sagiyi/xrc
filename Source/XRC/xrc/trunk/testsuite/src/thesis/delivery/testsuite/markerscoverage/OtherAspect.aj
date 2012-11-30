package thesis.delivery.testsuite.markerscoverage;

public aspect OtherAspect {

	before(): execution(void doBeforeFrom2aspects()){
		 System.out.println("before Other");
	 }   
}
