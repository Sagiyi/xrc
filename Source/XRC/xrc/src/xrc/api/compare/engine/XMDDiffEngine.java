package xrc.api.compare.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.Relationship;

import xrc.api.common.RcException;
import xrc.api.compare.IXMDDiffEngine;
import xrc.api.compare.IXMDDifference;
import xrc.api.ide.eclipse.core.dto.XMD;
import xrc.config.ConfigManager;



//TODO - medium-low priority: consider multithreading
/**
 * @see xrc.api.compare.IXMDDiffEngine
 * 
 * This implementaton is a (TODO: threadsafe) singleton
 * 
 * when creating RelationshipsInfo to represent a specific kind of difference (e.g. _beforeRemoved)
 * a pure delta is being created - only the different markers, e.g. if a relationship has several targets, and one was removed, only the removed is change, the other side is not in this delta.
 * @author Sagi Ifrah
 */
public class XMDDiffEngine implements IXMDDiffEngine { 
		
	private final static Logger _logger = Logger.getLogger(XMDDiffEngine.class);

	private static final boolean RUNTIME_TEST = false;
	private static final IXMDDiffEngine _instance = new XMDDiffEngine();
	
	//private singleton constructor
	private XMDDiffEngine(){
		super();
	} 
	
	//singleton accessor
	public static IXMDDiffEngine getInstance(){
		return _instance;
	}
	

	/* (non-Javadoc)
	 * @see xrc.api.compare.IXMDDiffEngine#diff(xrc.api.ide.eclipse.core.dto.XMD, xrc.api.ide.eclipse.core.dto.XMD)
	 */
	@Override
	public IXMDDifference diff(XMD beforeRelationshipsInfo, XMD afterRelationshipsInfo) throws RcException{
		//TODO: low priority - consider handling null, or delete following commented code
//		if(leftRelationshipsInfo == null && rightRelationshipsInfo == null){
//			return ???
//		}	
//		if(leftRelationshipsInfo == null && rightRelationshipsInfo != null){
//			return ???
//		}
//		if(leftRelationshipsInfo != null && rightRelationshipsInfo == null){
//			return ???
//		}
//		
//		//at this stage non is null	(else NullPointerException is excepted)	
		if(_logger.isTraceEnabled())
			_logger.trace("start diff");
		
			XMD beforeRemovedRelationshipsInfo = createDeltaRelationshipsInfo(beforeRelationshipsInfo, afterRelationshipsInfo);			
			XMD afterAddedRelationshipsInfo = createDeltaRelationshipsInfo(afterRelationshipsInfo, beforeRelationshipsInfo);
							
			//the relationships that have been changed on before side
			XMD beforeAddedToExistRelationshipsInfo =	createModifiedExist(
					beforeRelationshipsInfo,
					afterAddedRelationshipsInfo					
					);
			//the relationships that have been changed on after side
			XMD afterRemovedToExistRelationshipsInfo = createModifiedExist(
					afterRelationshipsInfo,
					beforeRemovedRelationshipsInfo					
					);
			//the relationships that are new on before side (where they don't exist yet)
			XMD beforeAddedToNonExistRelationshipsInfo = createModifiedNonExist(
					beforeRelationshipsInfo,
					afterAddedRelationshipsInfo,
					beforeAddedToExistRelationshipsInfo					
					);
			//the relationships that have been removed on after side (where they no longer exist)
			XMD afterRemovedToNonExistRelationshipsInfo = createModifiedNonExist(
					afterRelationshipsInfo,
					beforeRemovedRelationshipsInfo,
					afterRemovedToExistRelationshipsInfo				
					);
			
			XMDDifference relationshipsInfoDifference = new XMDDifference(
					beforeRelationshipsInfo,
					beforeRemovedRelationshipsInfo,
					beforeAddedToExistRelationshipsInfo,
					beforeAddedToNonExistRelationshipsInfo,
					
					afterRelationshipsInfo,
					afterAddedRelationshipsInfo,
					afterRemovedToExistRelationshipsInfo,
					afterRemovedToNonExistRelationshipsInfo					
					);		
			return relationshipsInfoDifference;
	}

