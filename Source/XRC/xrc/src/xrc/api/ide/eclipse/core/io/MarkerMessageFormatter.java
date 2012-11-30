package xrc.api.ide.eclipse.core.io;

import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.HandleProviderDelimiter;

import xrc.api.ide.eclipse.plugin.compare.ui.ajdt.DiffType;

/**
 *  TODO: verify extractDisplaySourceHandle() is good, or eliminate it (came from merge) 
 */
public class MarkerMessageFormatter {
	
	private final static Logger _logger = Logger.getLogger(MarkerMessageFormatter.class);

//	private final AJProjectModelFacade model;
	/**
	 * It seems that I can't enjoy the parsing of the model because there is no real model - 
	 * I don't have the aspects etc. but only the data I restored
	 * so I had to write other parser
	 */
	public MarkerMessageFormatter(){
		super();
//		model = null;
	}
//	public MarkerMessageFormatter(IProject project){
//		this.model = AJProjectModelFactory.getInstance().getModelForProject(project);
//	}

//	UpdateAJMarkers.getMarkerLabel
//	advised by TraceAspect.after(): <anonymous pointcut>
//	Marker [name=advised by, kind=advice, 
//	        sourceHanle==thesis.delivery.test.aspectj.simplestexample/
//	        src<thesis.delivery.test.aspectj.simplestexample{Dog.java[Dog~bark234, targets=
//	        [=thesis.delivery.test.aspectj.simplestexample/
//	        src<thesis.delivery.test.aspectj.simplestexample*TraceAspect.aj}TraceAspect&after, 
//	        =thesis.delivery.test.aspectj.simplestexample/
//	        src<thesis.delivery.test.aspectj.simplestexample*TraceAspect.aj}TraceAspect&before], ]
	
//	=accountexample/src<accountexample.validators*VipValidatorAspect.aj}VipValidatorAspect`declare parents

	//TODO: consider CharSequence...
//	TODO: replace with constants from HandleProviderDelimiter.COMPILATIONUNIT.getDelimiter());
	public String getMarkerMessage(IRelationship relationship) {
		if(!relationship.isAffects()){//is the class, not the aspect
			return getMarkerMessageForClass(relationship);
		}else{//aspect
			return getMarkerMessageForAspect(relationship); 
		}
	}
	public String getMarkerMessageForAspect(IRelationship relationship) {		
		StringBuilder messageSB = new StringBuilder();
		String name = relationship.getName();
		String displaySourceHandle = extractDisplaySourceHandle(relationship.getSourceHandle());
		List<?> targets = relationship.getTargets();
		for (Object object : targets) {			
//			getMarkerLabel(relationship);
			String target = object.toString();
			if(_logger.isTraceEnabled())
				_logger.trace("getMarkerMessageForAspect\n" + target);
			int aspectStartDelimeter = target.indexOf(HandleProviderDelimiter.TYPE.getDelimiter());//'['

			int aspectEndDelimeter = target.indexOf(HandleProviderDelimiter.METHOD.getDelimiter());//'~');

//			if(aspectEndDelimeter == -1 ){
//				aspectEndDelimeter = target.indexOf(HandleProviderDelimiter.ITD.getDelimiter());//')');
//			}
			if(aspectEndDelimeter == -1){
				aspectEndDelimeter = target.length();
			}
			int endDelimeterInd = target.length();
			
			String targetSrc = target.substring(aspectStartDelimeter + 1, aspectEndDelimeter);//TODO: src revision
			String type;
//			int endDelimeterInd = target.indexOf(HandleProviderDelimiter.COUNT.getDelimiter());//"!");
			if(endDelimeterInd > aspectEndDelimeter){
				type = target.substring(aspectEndDelimeter + 1,endDelimeterInd);
			} else{
				type = "";//target.substring(aspectEndDelimeter + 1);
			}			
			//this comment is what we had before extractDisplaySourceHandle() was added.
			messageSB.append(name).append(" ")
				.append(targetSrc).append(".")
				.append(type)
				.append(System.getProperty("line.separator"));
//			messageSB.append(name).append(" ")
//				.append(targetSrc).append(".").append(type)
//				.append(" on ").append(displaySourceHandle)
//				.append("\r").append("\n");
			
//			}else{
//				messageSB.append(name).append("TODO...");//TODO???... could it be not aspect here?
//			}
		}
		return messageSB.toString();
//		"MarkerMessageFormatter: getMarkerMessage: TODO...";
	}
	
