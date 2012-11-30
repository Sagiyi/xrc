package xrc.api.ide.eclipse.core.dto;

//was package org.eclipse.compare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
 
import org.eclipse.core.resources.IMarker;

/**
 *  A Serializable DTO to wrap and aggregate IMarker/s data for reading/writing 
 *  
 * TODO: medium-high priority: understand why I need both this and the RelationshipsInfo
 * 
 * @author Sagi Ifrah
 *
 */
public class MarkersInfo implements Serializable{
	
	private static final long serialVersionUID = 5852565083768571143L;
	
	List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();
	
	public MarkersInfo(){
		super();
	}
	
	public MarkersInfo(IMarker[] markers) throws CoreException{
		super();
		add(markers);
	}
	
	public List<MarkerInfo> getMarkerInfoList(){
		return markerInfoList;
	}
	
	public void add(IMarker[] markers) throws CoreException {
		for (IMarker marker : markers) {
			add(marker);
		}
	}
	
	public void add(IMarker marker) throws CoreException {								
			Map<String, Serializable> attributes = new HashMap<String, Serializable>();
			
			//attributes.putAll(marker.getAttributes());
			Map<String, Object> markerAttributes = marker.getAttributes();
			for (Map.Entry<String, Object> attribute : markerAttributes.entrySet()){
				String key = attribute.getKey();
				Object value = attribute.getValue();
				if (value instanceof Serializable) {//value's type is String  Integer, or Boolean
					Serializable serializableValue = (Serializable) value;
					attributes.put(key, serializableValue);
				} else {
					//TODO: throw exception?
				}
			}
			
			String type = marker.getType();
			markerInfoList.add(new MarkerInfo(type, attributes));		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((markerInfoList == null) ? 0 : markerInfoList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarkersInfo other = (MarkersInfo) obj;
		if (markerInfoList == null) {
			if (other.markerInfoList != null)
				return false;
		} else if (!markerInfoList.equals(other.markerInfoList))
			return false;
		return true;
	}
	
}
