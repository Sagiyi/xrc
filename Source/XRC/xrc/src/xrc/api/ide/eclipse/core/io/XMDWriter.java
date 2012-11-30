package xrc.api.ide.eclipse.core.io;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.aspectj.asm.HierarchyWalker;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.HandleProviderDelimiter;
import org.eclipse.ajdt.core.model.AJProjectModelFacade;
import org.eclipse.ajdt.core.model.AJProjectModelFactory;
import org.eclipse.ajdt.core.model.AJRelationshipManager;
import org.eclipse.ajdt.core.model.AJRelationshipType;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;

import xrc.api.common.RcException;
import xrc.api.compare.IXMDDiffEngine;
import xrc.api.compare.IXMDDifference;
import xrc.api.compare.engine.XMDDiffEngine;
import xrc.api.ide.eclipse.core.dto.XMD;
import xrc.api.ide.eclipse.core.dto.additionalrelationshipinfo.ParentDeclarationInfo;
import xrc.api.rc.IFileInfo;
import xrc.api.rc.IFileInfoReader;
import xrc.api.rc.IRcFactory;
import xrc.api.rc.IRcPropertyReader;
import xrc.api.rc.IRcPropertyWriter;
import xrc.api.rc.IRepositoryResource;
import xrc.api.rc.RepositoryResource;
import xrc.config.ConfigManager;


/**
 * TODO: medium-low priority - consider IRcPropertyWriter<RelationshipsInfo> instead of IRcPropertyWriter<Serializable>
 * 		 medium-high - don't forget to remove the listener after using it... (if using add/remove listener for managing dirty files)
 * 		 second option is to have a single listener that will manage it's resources (dirty...)
 *  	 Set<IResources> resources;	add, remove.
 *  	 medium - modifiers for rcFactory - static? final?...
 *  	 medium - modifiers for findRelationships(..) static? 
 *  	 medium-high - should this class be singleton? 
 *  	 high - write(..) used constant "AARC_RELATIONSHIPS" - remove this line after verifying it works
 * @author Sagi Ifrah
 *
 */
public class XMDWriter {
	
	private final static Logger _logger = Logger.getLogger(XMDWriter.class);	
	private static ConfigManager _configManager = ConfigManager.getInstance();
	
	private IResource _resource;
	private IRcFactory _rcFactory = _configManager.createRcFactory();
	
	public XMDWriter(IResource resource) {
		super();
		this._resource = resource;
	}

	public void writeAll() throws RcException{
		Set<String> effectedJavaFilePaths = findAffectedFiles(_resource);		
		
		if(_logger.isDebugEnabled())
			_logger.debug("writeAll: effectedJavaFilePaths: " + effectedJavaFilePaths);
		
		for (String path : effectedJavaFilePaths) {			
			IResource javaResource = RelationshipParser.findFilename(path);
			XMDWriter relationshipsInfoWriter = new XMDWriter(javaResource);		
			relationshipsInfoWriter.write(_resource);
		}
//		write4Aspect();//TODO: SAGI: verify. it's new
		write(_resource);//trial instead of write4aspects
	}
	
	private Set<String> findAffectedFiles(IResource resource) throws RcException{
		Set<IRelationship> diffRelationships = compareRelationshipsWithPrevious();
		Set<String> effectedJavaFilePaths = new HashSet<String>();
//		AJRelationshipType[] types = new AJRelationshipType[]{
//				AJRelationshipManager.ADVISES, AJRelationshipManager.DECLARED_ON, 
////				AJRelationshipManager.ANNOTATES, AJRelationshipManager.SOFTENS//TODO: what other kinds???
//				};
//		Map<Integer, List<IRelationship>> relationships = findRelationships(resource, types);		
//		for (List<IRelationship> relationshipList : relationships.values()) {		
//			for (IRelationship relationship : relationshipList) {
		
		for (IRelationship relationship : diffRelationships) {			
				@SuppressWarnings("unchecked")
				List<String> targets = relationship.getTargets();
				for (String targetHandle : targets) {					
					String pathStr = RelationshipParser.findPath(targetHandle);
					effectedJavaFilePaths.add(pathStr);
				}
			}
//		}
		return effectedJavaFilePaths;
	}
	