	/**
	 * create and return a RelationshipsInfo with relationships-data that is in the given side, 
	 * and that is also contained in the other-side (hence it is the data that exists and is modified)
	 * 
	 * @param side 
	 * @param otherSideDelta
	 * @return 
	 * @throws RcException 
	 */
	private XMD createModifiedExist(XMD side,
			XMD otherSideDelta
			) throws RcException{	
		XMD sideModifiedExist = new XMD();
		if(otherSideDelta!=null){
			Map<Integer, List<IRelationship>>  otherSideDeltaData = otherSideDelta.getData();
			Set<IRelationship> otherSideDeltaRelations = flat(otherSideDeltaData);
			for (IRelationship otherSideDeltaRelationship : otherSideDeltaRelations) {	
				Integer line = null;
				if(side!=null){
					line = side.findLineForSourceHandle(otherSideDeltaRelationship.getSourceHandle());
				}
				if(side == null || line != XMD.NOT_FOUND){				
					sideModifiedExist.add(line, otherSideDeltaRelationship);
				}
			}
		}
		return sideModifiedExist;
	}
	
	/**
	 * create and return a RelationshipsInfo with relationships-data that is in the other-side, 
	 * but that is not contained in the given side (hence it is the data that not-exists and is modified)
	 * TODO: medium priority - is the second delta required? also the new is seems like redundant since calling to create do new...
	 * 
	 * @param side
	 * @param otherSideDelta
	 * @param sideModifiedExist
	 * @return
	 * @throws RcException
	 */
	private XMD createModifiedNonExist(XMD side,
			XMD otherSideDelta,
			XMD sideModifiedExist
			) throws RcException{
		XMD sideModifiedNonExist = new XMD();
		sideModifiedNonExist = createDeltaRelationshipsInfo(otherSideDelta, side);
		sideModifiedNonExist = createDeltaRelationshipsInfo(sideModifiedNonExist, sideModifiedExist);
		return sideModifiedNonExist;
	}
	
	/**
	 * create and return the delta (difference) of two RelationshipsInfo objects
	 * 
	 * @param minuendRelationshipsInfo
	 * @param subtrahendRelationshipsInfo
	 * @return
	 * @throws RcException
	 */
	private XMD createDeltaRelationshipsInfo(
			XMD minuendRelationshipsInfo,
			XMD subtrahendRelationshipsInfo) throws RcException {
		
		if(minuendRelationshipsInfo == null && subtrahendRelationshipsInfo == null){
			return null;			
		}
		if(minuendRelationshipsInfo == null && subtrahendRelationshipsInfo != null){			
			return null;
		}
		if(minuendRelationshipsInfo != null && subtrahendRelationshipsInfo == null){
			XMD deltaRelationshipsInfo = new XMD(minuendRelationshipsInfo);
			return deltaRelationshipsInfo;
		}
		
		//at this stage non is null
		Map<Integer, List<IRelationship>> minuendData = minuendRelationshipsInfo.getData();
		Map<Integer, List<IRelationship>> subtrahendData = subtrahendRelationshipsInfo.getData();
		
		Set<IRelationship> minuendRelationshipSet = flat(minuendData);//return new Map...
		Set<IRelationship> subtrahendRelationshipSet = flat(subtrahendData);
				
		minuendRelationshipSet.removeAll(subtrahendRelationshipSet);//expect not to work, since can't trust the equals.
		removeAll(minuendRelationshipSet, subtrahendRelationshipSet);
		
		XMD deltaRelationshipsInfo = inflate(minuendRelationshipsInfo, minuendRelationshipSet); 
		return deltaRelationshipsInfo;
	}			
	
