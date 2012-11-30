package xrc.api.ide.eclipse.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.Relationship;

//import xrc.api.ide.eclipse.core.dto.additionalrelationshipinfo.AdditionalRelationshipInfo;

/**
 *  A Serializable DTO to wrap and aggregate IRelationship/s data for reading/writing
 *  
 * TODO: low-priority: consider Map<Integer, Set<IRelationship>> for _data.
 *		 medium-priority: set the serialVersionUID for all required classes in same generation way
 *  	 medium-priority: consider hash-code and equals, using comments from previous version
 *  	 medium-high-priority: consider using the additionalInfo to add data per Irelation, e.g. for parents declerations. other option is using the processor & formatter to do it directly on the target handle. 
 * @author Sagi Ifrah
 *
 */
public class XMD implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final boolean RUNTIME_TEST = false;
	
	public static final Integer NOT_FOUND = -1;
	
	private Map<Integer, List<IRelationship>> _data; 
	private int _size = 0;
//	private Map<IRelationship, AdditionalRelationshipInfo> _additionalInfo;
	
	public XMD(){
		super();
		_data = new HashMap<Integer, List<IRelationship>>();
//		_additionalInfo = new HashMap<IRelationship, AdditionalRelationshipInfo>();
	}
	
	public XMD(Map<Integer, List<IRelationship>> data) {
		super();
		this._data = data;
//		_additionalInfo = new HashMap<IRelationship, AdditionalRelationshipInfo>();
		initSize();
	}
	
	public XMD(XMD relationshipsInfo){
		this();		
		Map<Integer, List<IRelationship>> data = relationshipsInfo.getData();	
		for(Integer line:data.keySet()){
			List<IRelationship> relationships = data.get(line);
			List<IRelationship> cloneRelationships = new ArrayList<IRelationship>();
			for (IRelationship relationship : relationships) {
				IRelationship cloneRelationship = cloneRelationship(relationship);
				cloneRelationships.add(cloneRelationship);
			} 
			_data.put(line, cloneRelationships);		
		}		
		_size = relationshipsInfo.size();
//		TODO: if I use this additionalInfo...
//		_additionalInfo = relationshipsInfo.getAdditionalInfo().clone();
	}
	
	public Map<Integer, List<IRelationship>> getData() {
		return _data;
	}

	private void initSize() {
		Collection<List<IRelationship>> markersListsPerLine = _data.values();
		for (List<IRelationship> list : markersListsPerLine) {
			_size += list.size();
		}		
	}
	
	public int size(){
		if(_size==0){//if read before size was saved... TODO: delete this if, and support starting from version XXX
			initSize(); 
		}
		return _size;
	}

	/**
	 * find line for a relationship that is exactly the same (same targets as well)
	 * Note: help to mark difference on both sides when there was marker also before the change
	 * @param relationship
	 * @return
	 */
	public Integer findLineForExactMatch(IRelationship relationship){		
		for (Map.Entry<Integer, List<IRelationship>> entry : _data.entrySet()) {
			if(entry.getValue().contains(relationship)){
				return entry.getKey();
			}
		}
		return NOT_FOUND;		
	}
	
	/**
	 * find line for a relationship that is the same, 
	 * or line of a relationship that is the same except it has additional targets (except the given relationship's single target).
	 * Note: help to find pure delta
	 * @param relationship
	 * @return
	 */
	public Integer findLine(IRelationship relationship){
		Integer line = findLineForExactMatch(relationship);
		if(line != NOT_FOUND)
			return line;		
		return findLineForSingleTargetRelationship(relationship);		
	}

	private Integer findLineForSingleTargetRelationship(
			IRelationship relationship) {
		for (Map.Entry<Integer, List<IRelationship>> entry : _data.entrySet()) {
			List<IRelationship> relationships = entry.getValue();
			for (IRelationship currentRelationship : relationships) {
				boolean isKindEquals = relationship.getKind().equals(currentRelationship.getKind());
				boolean isNameEquals = relationship.getName().equals(currentRelationship.getName());
				boolean isSourceHandleEquals = relationship.getSourceHandle().equals(currentRelationship.getSourceHandle());
				if(isKindEquals && isNameEquals && isSourceHandleEquals){
					List<?> targets = currentRelationship.getTargets();
					if(targets.contains(relationship.getTargets().get(0))){
						return entry.getKey();
					}
				}
			}
		}
		return NOT_FOUND;
	}
	
	public Integer findLineForSourceHandle(String sourceHandle){
		for (Map.Entry<Integer, List<IRelationship>> entry : _data.entrySet()) {
			List<IRelationship> relationships = entry.getValue();
			for (IRelationship currentRelationship : relationships) {				
				boolean isSourceHandleEquals = sourceHandle.equals(currentRelationship.getSourceHandle());
				if(isSourceHandleEquals){					
					return entry.getKey();					
				}
			}
		}
		return NOT_FOUND;	
	}
	
	public void add(Integer line, IRelationship relationship){
		List<IRelationship> relationships = _data.get(line);
		if(relationships==null){
			relationships = new ArrayList<IRelationship>();
			_data.put(line, relationships);
		}
		relationships.add(relationship);	
		_size++;
	}
	
	/**
	 * Note: since IRelationship is not mine, I can't add there the clone
	 * 
	 * @param relationship
	 * @return
	 */
	public static IRelationship cloneRelationship(IRelationship relationship) {
		@SuppressWarnings("unchecked") //getTargets is beyond my reach...
		List<String> clonedTargets = relationship.getTargets();
		IRelationship cloneRelationship = new Relationship(
				relationship.getName(),
				relationship.getKind(), 
				relationship.getSourceHandle(),
				cloneTargets(clonedTargets),
				RUNTIME_TEST);
		return cloneRelationship;
	}

	/**
	 * Note: since IRelationship is not mine, I can't add there the clone's helper method.
	 * 
	 * @param targets
	 * @return
	 */
	public static List<String> cloneTargets(List<String> targets) {
		List<String> clonedtargets = new ArrayList<String>();
		clonedtargets.addAll(targets);		
		return clonedtargets;
	}

//	public AdditionalRelationshipInfo getAdditionalInfo() {
//		return _additionalInfo;
//	}		
}
