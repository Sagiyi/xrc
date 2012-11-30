package xrc.api.ide.eclipse.core.io;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import xrc.api.common.RcException;
import xrc.api.ide.eclipse.core.dto.MarkerInfo;
import xrc.api.ide.eclipse.core.dto.MarkersInfo;
import xrc.api.rc.IRcPropertyReader;
import xrc.config.ConfigManager;


public class MarkersInfoReader extends MarkersReader<MarkersInfo> {

	private ConfigManager _configManager = ConfigManager.getInstance();

	public MarkersInfoReader(IRcPropertyReader<MarkersInfo> propertyReader) {
		super(propertyReader);		
	}
	
	/* (non-Javadoc)
	 * @see xrc.eclipse.core.IMarkersWriter#read(org.eclipse.core.resources.IResource)
	 */
	@Override
	public IMarker[] read(IResource resource) throws RcException{		
		MarkersInfo propertyObject = readProperty(resource);
		List<MarkerInfo> markerInfoList = propertyObject.getMarkerInfoList();
		
		IMarker[] markers = new IMarker[markerInfoList.size()];
		int i=0;
		for (MarkerInfo markerInfo : markerInfoList) {		
			IMarker newMarker;
			try {
				newMarker = resource.createMarker(markerInfo.getType());
				newMarker.setAttributes(markerInfo.getAttributes());
			} catch (CoreException e) {
				throw new RcException(e);				
			}
			markers[i]=newMarker; i++;				
		}
		return  markers;		
	}
	

	protected String getPropertyName(){
		return _configManager.getAspectJPropertyName();
	}
	
	@Override
	protected List<String> getMarkerTypes() {
		String type = _configManager.getAdviceMarkerGenericType();
		List<String> markerTypes = new ArrayList<String>(1);
		markerTypes.add(type);
		return markerTypes;
	}
}