	/**
	 * since I can't implement equals & hasCode for IRelationship, I created this removeAll utility, 
	 * that use the set removeAll method with my equals implementation 
	 * @param resultRelationshipSet
	 * @param toBeRemovedRelationshipSet
	 */
	private void removeAll(Set<IRelationship> resultRelationshipSet,
			Set<IRelationship> toBeRemovedRelationshipSet) {		
		Set<IRelationship> toBeRemoved = new HashSet<IRelationship>();
		for (IRelationship findRelationship : toBeRemovedRelationshipSet) {
			for (IRelationship foundRelationship : resultRelationshipSet) {
				//TODO: also null verifications...
				if( findRelationship.getKind().getName().equals(foundRelationship.getKind().getName())  &&
					findRelationship.getName().equals(foundRelationship.getName()) &&
					findRelationship.getSourceHandle().equals(foundRelationship.getSourceHandle()) &&
//					findRelationship.getTargets().equals(foundRelationship.getTargets())
					compareTargets(findRelationship.getTargets(), foundRelationship.getTargets())
					){
					if(_logger.isTraceEnabled())
						_logger.trace("removeAll: found: " + foundRelationship);					
					toBeRemoved.add(foundRelationship);
				}
			}
		}
		resultRelationshipSet.removeAll(toBeRemoved);
	}
	
	//TODO: more elegant with comparator, different calls for display and for change control (identifyAffectedFiles)
	private static boolean compareTargets(List<String> t, List<String> t2){
		boolean ignoreModifyFlagDifference = ConfigManager.getInstance().isDiffForChangeControlIgnoreModifyFlag(); //TODO: get from config, later from file. true;
		boolean ignoreVersions = ConfigManager.getInstance().isDiffForChangeControlIgnoreVersions();//TODO: get from config
		if(ignoreModifyFlagDifference){
			if(t.equals(t2))
				return true;
			if(t.size()!= t2.size())
				return false;
			for (int i = 0; i < t.size(); i++) {
				String target = t.get(i);
				String target2 = t2.get(i);
				
				target = target.replace("[*", "[");
				target2 = target2.replace("[*", "[");
								

				if(ignoreVersions){//TODO
					target = removeVersion(target);
					target2 = removeVersion(target2);
				}
				if(!target.equals(target2)){
					if(_logger.isDebugEnabled())
						_logger.debug("targets differs:\n"+target+"\n"+target2);				
					return false;				
				}
			}
			return true;
		}else{
			return t.equals(t2);
		}
	}
	
	//TODO: move somewhere else like the processor, and fix to more elegant
	private static String removeVersion(String target) {//assume no * for modified.
		int startVersionCandidate = target.indexOf("[")+1;		
		if(startVersionCandidate < 1)
			return target;
		int endVersionCandidate = target.indexOf("]",startVersionCandidate);
		if(endVersionCandidate < 0)
			return target;

		try{
			Integer.valueOf(target.charAt(startVersionCandidate));
		}catch(NumberFormatException e){
			startVersionCandidate = target.indexOf("[", startVersionCandidate)+1;	
			if(startVersionCandidate < 1)
				return target;
			endVersionCandidate = target.indexOf("]",startVersionCandidate);
			if(endVersionCandidate < 0)
				return target;
		}
		String verStr = target.substring(startVersionCandidate, endVersionCandidate);
		try{				
			Long.valueOf(verStr);
		}catch(NumberFormatException e){
			_logger.error("removeVersion: 2 '[' chars found and yet failed to parse target:\n"+target);
			return target;
		}
		return target.replace("[" + verStr + "]", "");		
	}
	
	public static void main(String[] args) {
//		=xrctestsuite/src<thesis.delivery.testsuite.markerscoverage{MyClass.java[MyClass~doBeforeFrom2aspects
		String target ="=xrctestsuite/src<thesis.delivery.testsuite.markerscoverage*MyAspect.aj}MyAspect[356]`declare parents!5";
//						=xrctestsuite/src<thesis.delivery.testsuite.markerscoverage*MyAspect.aj}MyAspect     `declare parents!5
		System.out.println(removeVersion(target));
	}
	/**
	 * Note: not is use
	 * flat the data to a set, regarding the keys (lines).
	 * since the target-lists are not flatten too as in flat(..), it is less useful, and not trustable...
	 * @param data
	 * @return
	 */
	public Set<IRelationship> simpleFlat(Map<Integer, List<IRelationship>> data){
		Collection<List<IRelationship>> values = data.values();		
		Set<IRelationship> relationshipSet = new HashSet<IRelationship>();
		for (List<IRelationship> list : values) {
			relationshipSet.addAll(list);
		}
		return relationshipSet;
	}
	
