package xrc.config;

import org.apache.log4j.PropertyConfigurator;

import xrc.api.rc.IRcFactory;
import xrc.api.rc.svn.SvnFactory;


public class ConfigManager {
	
	private static final ConfigManager _instance = new ConfigManager();
	
	private ConfigManager(){
//		BasicConfigurator.configure();//for log4j
		//TODO...
		System.out.println("ConfigManager: trying to configure log4j:");
		String log4jPath = "C:\\dev\\ws42\\xrc\\src\\xrc\\config\\log4j.props";
		//"C:\\aop\\New\\ws37_2\\vcsdecorationconnectorNew\\src\\aarc\\config\\log4j.props");
			PropertyConfigurator.configure(log4jPath);
	}
	
	public IRcFactory createRcFactory(){//TODO: consider singleton
		return new SvnFactory();
		
	}
	
	public static ConfigManager getInstance(){
		return _instance;
	}
	
	public String[] getAdviceMarkerTypes(){		
		return Constants.ADVICE_MARKER_TYPES;
	}
	
	public String getAspectJPropertyName(){
		return Constants.ASPECTJ_PROPERTY_NAME;
	}
	
	public String getAdviceMarkerGenericType(){
		return Constants.ADVICE_MARKER_GENERIC_TYPE;
	}
	
	public String getMarkerAttributeNameType4XRC(){
		return Constants.MARKER_ATTRIBUTE_NAME__TYPE_FOR_XRC;
	}

	public String getRcPropertyNameRelationshipsInfo(){
		return Constants.RC_PROPERTY_NAME__RELATIONSHIPS_INFO;
	}
	
	public String[][] getDecoratedMergeViewerRulerAnnotationTypes(){ 
		return Constants.DECORATED_MERGE_VIEWER_RULER_ANNOTATION_TYPE;
	}

	public String[] getDiffMarkerTypeRoots() {		
		return Constants.DIFF_MARKER_TYPE_ROOTS;
	}	
	
	/**
	 * The Diff Engine use diff to identify changes, and highlight them 
	 * This is done on setInput by the DecoratedJavaMergeViewer
	 * This configuration decide if a modifyFlag is considered as a change, 
	 * and requires to highlight the advice markers that are associated with it. 
	 * @return
	 */
	public boolean isDiffDisplayIgnoreModifyFlag(){
		return Constants.IS_DIFF_DISPLAY_IGNORE_MODIFY_FLUG;
	}
	
	/**
	 * The Diff Engine use diff to find the affected files 
	 * (classes XMDs that are affected from aspects, and 
	 * 	aspects XMDs that are affected from classes).
	 * This is done on postAJBuild in order to change the XMDs of the affected files.
	 * This configuration decide if a modifyFlag is considered as a change, 
	 * and requires to modify the XMD of the affected file. 
	 * @return
	 */
	public boolean isDiffForChangeControlIgnoreModifyFlag(){
		return Constants.IS_DIFF_FOR_CHANGE_CONTROL_IGNORE_MODIFY_FLUG;
	}
	
	/**
	 * The Diff Engine use diff to identify changes, and highlight them 
	 * This is done on setInput by the DecoratedJavaMergeViewer
	 * This configuration decide if a version difference is considered as a change, 
	 * and requires to highlight the advice markers that are associated with it. 
	 * @return
	 */
	public boolean isDiffDisplayIgnoreVersions(){
		return Constants.IS_DIFF_DISPLAY_IGNORE_VERSIONS;
	}
	
	/**
	 * The Diff Engine use diff to find the affected files 
	 * (classes XMDs that are affected from aspects, and 
	 * 	aspects XMDs that are affected from classes).
	 * This is done on postAJBuild in order to change the XMDs of the affected files.
	 * This configuration decide if a version difference is considered as a change, 
	 * and requires to modify the XMD of the affected file. 
	 * @return
	 */
	public boolean isDiffForChangeControlIgnoreVersions(){
		return Constants.IS_DIFF_FOR_CHANGE_CONTROL_IGNORE_VERSIONS;
	}
}