	public String getMarkerMessageForClass(IRelationship relationship) {		
		StringBuilder messageSB = new StringBuilder();
		String name = relationship.getName();
		String displaySourceHandle = extractDisplaySourceHandle(relationship.getSourceHandle());
		List<?> targets = relationship.getTargets();
		for (Object object : targets) {			
//			getMarkerLabel(relationship);
			String target = object.toString();
			if(_logger.isTraceEnabled())
				_logger.trace("getMarkerMessageForClass:\n"+target);
			int aspectStartDelimeter = target.indexOf(HandleProviderDelimiter.ASPECT_TYPE.getDelimiter());//'}'
//			boolean isAspect =  aspectStartDelimeter < 0;
//			if(isAspect){
			int aspectEndDelimeter = target.indexOf(HandleProviderDelimiter.ADVICE.getDelimiter());//'&');

			if(aspectEndDelimeter == -1 ){
				aspectEndDelimeter = target.indexOf(HandleProviderDelimiter.ITD.getDelimiter());//')');
			}
			if(aspectEndDelimeter == -1 ){
				aspectEndDelimeter = target.indexOf(HandleProviderDelimiter.DECLARE.getDelimiter());//'`');//TODO: this is guts shot. I didn't tought of it (see Processor)
			}
			String targetSrc = target.substring(aspectStartDelimeter + 1, aspectEndDelimeter);//TODO: src revision
			String type;
			int endDelimeterInd = target.indexOf(HandleProviderDelimiter.COUNT.getDelimiter());//"!");
			if(endDelimeterInd>0){
				type = target.substring(aspectEndDelimeter + 1,endDelimeterInd);
			} else{
				type = target.substring(aspectEndDelimeter + 1);
			}			
			//this comment is what we had before extractDisplaySourceHandle() was added.
			messageSB.append(name).append(" ")
				.append(targetSrc).append(".").append(type).append(System.getProperty("line.separator"));
//			messageSB.append(name).append(" ")
//				.append(targetSrc).append(".").append(type)
//				.append(" on ").append(displaySourceHandle)
//				.append("\r").append("\n");
			
//			}else{
//				messageSB.append(name).append("TODO...");//TODO???... could it be not aspect here?
//			}
		}
		return messageSB.toString();
	}

	
	private String extractDisplaySourceHandle(String sourceHandle) {
		int startIndex = sourceHandle.indexOf("~")+1;
		return sourceHandle.substring(startIndex);		
	}
	
	public static boolean isModified(IRelationship relationship) {		
		List<?> targets = relationship.getTargets();
		for (Object object : targets) {			
//			getMarkerLabel(relationship);
			String target = object.toString();
			if(_logger.isTraceEnabled())
				_logger.trace("isModified target:\n" + target);
			int revisionNoStartIndex = target.indexOf("[*");
			if(revisionNoStartIndex != -1 ){				
				return true;
			}
			
		}
		return false;
	}
	
	public String getMarkerMessage(IRelationship relationship, DiffType diffType) {
		StringBuilder messageSB = new StringBuilder();
		messageSB.append(" --> ");
		messageSB.append(diffType.getLabel());
		messageSB.append(" ").append(getMarkerMessage(relationship));
		return messageSB.toString();
	}
//	=thesis.delivery.test.aspectj.simplestexample/src<thesis.delivery.test.aspectj.simplestexample*TraceAspect.aj}TraceAspect&after
	
//	//base on UpdateAJMarkers.getMarkerLabel:
//	public String getMarkerLabel(IRelationship relationship) {
//		IProgramElement target = model.getProgramElement(
//				(String) relationship.getTargets().get(0));
//		return relationship.getName()
//		+ " "
//		+ (target != null ? target.toLinkLabelString(false) : "null") 
//		+ (relationship.hasRuntimeTest() ?
//				" (runtime test)"  	                         
//				: ""); 
//	}

}