	/**
	 * gets a Map<Integer, List<IRelationship>> data and flat it to Set<IRelationship>.
	 * where each IRelationship contains a single-target-list, 
	 * so working with the return value promise a single target in each IRelationship.
	 * also the line key is ignored in the set, hence enable to work on sets of similar IRelationship regardless of lines
	 * (e.g. identify data that is same except lines etc.)
	 * @param data
	 * @return
	 */
	public static Set<IRelationship> flat(Map<Integer, List<IRelationship>> data){
		Collection<List<IRelationship>> values = data.values();		
		Set<IRelationship> relationshipSet = new HashSet<IRelationship>();
		for (List<IRelationship> list : values) {
			for (IRelationship relationship : list) {				
				List<String> targets = relationship.getTargets();
				if(targets.size()>1){
					for (String target : targets) {
						List<String> singleTargetList = new ArrayList<String>();
						singleTargetList.add(target);
						IRelationship singleTargetRelationship = 
//							new Relationship(relationship.getName(), relationship.getKind(), relationship.getSourceHandle(),singleTargetList, RUNTIME_TEST);
								new Relationship(relationship.getName(), relationship.getKind(), relationship.getSourceHandle(),singleTargetList, RUNTIME_TEST);
						relationshipSet.add(singleTargetRelationship);
					}
				}else{
					relationshipSet.add(relationship);	
				}
			}			
		}
		return relationshipSet;
	}
	//org - worked with AJDT, not with the very new AJDT that Eclipse 3.7 use.
//	public Set<IRelationship> flat(Map<Integer, List<IRelationship>> data){
//	Collection<List<IRelationship>> values = data.values();		
//	Set<IRelationship> relationshipSet = new HashSet<IRelationship>();
//	for (List<IRelationship> list : values) {
//		for (IRelationship relationship : list) {				
//			List<?> targets = relationship.getTargets();
//			if(targets.size()>1){
//				for (Object target : targets) {
//					List<Object> singleTargetList = new ArrayList<Object>();
//					singleTargetList.add(target);
//					IRelationship singleTargetRelationship = 
//						new Relationship(relationship.getName(), relationship.getKind(), relationship.getSourceHandle(),singleTargetList, RUNTIME_TEST);
//					relationshipSet.add(singleTargetRelationship);
//				}
//			}else{
//				relationshipSet.add(relationship);	
//			}
//		}			
//	}
//	return relationshipSet;
//}
	
	/**
	 * 	 * Note: I didn't found better opposite verb of flat than inflate...
	 * 
	 * This method does the opposite of flat.
	 * In order to have the lines, it needs the relationshipsInfo.
	 * Base on the given RelationshipsInfo, and Set<IRelationship>, it creates and return new RelationshipsInfo
	 * Generally - for each IRelationship in the set, find it in the RelationshipsInfo, than add it to RelationshipsInfo
	 * 
	 * @param relationshipsInfo
	 * @param relationshipSet
	 * @return
	 * @throws RcException
	 */
	private XMD inflate(
			XMD relationshipsInfo,
			Set<IRelationship> relationshipSet) throws RcException {
		Map<Integer, List<IRelationship>> data = new HashMap<Integer, List<IRelationship>>();
		for (IRelationship relationship : relationshipSet) {
			Integer line = relationshipsInfo.findLine(relationship);
			if(line.equals(-1)){
				throw new RcException("Error in code - find fails and should have succeeded");
			}
			List<IRelationship> lineRelationships = data.get(line);
			if(lineRelationships == null){
				lineRelationships = new ArrayList<IRelationship>();
				data.put(line, lineRelationships);
			}
			lineRelationships.add(relationship);
		}
		XMD inflateRelationshipsInfo = new XMD(data);
		return inflateRelationshipsInfo;
	}
	
	/**
	 * Note: currently not in use...
	 * A intersect B = A-(A-B)
	 * @param sourceRelationshipsInfo
	 * @param destinationRelationshipsInfo
	 * @return new RelationshipInfo with the intersection of the markers, but with the lines from the source. 
	 * @throws RcException 
	 */
	private XMD createIntersectionRelationshipsInfo(
			XMD sourceRelationshipsInfo,
			XMD destinationRelationshipsInfo) throws RcException {				
		XMD onlySourceRelationshipsInfo = createDeltaRelationshipsInfo(sourceRelationshipsInfo, destinationRelationshipsInfo);		
		XMD intersectionRelationshipsInfo = createDeltaRelationshipsInfo(sourceRelationshipsInfo, onlySourceRelationshipsInfo);
		return intersectionRelationshipsInfo;
	}	
	
