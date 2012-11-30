package xrc.api.ide.eclipse.core.dto.additionalrelationshipinfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdditionalRelationshipInfo implements Serializable{//implements AdditionalRelationshipInfo that extends serializable
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, Set<IAdditionalInfo>> _additionalTargetsInfo;//for each target enable additional info. the key is target-handle from the RelationshipInfo
	private Set<IAdditionalInfo> _additionalRelationshipInfo;
	
	public AdditionalRelationshipInfo clone() {
		AdditionalRelationshipInfo clone = new AdditionalRelationshipInfo();
		for(IAdditionalInfo additionalInfo : _additionalRelationshipInfo){
			clone.addAdditionalInfo(additionalInfo.clone());
		} 
		
		Map<String, Set<IAdditionalInfo>> orgAdditionalTargetsInfo = this.getAdditionalTargetsInfo();
		for (Map.Entry<String, Set<IAdditionalInfo>> entry : orgAdditionalTargetsInfo.entrySet()) {
			for (IAdditionalInfo ai : entry.getValue()){
				clone.addTargetAdditionalInfo(entry.getKey(), ai);
			}
		}	
		
		return clone;
	}

	public AdditionalRelationshipInfo() {
		super();
		_additionalTargetsInfo = new HashMap<String, Set<IAdditionalInfo>>();
		_additionalRelationshipInfo = new HashSet<IAdditionalInfo>();
	}

	public Set<IAdditionalInfo> find(String targetHandle){
		return _additionalTargetsInfo.get(targetHandle);
	}
	
	public void addTargetAdditionalInfo(String targetHandle, IAdditionalInfo additionalInfo){
		Set<IAdditionalInfo> targetAdditionalInfo = find(targetHandle);
		if(targetAdditionalInfo == null)
		{
			targetAdditionalInfo = new HashSet<IAdditionalInfo>();
		}
		targetAdditionalInfo.add(additionalInfo);		
	}
	
	public Set<IAdditionalInfo> removeTargetAdditionalInfo(String targetHandle){		
		return _additionalTargetsInfo.remove(targetHandle);
	}
	
	public boolean removeTargetAdditionalInfo(String targetHandle, IAdditionalInfo additionalInfo){		
		Set<IAdditionalInfo> targetAdditionalInfo = find(targetHandle);
		return targetAdditionalInfo.remove(additionalInfo);

	}
	
	public void addAdditionalInfo(IAdditionalInfo additionalInfo){
		_additionalRelationshipInfo.add(additionalInfo);
	}
	
	public boolean removeAdditionalInfo(IAdditionalInfo additionalInfo){
		return _additionalRelationshipInfo.remove(additionalInfo);
	}

	public Map<String, Set<IAdditionalInfo>> getAdditionalTargetsInfo() {
		return _additionalTargetsInfo;
	}

	public Set<IAdditionalInfo> getAdditionalRelationshipInfo() {
		return _additionalRelationshipInfo;
	}	
}
