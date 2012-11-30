package xrc.api.ide.eclipse.core.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.HandleProviderDelimiter;
import org.eclipse.ajdt.core.model.AJRelationshipManager;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import xrc.api.common.RcException;
import xrc.api.ide.eclipse.core.dto.MarkersInfo;
import xrc.api.ide.eclipse.core.dto.XMD;
import xrc.api.rc.IRcPropertyReader;
import xrc.config.ConfigManager;
import xrc.config.MarkerAttribute;
import xrc.config.Constants.AdviceMarkerType;


/**
 * TODO: medium-low priority: general/in read(IResource resource) - consider the use of relationships, and the class org.eclipse.ajdt.internal.ui.markers.UpdateAJMarkers.createMarker...
 * @author Sagi Ifrah
 *
 */
public class XMDReader extends
		MarkersReader<XMD> {
	
	private final static Logger _logger = Logger.getLogger(XMDReader.class);
	private static ConfigManager _configManager = ConfigManager.getInstance();

	public XMDReader(IRcPropertyReader<XMD> propertyReader) {
		super(propertyReader);		
	}
	
	/* (non-Javadoc)
	 * @see xrc.eclipse.core.IMarkersWriter#read(org.eclipse.core.resources.IResource)
	 */
	@Override
	public IMarker[] read(IResource resource) throws RcException{		
		XMD propertyObject = readProperty(resource); 
		if(propertyObject==null){
			propertyObject = new XMD();	
		}			
		IMarker[] markers = createAndAssignMarkers(propertyObject, resource);
		return  markers;		
	}	
	
	/**
	 * @throws RcException 
	 * @Redundant not in use, maybe if needs in future.
	 * Notice that it's changing the resource - creating new markers on it.
	 */
	MarkersInfo convertToMarkersInfo(XMD source, IResource resource) throws RcException{
		MarkersInfo markersInfo = new MarkersInfo();
		IMarker[] markers = createAndAssignMarkers(source, resource);
		try {
			markersInfo.add(markers);
			return  markersInfo;
		} catch (CoreException e) {				
			throw new RcException(e);				
		}
	}
	
	public IMarker[] createAndAssignMarkers(XMD source, IResource resource) throws RcException{

		Map<Integer, List<IRelationship>> data = source.getData();	
		IMarker[] markers = new IMarker[source.size()];		
		int i=0;
		for (Entry<Integer, List<IRelationship>> relationshipsPerLine : data.entrySet()) {
			Integer line = relationshipsPerLine.getKey();
			List<IRelationship> relationships = relationshipsPerLine.getValue();
			for (IRelationship relationship : relationships) {				
				IMarker newMarker;
				try {
					newMarker = createMarker(resource,line,relationship);
					markers[i]=newMarker; i++;
				
				} catch (CoreException e) {				
					throw new RcException(e);				
				}							
			}
		}
		return markers;
	}

	protected String getPropertyName(){
		return _configManager.getRcPropertyNameRelationshipsInfo();
	}
	private IMarker createMarker(IResource resource, Integer line,
			IRelationship relationship) throws CoreException {
		String markerType = getMarkerType(relationship);
		int severity = -1; 
		if(markerType.contains("`")){//HandleProviderDelimiter.DECLARE.getDelimiter()
			int delimeterIndex = markerType.indexOf("`");
			severity = Integer.valueOf(markerType.substring(delimeterIndex+1));
			markerType = markerType.substring(0,delimeterIndex);
		}
		String message = getMarkerMessage(resource, relationship);
		IMarker marker = resource.createMarker(markerType);
		marker.setAttribute(IMarker.LINE_NUMBER, line);		
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		
		if(severity!=-1){
			marker.setAttribute(IMarker.SEVERITY, severity);
		}
		
		//TODO: extract constants to _config_manager
		marker.setAttribute(MarkerAttribute.RELATIONSHIP_NAME.toString(), relationship.getName());//previously was "relationship.name"
		marker.setAttribute(MarkerAttribute.RELATIONSHIP_KIND.toString(), relationship.getKind().toString());//previously was "relationship.kind" 
		marker.setAttribute(MarkerAttribute.RELATIONSHIP_SOURCE_HANDLE.toString(), relationship.getSourceHandle().toString());//previously was "relationship.sourceHanle"
		marker.setAttribute(MarkerAttribute.RELATIONSHIP_TARGETS.toString(), relationship.getTargets().toString());//previously was "relationship.targets"

		return marker;
	}
	
	private String getMarkerMessageOrg(IRelationship relationship) {
		return //TODO: organize and use StringBuilder
			"Marker ["+
			"name=" +relationship.getName()+ ",\n" +
			"kind=" + relationship.getKind()+ ",\n" +			
			"sourceHanle=" + relationship.getSourceHandle() + ",\n" +
			"targets=" + relationship.getTargets() + ",\n" +
			"]";
	}

	private String getMarkerMessage(IResource resource, IRelationship relationship) {
		MarkerMessageFormatter formatter = new MarkerMessageFormatter();
		return formatter.getMarkerMessage(relationship);
	}
	
	//TODO: move to other place... utility?  was private not static
	public static String getMarkerType(IRelationship relationship) {
		String name = relationship.getName();
		if(relationship.getTargets() == null || relationship.getTargets().size() == 0){
			_logger.warn("getMarkerType: relationship.getTargets() == null || relationship.getTargets().size() == 0");
			_logger.warn("relationship=" + relationship);
			return "";//TODO: null is probably better than "", since it'll throw exception? maybe throw exception here for misuse, or delete the if and get ARrayBoundException naturally from the ..get(0).
		}
		if(name.equals(AJRelationshipManager.ADVISED_BY.getDisplayName())){			
			if(relationship.getTargets().size() > 1){//TODO: if all are the same (e.g. 3 after) it should be return.
				return AdviceMarkerType.advicemarker.getQualifiedName();//"org.eclipse.ajdt.ui.advicemarker";//TODO: get the right one (after/before etc) from somewhere, maybe use enum in ConfigManager.
			}//see org.eclipse.ajdt.internal.ui.markers.UpdateAJMarkers.getMarkerTypeForRelationship(...)
			String targetHandle = (String)relationship.getTargets().get(0);			
			MarkerIconType targetMarkerIconType = MarkerIconType.valueOf(getTargetMarkerType(targetHandle));
			return targetMarkerIconType.getQualifiedName();
		}
		if(name.equals(AJRelationshipManager.ASPECT_DECLARATIONS.getDisplayName())){
			return AdviceMarkerType.itdmarker.getQualifiedName();//"org.eclipse.ajdt.ui.itdmarker";
//			return "org.eclipse.ajdt.ui.annotatedmarker";
			//TODO: find the differnece with "org.eclipse.ajdt.ui.annotatedmarker", and if need to be handled too (both use same icon).
		}
		if(name.equals(AJRelationshipManager.SOFTENED_BY.getDisplayName())
		 ||name.equals(AJRelationshipManager.ANNOTATED_BY.getDisplayName())){
			if(_logger.isDebugEnabled())
				_logger.debug("relationship kind="+relationship.getKind());
			return AdviceMarkerType.itdmarker.getQualifiedName();
//			return "org.eclipse.ajdt.ui.annotatedmarker";
			//TODO: find the differnece with "org.eclipse.ajdt.ui.annotatedmarker", and if need to be handled too (both use same icon).
		}
		if(name.equals(AJRelationshipManager.MATCHES_DECLARE.getDisplayName())){
			if(relationship.getTargets().size()==1){
				String  target = (String) relationship.getTargets().get(0);
				String targetType = target.substring(target.indexOf(HandleProviderDelimiter.DECLARE.getDelimiter())+1);
				if(_logger.isDebugEnabled())
					_logger.debug("targetType = " + targetType);
				int severity = 0;
				if(targetType.startsWith("declare error")){
					severity = IMarker.SEVERITY_ERROR;
					return "xrc.errorWithBackgroundSupport"
					+"`"+ severity;
				}
				if(targetType.startsWith("declare warning")){
					severity = IMarker.SEVERITY_WARNING;
					return "xrc.warningWithBackgroundSupport"
					+"`"+ severity;
				}
				return //worked with AJDT markers, but not for diff (does not support highlight):
						"org.eclipse.ajdt.ui.problemmarker"
//						
//						"xrc.warningWithBackgroundSupport"
						+"`"+ severity;//AdviceMarkerType.???.getQualifiedName();
			}
			if(_logger.isDebugEnabled())
				_logger.debug("relationship kind = "+relationship.getKind());
			return AdviceMarkerType.itdmarker.getQualifiedName();			
//			return "org.eclipse.ajdt.ui.annotatedmarker";
			//TODO: find the differnece with "org.eclipse.ajdt.ui.annotatedmarker", and if need to be handled too (both use same icon).
		}
		
		//for aj history TODO: move to other place and verify there is no other logic missing that differ the aj from java handling.
		if(name.equals(AJRelationshipManager.ADVISES.getDisplayName())){
			if(relationship.getTargets().size() > 1){//TODO: see above for adviceBy
				return AdviceMarkerType.sourceadvicemarker.getQualifiedName();//TODO: see above for adviceBy
			}//TODO: see above for adviceBy
			String sourceHandle = (String)relationship.getSourceHandle();			
			MarkerIconType targetMarkerIconType = MarkerIconType.valueOf("source"+getTargetMarkerType(sourceHandle));
			return targetMarkerIconType.getQualifiedName();
		}
		if(name.equals(AJRelationshipManager.DECLARED_ON.getDisplayName())){
			return AdviceMarkerType.sourceitdmarker.getQualifiedName();
		}
		if(name.equals(AJRelationshipManager.SOFTENS.getDisplayName())
		 ||name.equals(AJRelationshipManager.ANNOTATES.getDisplayName())){
			if(_logger.isDebugEnabled())
				_logger.debug("relationship kind = "+relationship.getKind());
			return AdviceMarkerType.sourceitdmarker.getQualifiedName();
			//TODO: see above..					
		}
		if(name.equals(AJRelationshipManager.MATCHED_BY.getDisplayName())){
			return AdviceMarkerType.sourceitdmarker.getQualifiedName();
		}

		if(_logger.isDebugEnabled()){
			_logger.debug("relationship is not ADVISED_BY. name = " + name);
			_logger.debug("relationship = " + relationship);
		}
		return "";//??? TODO: "org.eclipse.ajdt.ui.advicemarker";//TODO: what to return if not found?
	}
	
	public static String getTargetMarkerType(String targetHandle){
		if(_logger.isDebugEnabled())
			_logger.debug("targetHandle is:\n" + targetHandle);
		String typeWithSuffix = targetHandle.substring(targetHandle.indexOf("&") + 1);
		
		int endDelimeterIndOption1 = typeWithSuffix.indexOf("!");
		int endDelimeterIndOption2 = typeWithSuffix.indexOf("&");
		//we want the min of these options that is bigger than 0.
		int min = Math.min(endDelimeterIndOption1, endDelimeterIndOption2);		
		int endDelimeterInd;
		if(min > 0){
			endDelimeterInd = min; 
		} else {
			int max = Math.max(endDelimeterIndOption1, endDelimeterIndOption2);
			if(max > 0){
				endDelimeterInd = max;
			} else {
				return typeWithSuffix;
			}
		}
		typeWithSuffix = typeWithSuffix.substring(0,endDelimeterInd);
		int endDelimeterIndOption3 = typeWithSuffix.indexOf("Returning");
		if(endDelimeterIndOption3 < 0)
			return typeWithSuffix; 
		return typeWithSuffix.substring(0,endDelimeterIndOption3);
	}

	//lower-case only because targetHandle use lower-case, and it's unworthy complication to support both.
	//TODO: move to - -_configManager? since it depends on ajdt plugin definitions
	enum MarkerIconType{
		before("org.eclipse.ajdt.ui.beforeadvicemarker"),
		after("org.eclipse.ajdt.ui.afteradvicemarker"),
		around("org.eclipse.ajdt.ui.aroundadvicemarker"),
		
		sourcebefore("org.eclipse.ajdt.ui.sourcebeforeadvicemarker"),
		sourceafter("org.eclipse.ajdt.ui.sourceafteradvicemarker"),
		sourcearound("org.eclipse.ajdt.ui.sourcearoundadvicemarker");
		
		private String _qualifiedName;	

		private MarkerIconType(String qualifiedName) {
			this._qualifiedName = qualifiedName;
		}
		
		public String getQualifiedName() {
			return _qualifiedName;
		}	    
	}

	@Override
	protected List<String> getMarkerTypes() {
		String type = _configManager.getAdviceMarkerGenericType();
		List<String> markerTypes = new ArrayList<String>(1);
		markerTypes.add(type);
		return markerTypes;
	}
	
}
