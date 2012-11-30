package xrc.api.ide.eclipse.core;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.ajde.core.AjCompiler;
import org.aspectj.ajde.core.IBuildMessageHandler;
import org.eclipse.ajdt.core.AspectJCore;
import org.eclipse.ajdt.core.builder.IAJBuildListener;
import org.eclipse.ajdt.core.lazystart.IAdviceChangedListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

import xrc.api.common.RcException;
import xrc.api.ide.eclipse.core.io.XMDWriter;
import xrc.config.ConfigManager;


/**
 * 2 options: 
 * 1 query the project for dirty files - does project support this?
 * 2 manage by myself the dirty files per project - on save mark as dirty, and after creating relationships mark as clean.
 * 	- consider that when openning a project maybe everything should be dirty. 
 * 
 * @author Administrator
 *
 */
public class AJBuildListener implements IAJBuildListener {
	
	private final static Logger _logger = Logger.getLogger(AJBuildListener.class);

	private ConfigManager _configManager = ConfigManager.getInstance();

	//TODO: don't forget to remove the listener after using it... (if using add/remove listener for managing dirty files)
	//second option is to have a single listener that will manage it's resources (dirty...)
	//	Set<IResources> resources;
	//	add, remove.
	private IResource _resource;	

	public AJBuildListener(IResource resource) {
		this._resource = resource;
	}
	
	@Override
	public void preAJBuild(int kind, IProject project,
			IProject[] requiredProjects) {
		if(_logger.isTraceEnabled())
			_logger.trace("preAJBuild");
//		_logger.debug(RelationshipParser.findJavaTargetFilename("=accountexample/src<accountexample{Account.java[Account~withdraw~I"));
		
		//TODO: read saved listeners from a file in the XRC plug-in start() method for example.
		//		incase Eclipse was shut-down after saves, and before build.
	}

	@Override
	public void postAJBuild(int kind, IProject project, boolean noSourceChanges, Map<IFile, List<CategorizedProblem>> newProblems) {
		if (noSourceChanges) {			            
			return; 
		}
		
		AjCompiler aadd = org.eclipse.ajdt.core.AspectJPlugin.getDefault().getCompilerFactory().getCompilerForProject(project);
		IBuildMessageHandler messageHandler 
					 = org.eclipse.ajdt.core.AspectJPlugin.getDefault().getCompilerFactory().getCompilerForProject(project)
					 .getMessageHandler(); 
		
//		IBuildMessageHandler messageHandler2 = aadd.getMessageHandler();		
//		if (messageHandler instanceof org.eclipse.ajdt.internal.ui.ajde.UIMessageHandler) {		
//			((org.eclipse.ajdt.internal.ui.ajde.UIMessageHandler) messageHandler).getErrors;		        
//		} 

		try {
			//since there is a problem to remove this with the removeListener...
			AdviceListenerManager adviceListenerManager = AdviceListenerManager.getInstance();
			if(!adviceListenerManager.hasListener(_resource)){
				return;
			}
			if(_logger.isDebugEnabled())
				_logger.debug("postAJBuild, going to write relationship-info for "+_resource.getName());
			XMDWriter relationshipsInfoWriter = new XMDWriter(_resource);		
//			relationshipsInfoWriter.write();//worked with this
			//verify build passed, else write changes only if configuration requires so.
			if(isBuildBuildFailed((IFile) _resource)){//TODO:  || !ConfigManager.getInstance().isToClearMarkersOnBuildFailure){ //conf' recommended false
				_logger.warn("postAJBuild: "+_resource.getName()+ 
						" build failed, so his and affected files markers will not be update for persistence (this can be changed in configuration [isToClearMarkersOnBuildFailure])");				
			}else{//build successful
				relationshipsInfoWriter.writeAll();//new. not tested.
			}
			//TODO:
			//this sometimes run for all the classes and sometimes only for the aj.
			//So, for the aj, I'll extract the targets's resources and run it for each of them.
			//Note: beware of duplications, if running several times for the same resource (e.g. once by the aspect, and once for the target resouce on their build).			
			//for each target of the advices in the aj:
			//	extract the resource from the handle.
			//	create RelationshipsInfoWriter
			// 	write()	
			
			adviceListenerManager.removeListener(_resource);
		} catch (RcException e) {
			e.printStackTrace();
//			System.exit(-1);//TODO: decide if required exit
		}		
	}
	
	//TODO: move..
//	if(isResourceBuiledPassed){ 
	public static boolean isBuildBuildFailed(IFile file){
		boolean isBuildBuildFailed = true;
		ICompilationUnit cu = (ICompilationUnit)AspectJCore.create((IFile) file);
		try {
			isBuildBuildFailed = !cu.isStructureKnown();// || cu.isConsistent();
		} catch (JavaModelException e) {
			_logger.warn("isBuildBuildFailed: JavaModelException, we suppose build failed.");			
		}
		return isBuildBuildFailed;
	}
	
	@Override
	public void addAdviceListener(IAdviceChangedListener adviceListener) { }
	
	@Override
	public void removeAdviceListener(IAdviceChangedListener adviceListener) { }

	@Override
	public void postAJClean(IProject project) { }

}
