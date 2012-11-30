package xrc.api.ide.eclipse.plugin.compare.ui.ajdt;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.asm.IRelationship;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import xrc.api.common.RcException;
import xrc.api.compare.IXMDDifference;
import xrc.api.compare.engine.XMDDiffEngine;
import xrc.api.ide.eclipse.core.dto.MarkersInfo;
import xrc.api.ide.eclipse.core.dto.XMD;
import xrc.api.ide.eclipse.core.io.MarkerMessageFormatter;
import xrc.api.rc.IRcFactory;
import xrc.api.rc.IRepositoryResource;
import xrc.api.rc.svn.PropertyReader;
import xrc.config.ConfigManager;


/**
 * TODO: low-priority: consider extending JavaStructureDiffViewer, 
 * 		 this will require also class AspectJStructureDiffViewerCreator that is very similar to JavaStructureDiffViewerCreator.
 * 		 ??? what is the profit / difference in behavior? - if doing so - handle constructor, override methods & run super (e.g. postDiffHook) etc...
 * 		 low-priority: member _input decide if should be Object or DiffNode
 * 		 medium priority - verify clean of diff-markers (in postDiffHook(..)), actually what is the meaning if clean will not happen? what kinds should be cleaned?
 * 		 low-priority - verify postDiffHook - is it redundant? probably depends on above inheritance idea.
 * 		 low-priority - I moved code to temp after // from version 203 - verify if assignMarkers and chacheReader are use-able.
 * 		 medium priority - clean as possible the types that need name-space prefixes.
 * 		 high priority - createDiffMarkers(..) - doc & explain cases + test correctness
 * 		 medium-low priority - createDiffMarkers(..) - consider using return value +/ add it to the annotationModel somehow. (what is the benefit?)
 * 		 medium-low priority - consider refining isModified(..), also MarkerMessageFormatter might be singleton 
 * @author Sagi Ifrah
 *
 */
public class AspectJStructureDiffViewer{

	private final static Logger _logger = Logger.getLogger(AspectJStructureDiffViewer.class);

	private static ConfigManager _configManager = ConfigManager.getInstance();
	private Object _input;
	public AspectJStructureDiffViewer(Object input){
		_input = input;
	}
	
	/**
	 * Note: I think it's currently not in use. It's for case we need the diff to be hooked. 
	 * @param differencer
	 * @param root
	 * @param monitor
	 */
	protected void postDiffHook(Differencer differencer, IDiffContainer root,
			IProgressMonitor monitor) {		
		diff();		
	}		
	
