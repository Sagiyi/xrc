package bankexample;


public aspect Trace {
	
	private boolean isTraceEnabled = false;
	
	Object around(): execution(* *(..))  && ! within(accountexample.Trace){				
		Object jointPoint = thisJoinPoint;
		Object ret;
		if(isTraceEnabled){	
			System.out.println("start: " + jointPoint);	
			ret = proceed();
			System.out.println("finish: " + jointPoint);
		} else {
			ret = proceed();
		}
		return ret;
	} 
} 
