package xrc.api.ide.eclipse.core.io;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import xrc.api.common.RcException;
import xrc.api.ide.IMarkersWriter;
import xrc.api.ide.eclipse.core.dto.MarkersInfo;
import xrc.api.rc.IRcPropertyWriter;
import xrc.config.ConfigManager;


/**
 * History: 
 * MarkersReader came from MarkerStreamer re-factoring, 
 */
public class MarkersInfoWriter implements IMarkersWriter {
		
	private IRcPropertyWriter<MarkersInfo> _propertyWriter;
	private ConfigManager _configManager = ConfigManager.getInstance();
	
	public MarkersInfoWriter(IRcPropertyWriter<MarkersInfo> propertyWriter) {
		super();
		this._propertyWriter = propertyWriter;
	}
	
	/* (non-Javadoc)
	 * @see xrc.eclipse.core.IMarkerWriter#write(org.eclipse.core.resources.IResource)
	 */
	@Override
	public boolean write(IResource resource) throws RcException{		//W1
		MarkersInfo myResourceInfo = new MarkersInfo();
		try {
			myResourceInfo = produceMarkersInfo(resource);		
//			test(resource);
////		writeToFile(resource, myResourceInfo);
			_propertyWriter.write(_configManager.getAspectJPropertyName(), myResourceInfo);		
		} catch (CoreException e) {
			throw new RcException(e);				
		}
		return true;
	}
	
	public MarkersInfo produceMarkersInfo(IResource resource) throws CoreException{
		MarkersInfo markersInfo = new MarkersInfo();					
		for (String adviceMarkerType : _configManager.getAdviceMarkerTypes()) {
			IMarker[] adviceMarkers = resource.findMarkers(adviceMarkerType, true, IResource.DEPTH_INFINITE);
			if(adviceMarkers != null && adviceMarkers.length > 0){
				markersInfo.add(adviceMarkers);
			}
		}		
		return markersInfo;
	}
	
	
	
	
	
	//Test 
//	
//	
//	public void test(IResource resource){
//		AspectJMarkersComparator a = new AspectJMarkersComparator();
//		try {
//			a.preProcessing(resource);
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	
	
	//TODO: this section (writeToFile) is probably obsolete. it should move to be other kind of (optional) propertyWriter.
	
	private OutputStream getOutoutStream(IResource resource) throws FileNotFoundException{//W2
		return new FileOutputStream( "I:/tempMarkers/" + resource.getName() + ".markers" );  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	private void writeToFile(IResource resource, MarkersInfo myResourceInfo) throws IOException, CoreException{
//		 DataOutputStream output = new DataOutputStream(new FileOutputStream(file));		
		OutputStream file = getOutoutStream(resource);//new FileOutputStream( "I:/temp/" + resource.getName() +"" );
		OutputStream buffer = new BufferedOutputStream( file );
		ObjectOutput output = new ObjectOutputStream( buffer );
//		IMarker[] markers = resource.findMarkers(type, true, IResource.DEPTH_INFINITE);
//		MarkersInfo myResourceInfo = new MarkersInfo(markers);
		output.writeObject(myResourceInfo); 
//		for (IMarker marker : markers) {		
//			Map<?, ?> attributes = marker.getAttributes();
//			for (Object key : attributes.keySet()) {
//				output.writeObject(key);
//				output.writeObject(attributes.get(key));
//			}			
//		}	
		output.close();
	}

}