	/**
	 * do the preparations, run the diff engine, delete the markers and create new ones. 
	 */
	public void diff(){
		//preparations
		DiffNode input = (DiffNode)getInput();
		ITypedElement left = input.getLeft();
		ITypedElement right = input.getRight();
		ITypedElement before;
		ITypedElement after; 
		if(compareVersions(left,right)>=0){
			before = right;
			after = left;				
		} else {
			before = right;
			after = left;			
		}		
		try { 
			XMD beforeRelationshipsInfo = extractRelationshipsInfo(before);
			XMD afterRelationshipsInfo = extractRelationshipsInfo(after);
			
			//run diff engine
			IXMDDifference relationshipsInfoDifference =
				XMDDiffEngine.getInstance().diff(beforeRelationshipsInfo, afterRelationshipsInfo);
			
			//delete markers
			deleteMarkers(before, after);
			
			//create new markers
			mark(relationshipsInfoDifference, before, after);
									
		} catch (RcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteMarkers(ITypedElement before, ITypedElement after) throws RcException, CoreException{
		IResource leftResource = getResource(getRepositoryResource(before));
		IResource rightResource = getResource(getRepositoryResource(after));
		String[] diffMarkerTypeRoots = _configManager.getDiffMarkerTypeRoots();
		for (String markerType : diffMarkerTypeRoots) {		
			leftResource.deleteMarkers(markerType, true, IResource.DEPTH_ZERO);		
			rightResource.deleteMarkers(markerType, true, IResource.DEPTH_ZERO);
		}
	}
	
	private void mark(IXMDDifference relationshipsInfoDifference, ITypedElement before, ITypedElement after) throws RcException{
		
		mark(relationshipsInfoDifference.getBefore(), before, DiffType.BeforeModified);
		mark(relationshipsInfoDifference.getAfter(), after, DiffType.AfterModified);

		mark(relationshipsInfoDifference.getBeforeRemoved(), before, DiffType.BeforeRemoved);//markRemoved
		mark(relationshipsInfoDifference.getAfterAdded(), after, DiffType.AfterAdded);//markAdd

		mark(relationshipsInfoDifference.getBeforeAddedToExist(), before, DiffType.BeforeAddedToExist);
		mark(relationshipsInfoDifference.getAfterRemovedToExist(), after, DiffType.AfterRemovedToExist);

		mark(relationshipsInfoDifference.getBeforeAddedToNonExist(), before, DiffType.BeforeAddedToNonExist);
		mark(relationshipsInfoDifference.getAfterRemovedToNonExist(), after, DiffType.AfterRemovedToNonExist);

	}		
	
	/**
	 * check which is before and which is after, according to versionId
	 * @param left
	 * @param right
	 * @return
	 */
	private int compareVersions(ITypedElement left, ITypedElement right) {
		try {
			IRepositoryResource leftRepositoryResource = getRepositoryResource(left);
			IRepositoryResource rightRepositoryResource = getRepositoryResource(right);
			Comparable leftRevision = (Comparable) leftRepositoryResource.getRevisionId();
			Comparable rightRevision = (Comparable) rightRepositoryResource.getRevisionId();
			return leftRevision.compareTo(rightRevision);
		} catch (RcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return 0;
	}

	private void mark(XMD changedRelationshipsInfo,
			ITypedElement side, DiffType diffType) throws RcException {
		IRepositoryResource repositoryResource = getRepositoryResource(side);
		if(repositoryResource!=null)
			createDiffMarkers(changedRelationshipsInfo, repositoryResource, diffType);
	}
	
	private XMD extractRelationshipsInfo(ITypedElement iTypedElement) throws RcException{
		if (iTypedElement instanceof org.eclipse.team.svn.ui.compare.ResourceCompareInput.ResourceElement) {
			org.eclipse.team.svn.ui.compare.ResourceCompareInput.ResourceElement resourceElement = (org.eclipse.team.svn.ui.compare.ResourceCompareInput.ResourceElement) iTypedElement;
			org.eclipse.team.svn.core.resource.IRepositoryResource svnRepositoryResource = resourceElement.getRepositoryResource();
			IRcFactory rcFactory = _configManager.createRcFactory();
			IRepositoryResource repositoryResource = rcFactory.createRepositoryResource(svnRepositoryResource);
			//worked with: new RepositoryResource(svnRepositoryResource);
			PropertyReader<XMD> propertyReader = rcFactory.createPropertyReader(repositoryResource, XMD.class);
			XMD relationshipsInfo = propertyReader.read(
				_configManager.getRcPropertyNameRelationshipsInfo());//"XRC_RELATIONSHIPS", older was "AARC_RELATIONSHIPS")
			return ((xrc.api.ide.eclipse.core.dto.XMD)relationshipsInfo);
		}
		return null;//TODO: throw? or trust the cast exception?
	}
	
	private static IRepositoryResource getRepositoryResource(
			ITypedElement side) throws RcException {
		if (side instanceof org.eclipse.team.svn.ui.compare.ResourceCompareInput.ResourceElement) {
			org.eclipse.team.svn.ui.compare.ResourceCompareInput.ResourceElement resourceElement = (org.eclipse.team.svn.ui.compare.ResourceCompareInput.ResourceElement) side;
			org.eclipse.team.svn.core.resource.IRepositoryResource svnRepositoryResource = resourceElement.getRepositoryResource();
			ConfigManager configManager = ConfigManager.getInstance();//TODO: consider moving this and the rcFactory to the class level.
			IRcFactory rcFactory = configManager.createRcFactory();
			IRepositoryResource repositoryResource = rcFactory.createRepositoryResource(svnRepositoryResource);
				//worked with: new RepositoryResource(svnRepositoryResource);			
			return repositoryResource;
		}
		return null;
	}
	
	private static IResource getResource(IRepositoryResource repositoryResource) throws RcException{
		Object revisionId = repositoryResource.getRevisionId();		
		String name = repositoryResource.getName();		
		String shortName = name.substring(0, name.lastIndexOf("."));
		String tempResourcePath = "/tempProjectForRevisionDecorateions/"+shortName+"_rev_"+revisionId+"diff"+".temp";
		IFile resource = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(
				tempResourcePath));
		
		if(!resource.exists()){		
			if(revisionId instanceof Long && (Long)revisionId !=-1){
				_logger.warn("getResource: check why reach this point (probably a bug)");
			}
			//TODO: instead of (or in addition to) below try, should write the relationship info to the -1 file, for display compare with current version.
			try {				
				String fullUrlString = repositoryResource.getUrl();
				String repUrlString = repositoryResource.getLocalPath();
				String pathString = fullUrlString.substring(repUrlString.length());
				
				IFile ll = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
				ll = findEclipseResource(repositoryResource);//need to be merged with the same method in CompilationUnit..4XRC, now it's different IRepositoryResource argument in each
				
				
				resource.create(ll.getContents(), IResource.FORCE | IResource.DERIVED | IResource.TEAM_PRIVATE | IResource.HIDDEN , null);
			} catch (CoreException e) {				
				e.printStackTrace();
				throw new RcException(e);
			}
		}
		
		return resource;
	}
	
	//New!!!Sagi - to handle cases where the project is not in the root of the SVN
		//need to be merged with the same method in CompilationUnit..4XRC, now it's different IRepositoryResource argument in each
			public static IFile findEclipseResource(IRepositoryResource repResource) throws RcException{
//				String name = repResource.getName();
				String fullUrlString = repResource.getUrl();
				String repUrlString = repResource.getLocalPath();
				String pathString = fullUrlString.substring(repUrlString.length());
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
				if(file.exists()){
					return file;
				}
				while (pathString.contains("\\") || pathString.contains("/")){ //both seperators can be use by SVN.
					int sepIndex = Math.max(pathString.indexOf("\\"), pathString.indexOf("/"));
					pathString = pathString.substring(sepIndex+1);
					_logger.warn("findEclipseResource: pathString=" + pathString);
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
					if(file.exists()){
						return file;
					}
				} //TODO: improve. not efficient but rare to enter to this loop.
				_logger.error("resource not found");
				return null;
			}
			
	private static MarkersInfo createDiffMarkers(XMD relationshipsInfo, IRepositoryResource repositoryResource, DiffType diffType) throws RcException{
		String diffMarkerType = diffType.getMarkerType();
		IResource resource = getResource(repositoryResource);
		MarkersInfo diffMarkersInfo = new MarkersInfo();
		try {
			if(relationshipsInfo!=null){//TODO: test this null test is fine.
				Map<Integer, List<IRelationship>> data = relationshipsInfo.getData();
				for (Integer line : data.keySet()) {

					if(!DiffType.BeforeModified.equals(diffType) && !DiffType.AfterModified.equals(diffType) ){
						IMarker marker = resource.createMarker(diffMarkerType);
						marker.setAttribute(IMarker.LINE_NUMBER, line);
						marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);					
						marker.setAttribute(IMarker.MESSAGE, "diff: " + createMessage(data.get(line), diffType));//TODO: pass ADDed/REMOVEd...

						IMarker markerBkgrnd = resource.createMarker("xrc.changedAdviceBackground");
						markerBkgrnd.setAttribute(IMarker.LINE_NUMBER, line);
						markerBkgrnd.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
						//					markerBkgrnd.setAttribute(IMarker.MESSAGE, "TestMarker bkgrnd");
						diffMarkersInfo.add(marker );
					} else{
						if(isModified(data.get(line))){
							IMarker marker = resource.createMarker(diffMarkerType);
							marker.setAttribute(IMarker.LINE_NUMBER, line);
							marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
							diffMarkersInfo.add(marker );
						}
					}
				}
			}
		} catch (CoreException e) {
			throw new RcException(e);
		}			
		return diffMarkersInfo;
	}	
	
	private static String createMessage(List<IRelationship> list, DiffType diffType) {		
		StringBuilder message = new StringBuilder();
		MarkerMessageFormatter formatter = new MarkerMessageFormatter();		
		for (IRelationship relationship : list) {
			message.append(formatter.getMarkerMessage(relationship, diffType));
		}
		return message.toString();
	}
	
	private static boolean isModified(List<IRelationship> list) {				
		for (IRelationship relationship : list) {
			if(MarkerMessageFormatter.isModified(relationship)){
				return true;
			}
		}
		return false;
	}
		
	private Object getInput() {		
		return _input;
	}
}
