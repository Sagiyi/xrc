package xrc.api.ide.eclipse.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.ajdt.core.builder.AJBuilder;
import org.eclipse.ajdt.core.builder.IAJBuildListener;
import org.eclipse.core.resources.IResource;

public class AdviceListenerManager {
	
	private final static Logger _logger = Logger.getLogger(AdviceListenerManager.class);

	private static AdviceListenerManager _instance = new AdviceListenerManager();
	private Map<IResource, IAJBuildListener> _adviceChangedListeners = new HashMap<IResource, IAJBuildListener>();
	
	private Set<IAJBuildListener> _garbage = new HashSet<IAJBuildListener>();
	
	private AdviceListenerManager(){
		//TODO: read _adviceChangedListeners from a file (need to make it serializable...).
		clean();
	}
	
	public static AdviceListenerManager getInstance(){
		return _instance;
	}
	 
	public void addListener(IResource resource){
		IAJBuildListener listener = new AJBuildListener(resource);		
		AJBuilder.addAJBuildListener(listener);
		_adviceChangedListeners.put(resource, listener);
		//TODO: write _adviceChangedListeners to a file...
	}	
	
	public void removeListener(IResource resource){
		IAJBuildListener listener = _adviceChangedListeners.get(resource);		
//		AJBuilder.removeAJBuildListener(listener);
		_adviceChangedListeners.remove(resource);
		_garbage.add(listener);		
	}
	
	public boolean hasListener(IResource resource) {		
		return _adviceChangedListeners.containsKey(resource);
	}
	
	//TODO: occasionally remove the leftovers - listeners that should be remove from the AJBuilder since they are not in the map.
	private void clean(){
		Thread thread = new Thread(){			
			public void run(){
				while(true){
					try{
						Set<IAJBuildListener> approveToRemove = new HashSet<IAJBuildListener>();
						for(IAJBuildListener listener : _garbage){
							AJBuilder.removeAJBuildListener(listener);
							approveToRemove.add(listener);							
						}
						_garbage.removeAll(approveToRemove);
					}catch (Exception e) {
						_logger.error("cought an exception while cleaning:" + e.getMessage(),e);						
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}
	
	
//	//TODO: this should replace the use of AJBuildListener, and be more efficient.
//	private Map<String, IAdviceChangedListener> adviceChangedListeners;	
//	
//	public void addListener(IResource resource){
//		IAdviceChangedListener listener = new AJBuildListener(resource);
//		AJBuilder.addAdviceListener(listener);
//	}	
//	
//	public void removeListener(IResource resource){
//		IAdviceChangedListener listener = adviceChangedListeners.get("");
//		AJBuilder.removeAdviceListener(listener);
//	}
	
}
