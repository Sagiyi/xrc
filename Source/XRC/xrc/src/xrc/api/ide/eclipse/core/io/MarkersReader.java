package xrc.api.ide.eclipse.core.io;

import java.io.Serializable;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import xrc.api.common.RcException;
import xrc.api.ide.IMarkersReader;
import xrc.api.rc.IRcPropertyReader;
import xrc.config.ConfigManager;


/**
 * History: 
 * MarkersReader came from MarkerStreamer re-factoring, 
 */
public abstract class MarkersReader<PropertyValueType extends Serializable> implements IMarkersReader {

	private ConfigManager _configManager = ConfigManager.getInstance();
	
	private IRcPropertyReader<PropertyValueType> _propertyReader;	
	
	public MarkersReader(IRcPropertyReader<PropertyValueType> propertyReader) {
		super();
		this._propertyReader = propertyReader;
	}
	
	/**
	 * To enable children to decide the property-name to read
	 * @return property-name to read
	 */
	abstract protected String getPropertyName();
	abstract protected List<String> getMarkerTypes();

	protected PropertyValueType readProperty(IResource resource) throws RcException{		
//		try {
			clearMarkers(resource);			
			PropertyValueType propertyObject = _propertyReader.read(getPropertyName());
			return propertyObject;
//		} catch (CoreException e) {
//			_logger.warn(e + "failed with " +resource.getName());		    	  
//			throw new RcException(e);
//		}		
	}	
	
	//TODO: move this to some MarkerManager...
	public void clearMarkers(IResource resource) throws RcException{	
//		String type = _configManager.getAdviceMarkerGenericType();		
		List<String> markerTypes = getMarkerTypes();
		for (String markerType : markerTypes) {			
			try {
				//for debug:			
				//IMarker[] markers = resource.findMarkers(type, true, IResource.DEPTH_INFINITE);
				resource.deleteMarkers(markerType, true, IResource.DEPTH_ZERO);
			} catch (CoreException e) {
				throw new RcException(e);				
			}
		}
	}	
}