	//TODO: low priority - see if this old method  might be helpful if we want different markers for each side - probably this is will be deleted.
//	private Map<Integer, GenericDiffType> createBeforeTypeMarkers(RelationshipsInfoDifference relationshipsInfoDifference){
////		markerTypePerLine	
//		Map<Integer, GenericDiffType> beforeTypeMarkers = new HashMap<Integer, GenericDiffType>();
//				
//		Set<Integer> beforeRemovedLines = new HashSet<Integer>();
//		beforeRemovedLines.addAll(relationshipsInfoDifference.getBeforeRemoved().getData().keySet());
//		beforeRemovedLines.addAll(relationshipsInfoDifference.getBeforeTotallyRemoved().getData().keySet());
//
//		Set<Integer> beforeAddedLines = new HashSet<Integer>();
//		beforeAddedLines.addAll(relationshipsInfoDifference.getBeforeAddedToExist().getData().keySet());
//		beforeAddedLines.addAll(relationshipsInfoDifference.getBeforeAddedToNonExist().getData().keySet());
//						
//		Set<Integer> beforeMarkeredLines = new HashSet<Integer>();
//		beforeMarkeredLines.addAll(beforeRemovedLines);
//		beforeMarkeredLines.addAll(beforeAddedLines);
//
//		for (Integer beforeMarkeredLine : beforeMarkeredLines) {
//			if(beforeRemovedLines.contains(beforeMarkeredLine)){
//				if(beforeAddedLines.contains(beforeMarkeredLine)){
//					beforeTypeMarkers.put(beforeMarkeredLine, GenericDiffType.DIFFERENT);
//				}else{
//					beforeTypeMarkers.put(beforeMarkeredLine, GenericDiffType.REMOVE);
//				}
//			}else{
//				beforeTypeMarkers.put(beforeMarkeredLine, GenericDiffType.ADD);
//			}
//		}
//		return beforeTypeMarkers;
//	}
//	
//	
	//TODO: low priority - see if this old method  might be helpful if we want different markers for each side - probably this is will be deleted. 
//	private Map<Integer, GenericDiffType> createAfterTypeMarkers(RelationshipsInfoDifference relationshipsInfoDifference){
////		markerTypePerLine		
//		Map<Integer, GenericDiffType> afterTypeMarkers = new HashMap<Integer, GenericDiffType>();
//					
//		Set<Integer> afterRemovedLines = new HashSet<Integer>();
//		afterRemovedLines.addAll(relationshipsInfoDifference.getAfterRemovedToExist().getData().keySet());
//		afterRemovedLines.addAll(relationshipsInfoDifference.getAfterRemovedToNonExist().getData().keySet());
//
//		Set<Integer> afterAddedLines = new HashSet<Integer>();
//		afterAddedLines.addAll(relationshipsInfoDifference.getAfterAdded().getData().keySet());
//		afterAddedLines.addAll(relationshipsInfoDifference.getAfterTotallyAdded().getData().keySet());
//						
//		Set<Integer> afterMarkeredLines = new HashSet<Integer>();
//		afterMarkeredLines.addAll(afterRemovedLines);
//		afterMarkeredLines.addAll(afterAddedLines);
//		
//		for (Integer afterMarkeredLine : afterMarkeredLines) {
//			if(afterRemovedLines.contains(afterMarkeredLine)){
//				if(afterAddedLines.contains(afterMarkeredLine)){
//					afterTypeMarkers.put(afterMarkeredLine, GenericDiffType.DIFFERENT);
//				}else{
//					afterTypeMarkers.put(afterMarkeredLine, GenericDiffType.REMOVE);
//				}
//			}else{
//				afterTypeMarkers.put(afterMarkeredLine, GenericDiffType.ADD);
//			}
//		}
//		return afterTypeMarkers;		
//	}
}
