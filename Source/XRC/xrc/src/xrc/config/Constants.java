package xrc.config;

public interface Constants {
//	from ajdt plugin.xml
	String[] ADVICE_MARKER_TYPES = {
			"org.eclipse.ajdt.ui.advicemarker",
		    "org.eclipse.ajdt.ui.sourceandtargetmarker",
		    "org.eclipse.ajdt.ui.dynamicsourceandtargetmarker",
		    "org.eclipse.ajdt.ui.dynamicadvicemarker",
		    "org.eclipse.ajdt.ui.afteradvicemarker",
		    "org.eclipse.ajdt.ui.beforeadvicemarker",
		    "org.eclipse.ajdt.ui.aroundadvicemarker",
		    "org.eclipse.ajdt.ui.sourceadvicemarker",
		    "org.eclipse.ajdt.ui.sourcedynamicadvicemarker",
		    "org.eclipse.ajdt.ui.sourcebeforeadvicemarker",
		    "org.eclipse.ajdt.ui.sourceafteradvicemarker",
		    "org.eclipse.ajdt.ui.sourcearoundadvicemarker",
		    "org.eclipse.ajdt.ui.dynamicafteradvicemarker",
		    "org.eclipse.ajdt.ui.dynamicbeforeadvicemarker",
		    "org.eclipse.ajdt.ui.dynamicaroundadvicemarker",
		    "org.eclipse.ajdt.ui.sourcedynamicafteradvicemarker",
		    "org.eclipse.ajdt.ui.sourcedynamicbeforeadvicemarker",
		    "org.eclipse.ajdt.ui.sourcedynamicaroundadvicemarker",
		    "org.eclipse.ajdt.ui.declarationmarker",
		    "org.eclipse.ajdt.ui.itdmarker",
		    "org.eclipse.ajdt.ui.annotatedmarker",
		    "org.eclipse.ajdt.ui.sourceitdmarker",
		    "org.eclipse.ajdt.ui.sourceannotatedmarker",
		    "org.eclipse.ajdt.ui.customadvicemarker",
			"org.eclipse.ajdt.ui.changedadvicemarker",
			//problem markers ( markerType="org.eclipse.ajdt.ui.problemmarker" )
			"org.eclipse.ajdt.ui.warning",
			"org.eclipse.ajdt.ui.error"
			
			,"org.eclipse.ajdt.ui.problemmarker"
		};
	
//	base on ajdt plugin.xml
	enum AdviceMarkerType{
		advicemarker("org.eclipse.ajdt.ui.advicemarker"),
	    sourceandtargetmarker("org.eclipse.ajdt.ui.sourceandtargetmarker"),
	    dynamicsourceandtargetmarker("org.eclipse.ajdt.ui.dynamicsourceandtargetmarker"),
	    dynamicadvicemarker("org.eclipse.ajdt.ui.dynamicadvicemarker"),
	    afteradvicemarker("org.eclipse.ajdt.ui.afteradvicemarker"),
	    beforeadvicemarker("org.eclipse.ajdt.ui.beforeadvicemarker"),
	    aroundadvicemarker("org.eclipse.ajdt.ui.aroundadvicemarker"),
	    sourceadvicemarker("org.eclipse.ajdt.ui.sourceadvicemarker"),
	    sourcedynamicadvicemarker("org.eclipse.ajdt.ui.sourcedynamicadvicemarker"),
	    sourcebeforeadvicemarker("org.eclipse.ajdt.ui.sourcebeforeadvicemarker"),
	    sourceafteradvicemarker("org.eclipse.ajdt.ui.sourceafteradvicemarker"),
	    sourcearoundadvicemarker("org.eclipse.ajdt.ui.sourcearoundadvicemarker"),
	    dynamicafteradvicemarker("org.eclipse.ajdt.ui.dynamicafteradvicemarker"),
	    dynamicbeforeadvicemarker("org.eclipse.ajdt.ui.dynamicbeforeadvicemarker"),
	    dynamicaroundadvicemarker("org.eclipse.ajdt.ui.dynamicaroundadvicemarker"),
	    sourcedynamicafteradvicemarker("org.eclipse.ajdt.ui.sourcedynamicafteradvicemarker"),
	    sourcedynamicbeforeadvicemarker("org.eclipse.ajdt.ui.sourcedynamicbeforeadvicemarker"),
	    sourcedynamicaroundadvicemarker("org.eclipse.ajdt.ui.sourcedynamicaroundadvicemarker"),
	    declarationmarker("org.eclipse.ajdt.ui.declarationmarker"),
	    itdmarker("org.eclipse.ajdt.ui.itdmarker"),
	    annotatedmarker("org.eclipse.ajdt.ui.annotatedmarker"),
	    sourceitdmarker("org.eclipse.ajdt.ui.sourceitdmarker"),
	    sourceannotatedmarker("org.eclipse.ajdt.ui.sourceannotatedmarker"),
	    customadvicemarker("org.eclipse.ajdt.ui.customadvicemarker"),
		changedadvicemarker("org.eclipse.ajdt.ui.changedadvicemarker");
				
		private String _qualifiedName;	

		private AdviceMarkerType(String qualifiedName) {
			this._qualifiedName = qualifiedName;
		}
		
		public String getQualifiedName() {
			return _qualifiedName;
		}
	}
	
	String ASPECTJ_PROPERTY_NAME = "aspectJ:";
	String ADVICE_MARKER_GENERIC_TYPE = "org.eclipse.ajdt.ui.advicemarker";
	String MARKER_ATTRIBUTE_NAME__TYPE_FOR_XRC = "MarkerType4XRC";//"Type4AARC";
	String RC_PROPERTY_NAME__RELATIONSHIPS_INFO = "XRC_RELATIONSHIPS";//"aarcRelationshipsInfo:";
	
	//see section <!-- Marker declarations -->  in org.eclipse.ajdt.ui/plugin.xml
	final String[][] DECORATED_MERGE_VIEWER_RULER_ANNOTATION_TYPE = {
			{
				"org.eclipse.debug.core.breakpoint",
				"org.eclipse.ajdt.advice",
				"org.eclipse.ajdt.aspectDeclaration"
				
//				,"org.eclipse.jdt.core.problem"
//				,"org.eclipse.core.resources.problemmarker"
				,"org.eclipse.ajdt.ui.problemmarker"
				,"org.eclipse.ajdt.ui.warning"
				,"org.eclipse.ajdt.ui.error"
			},
			{
				"org.eclipse.ajdt.changedAdvice"
			}
	};
	
	String[] DIFF_MARKER_TYPE_ROOTS = {"org.eclipse.ajdt.ui.changedadvicemarker"};

	
	boolean IS_DIFF_DISPLAY_IGNORE_MODIFY_FLUG = true;
	boolean IS_DIFF_DISPLAY_IGNORE_VERSIONS = true;
	boolean IS_DIFF_FOR_CHANGE_CONTROL_IGNORE_MODIFY_FLUG = false;
	boolean IS_DIFF_FOR_CHANGE_CONTROL_IGNORE_VERSIONS = false;

}