	private Set<IRelationship> compareRelationshipsWithPrevious() throws RcException {
		//read from current, after modifying, and save or do (but this is without modifying... )
		Map<Integer, List<IRelationship>> curRelationships = findRelationships(_resource);
		Map<Integer, List<IRelationship>> prevRelationships = readPrevRelationships(_resource);
		return compareXMDofAspect(curRelationships, prevRelationships);
	}

	//TODO: move to diffEngine
	private Set<IRelationship> compareXMDofAspect(
			Map<Integer, List<IRelationship>> curRelationships,
			Map<Integer, List<IRelationship>> prevRelationships) throws RcException {
		
		//handle first revision: TODO: is it right?
		if(prevRelationships == null){
			Set<IRelationship> a = XMDDiffEngine.flat(new XMD(curRelationships).getData());
			return a;
		}
		
		// TODO Auto-generated method stub
		//compare the IRelationship sets, ignoring the keys.
		//I think we want to ignore also the class revision.
		IXMDDiffEngine diff = XMDDiffEngine.getInstance();
		IXMDDifference d = diff.diff(
				new XMD(prevRelationships),
				new XMD(curRelationships));
		Set<IRelationship> a = XMDDiffEngine.flat(d.getBeforeAddedToExist().getData());
		Set<IRelationship> b = XMDDiffEngine.flat(d.getBeforeAddedToNonExist().getData());
		Set<IRelationship> c = XMDDiffEngine.flat(d.getBeforeRemoved().getData());
		Set<IRelationship> dd = new HashSet(a);
		dd.addAll(b);
		dd.addAll(c);
		return dd;
	}
	
		
	private Map<Integer, List<IRelationship>> readPrevRelationships(
			IResource resource) throws RcException {
		// TODO Auto-generated method stub
//		readFileInfo from resource -> 
//		File _file, long _revision, long _wcRevision, boolean _isModified;
		
		ConfigManager configManager = ConfigManager.getInstance();
		IRcFactory rcFactory = configManager.createRcFactory();
		URI fileName = resource.getLocationURI();		
		IFileInfoReader<IFileInfo> fileinfoReader = rcFactory.createFileInfoReader(new File(fileName));//TODO: is this is what I need from the resource for the file???
		IFileInfo fileInfo = fileinfoReader.read();
		String fileNameStr = fileInfo.getFile().getAbsolutePath();//TODO: or canonial or other...
		String localPath = fileinfoReader.getRepositoryRootURL(fileNameStr);
			
		String name = fileInfo.getFile().getName();
		long revision = (Long) fileInfo.getRevision();
//		String localPath = "file://C:/junk/aop/aopWorkspace/repos";//TODO: find how to get this path...
		String fullPath = resource.getFullPath().toString();
		String url = fileInfo.getFile().toURI().toString();
		try {
			url = fileInfo.getFile().toURI().toURL().toString();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		url = localPath + fullPath; //worked but read from repository
		String repositoryPath = null;
		
		url = fileNameStr;//try to read from WC
		//		Debug costructor: SvnRepositoryResource 
//		[revisionId=327, 
//		 name=Cat.java,
//     file:/C:/junk/aop/runtime-New_configuration/thesis.delivery.test.aspectj.simplestexample/src/thesis/delivery/test/aspectj/simplestexample/Cat.java
//												  /thesis.delivery.test.aspectj.simplestexample/src/thesis/delivery/test/aspectj/simplestexample/Cat.java
//		 url=file://c:/junk/aop/aopWorkspace/repos/thesis.delivery.test.aspectj.simplestexample/src/thesis/delivery/test/aspectj/simplestexample/Cat.java,
// localPath=file://C:/junk/aop/aopWorkspace/repos, 
//		 repositoryPath=null]
		IRepositoryResource repositoryResource = new RepositoryResource(name, revision, localPath, url, repositoryPath);
		IRcPropertyReader<XMD> propertyReader = rcFactory.createPropertyReader(repositoryResource, XMD.class);
		String propName = configManager.getRcPropertyNameRelationshipsInfo();
//		String propName = configManager.getAspectJPropertyName();
		XMD relationshipsInfo = propertyReader.read(propName, false);
		if(relationshipsInfo == null){
			_logger.warn("prev version not found. if it is not the first version, there is a bug.");
			return null;
		}
		return relationshipsInfo.getData();	
//		return null;
	}

	/**
	 * This is done by a RelationshipInfoWriter with _resource that is java file.
	 * aspectResource - the aspect that affect the resource
	 */
	public void write(IResource aspectResource) throws RcException{
		Map<Integer, List<IRelationship>> relationships = findRelationships(_resource);
		List<ParentDeclarationInfo> parentDeclarationInfos = collectParentDeclerationInfo(aspectResource);
		
		XMD ri = new XMD(relationships);

		String localFilename = _resource.getLocation().toString();		
		XMDProcessor processor = new XMDProcessor();
		XMD riClone1 = processor.updateTargetsParentDeclaration(ri, parentDeclarationInfos);
		XMD riClone = processor.updateTargetsRevisionIds(riClone1);
		
		IRcPropertyWriter<Serializable> propertyWriter = _rcFactory.createPropertyWriter(localFilename, Serializable.class);
				
		propertyWriter.write(_configManager.getRcPropertyNameRelationshipsInfo(), riClone);		
	}	
	
//	/**
//	 * This is done by a RelationshipInfoWriter with _resource that is aj file.
//	 * aspectResource - the aspect that affect the resource
//	 */
//	public void write4Aspect() throws RcException{
//		Map<Integer, List<IRelationship>> relationships = findRelationships(_resource);
////		List<ParentDeclarationInfo> parentDeclarationInfos = collectParentDeclerationInfo(aspectResource);		
//		RelationshipsInfo ri = new RelationshipsInfo(relationships);
//		
//		String localFilename = _resource.getLocation().toString();		
////		RelationshipsInfoProcessor processor = new RelationshipsInfoProcessor();
////		RelationshipsInfo riClone1 = processor.updateTargetsParentDeclaration(ri, parentDeclarationInfos);
////		RelationshipsInfo riClone = processor.updateTargetsRevisionIds(riClone1);		
//		IRcPropertyWriter<Serializable> propertyWriter = _rcFactory.createPropertyWriter(localFilename, Serializable.class);
//				
//		propertyWriter.write(_configManager.getRcPropertyNameRelationshipsInfo(), ri);		
//	}	
	
	public Map<Integer, List<IRelationship>> findRelationships(IResource resource){
		return findRelationships(resource, AJRelationshipManager.getAllRelationshipTypes());
	}
	
	public Map<Integer, List<IRelationship>> findRelationships(IResource resource, AJRelationshipType[] types){
		ICompilationUnit cu = (ICompilationUnit)JavaCore.create(resource);
		AJProjectModelFacade model = AJProjectModelFactory.getInstance().getModelForJavaElement(cu);

		if (model.hasModel()) {		
			@SuppressWarnings("unchecked")
			Map<Integer,List<IRelationship>> relationships = model.getRelationshipsForFile(cu, types);
//			updateR(resource, cu, model, relationships);
			return relationships;
		} else {
			_logger.warn("model not found for resource: " + resource);			
			return null;
		}
	}

	//TODO: FIXME: it will only hold for external (regular) classes. what about internal classes?
	
	// collect all the parent-declerations in the aspect, and filter them for this instance's _resource

	// for the hirarchiy of the aspect ProgramElement-s, if the kind is declare-parents, get the handle and the details,
	// use the handle to get from the mapper the class that is being extended or that implements.
	private List<ParentDeclarationInfo> collectParentDeclerationInfo(IResource aspectResource) {		
		ICompilationUnit cu = (ICompilationUnit)JavaCore.create(aspectResource);
		AJProjectModelFacade model = AJProjectModelFactory.getInstance().getModelForJavaElement(cu);
		
		//FIXME: should be Set<types> for all the types in the class, and later check it's contained in...
		final String resourceType = _resource.getName()
									.substring(0, _resource.getName().indexOf('.'));
		if(_logger.isTraceEnabled())
			_logger.trace("collectParentDeclerationInfo: Resource: FileExtension = " + _resource.getFileExtension()  
				+ ", extension index = " + _resource.getName().indexOf(_resource.getFileExtension())
				+",name = " +_resource.getName().substring(0, _resource.getName().indexOf(_resource.getFileExtension())));

		final String aspectResourceName = aspectResource.getName();
		final List<ParentDeclarationInfo> parentDeclerations = new ArrayList<ParentDeclarationInfo>();
        IProgramElement ipe = model.javaElementToProgramElement(cu);
        //if the relationship is declaring parents and its on the _resource class, we want to 
        ipe.walk(new HierarchyWalker() {
            protected void preProcess(IProgramElement node) {
            	if(_logger.isTraceEnabled())
            		_logger.trace("nodeName = " +node.getName()+", kind = "+node.getKind()
            				+"details =" +node.getDetails());
            	if(node.getKind().equals(IProgramElement.Kind.DECLARE_PARENTS)
            	 ||node.getKind().equals(IProgramElement.Kind.INTER_TYPE_PARENT)
            	)//&& node.getSourceLocation().getSourceFile().getName().equals(resourceName))//TODO...
            	{ 
            		String handle = node.getHandleIdentifier();
            		if(_logger.isTraceEnabled())
            			_logger.trace("handle is:\n"+ handle);            	
            		String details = node.getDetails();
            		List<IRelationship> declareOnRelationships = node.getModel().getRelationshipMap().get(handle);
            		for (IRelationship relationship : declareOnRelationships) {
            			List<String> targets = relationship.getTargets();
            			for (String target : targets) {
            				if(_logger.isTraceEnabled())
            					_logger.trace("target is:\n" + target);
            				int typeIndex = target.indexOf(HandleProviderDelimiter.TYPE.getDelimiter()) + 1;//'['
            				int typeParamIndex = target.indexOf(HandleProviderDelimiter.TYPE_PARAMETER.getDelimiter());//']'
            				String type;
            				if(typeParamIndex>0)
            					type = target.substring(typeIndex, typeParamIndex);
            				else
            					type = target.substring(typeIndex);
            				if(_logger.isTraceEnabled())
            					_logger.trace("total1: " +
            						"[aspect:" + aspectResourceName+", class: "+resourceType+", type:"+ type +", details: "+ details+"]");
            				if(resourceType.equals(type)){//FIXME to support also internal classes.
            					ParentDeclarationInfo declareParentData = new ParentDeclarationInfo(
            							aspectResourceName, type, details, handle);
	            				parentDeclerations.add(declareParentData);
            				}
            			}            			
					}
            		//update relationship target with node.getDetails, after a special char.
            		if(_logger.isDebugEnabled())
            		_logger.debug("node: source file location = "+node.getSourceLocation().getSourceFile().getName()
            		+ "\n\tname= " + node.getName()+", want to add " + aspectResourceName + ", " + node.getDetails());
            	}
            }
        });
        if(_logger.isTraceEnabled())
        	_logger.trace("return parentDeclerations: "+parentDeclerations);
        return parentDeclerations;
	}
//	old tries - todo: delete this.
//	private void updateRold(IResource resource, Map<Integer, List<IRelationship>> relationships) {		
//		ICompilationUnit cu = (ICompilationUnit)JavaCore.create(resource);
//		AJProjectModelFacade model = AJProjectModelFactory.getInstance().getModelForJavaElement(cu);
//		
//		final String resourceName = resource.getName();
//        IProgramElement ipe = model.javaElementToProgramElement(cu);
//        ipe.walk(new HierarchyWalker() {
//            protected void preProcess(IProgramElement node) {
//            	_logger.debug("UpdateR:" +node.getName()+", "+node.getKind());
//            	_logger.debug("UpdateR2:" +node.getDetails());
//            	if(node.getKind().equals(IProgramElement.Kind.DECLARE_PARENTS)
//            	 ||node.getKind().equals(IProgramElement.Kind.INTER_TYPE_PARENT)
//            	)//&& node.getSourceLocation().getSourceFile().getName().equals(resourceName))//TODO...
//            	{
//            		//update relationship target with node.getDetails, after a special char.
//            		_logger.debug("UpdateR: node source file: "+node.getSourceLocation().getSourceFile().getName());
//            		_logger.debug("UpdateR: node name: "+node.getName());
//            		_logger.debug("UpdateR: want to add "+resourceName+", " + node.getDetails());
//            	}
//            }
//        });
//	}
	
}
