package xrc.api.ide.eclipse.core.io;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.HandleProviderDelimiter;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import xrc.api.common.RcException;
import xrc.api.ide.eclipse.core.dto.XMD;
import xrc.api.ide.eclipse.core.dto.additionalrelationshipinfo.ParentDeclarationInfo;
import xrc.api.rc.IFileInfo;
import xrc.api.rc.IFileInfoReader;
import xrc.api.rc.IRcFactory;
import xrc.config.ConfigManager;

public class XMDProcessor {
	
	private final static Logger _logger = Logger.getLogger(XMDProcessor.class);

	private ConfigManager _configManager = ConfigManager.getInstance();
	private IRcFactory _rcFactory = _configManager.createRcFactory();
	
	public XMDProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public XMD updateTargetsRevisionIds(XMD relationshipsInfo) throws RcException {
		XMD clone = new XMD(relationshipsInfo); 
		Collection<List<IRelationship>> values = clone.getData().values();
		for (List<IRelationship> list : values) {
			for (IRelationship relationship : list) {
				@SuppressWarnings("unchecked")
				List<String> targets = relationship.getTargets();
				for (int i = 0; i < targets.size(); i++) {
					String target = targets.get(i);					
					String updatedTarget = createUpdatedTarget(target).toString();
					targets.set(i, updatedTarget);					
				}
			}
		}	
		return clone;
	}


	/**
	 * regarding isModified:
	 * It is very important to notice visually if the versioned aspect that effect the source has been changed its last commit
	 * or if it is clean, since if it is clean we know for sure that the marker reflects the effect of the versioned aspect,
	 * else we can only guess that it might reflect the aspect in the version we found or it's successor,
	 * but it also might reflect a state that the aspect have been while committing the java that was effected, 
	 * but than the aspect was changed again, and we don't have anywhere the real aspect that influenced the java class while it was committed. 
	 * TODO: in GUI it would be very nice to have other color when having dirty on same target& its version,
	 * 		 since it's suspected for different, but cannot be detected.(pink marker) note case of both pink & yellow... 
	 * 
	 */
	private CharSequence createUpdatedTarget(CharSequence target) throws RcException {				
		CharSequence targetFilename = findTargetFilename(target);
		if("-1".equals(targetFilename)){//identify it as aj... TODO
			return target;
		}
		IFileInfo fileInfo = readFileInfo(targetFilename.toString());
		return createUpdatedTarget(target, fileInfo);		
	}

	protected CharSequence createUpdatedTarget(CharSequence target, IFileInfo fileInfo) {
		Comparable<?> targetRevisionId = fileInfo.getRevision();
		boolean isModified = fileInfo.isModified();
		CharSequence newUpdatedTarget = createUpdatedTarget(target, targetRevisionId, isModified );
		return newUpdatedTarget;
	}

	//TODO: this can probably done faster with tokenizer or smarter parsing, since we go over the string and get/process each part.
	private CharSequence findTargetFilename(CharSequence target) {
		if(_logger.isDebugEnabled())
			_logger.debug("target is:\n"+target);		
		StringBuilder targetSB = new StringBuilder(target);
		if(targetSB.indexOf("*")==-1){
			if(_logger.isDebugEnabled())
				_logger.debug("I think it's not an affected, but affect - not the java but the advice aj");
			return "-1";//TODO...
		}
//		String basePathStr = System.getProperty("user.dir");
//		String basePathStr = "C:\\aop\\New\\runtime-New_configuration\\";
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath location = root.getLocation();
		String basePathStr = location.toOSString()+ File.separator; 
			
		StringBuilder basePath = new StringBuilder(basePathStr);
		//TODO: SAGI: handle case where the path of the package is not same as the package name 
		//e.g. rename the package name..
		StringBuilder mainRelative = new StringBuilder(target.subSequence(targetSB.indexOf("=")+1, targetSB.indexOf("<")));
		StringBuilder secondaryRelative = new StringBuilder(target.subSequence(targetSB.indexOf("<")+1, targetSB.indexOf("*")));
		for(int index = secondaryRelative.indexOf("."); index!=-1; index = secondaryRelative.indexOf(".")){
			secondaryRelative.setCharAt(index, '\\');
		}
				
		int aspectTypeDelimiterIndex = Math.max(targetSB.indexOf(HandleProviderDelimiter.ASPECT_TYPE.getDelimiter()+""),
				targetSB.indexOf("}"));//old versions delimeter.
		StringBuilder RelativeFilename = new StringBuilder(target.subSequence(targetSB.indexOf("*")+1, aspectTypeDelimiterIndex));
		StringBuilder filename = basePath.append(mainRelative).append("\\").append(secondaryRelative).append("\\").append(RelativeFilename); 
//		String projectPath = "=" to "<";
//		String sssPath = "<" to "*" but replace "." with "\\";
//		filename = "* to "}"" +
//		combine all.
		
//		C:\aop\New\runtime-New_configuration\thesis.delivery.test.aspectj.simplestexample\src\thesis\delivery\test\aspectj\simplestexample\TraceAspect.aj
//											 thesis.delivery.test.aspectj.simplestexample\src\thesis\delivery\test\aspectj\simplestexample
		return filename;
	}

	public IFileInfo readFileInfo(String targetFilename) throws RcException{
		File file = new File(targetFilename);
		IFileInfoReader<? extends IFileInfo> fileInfoReader = _rcFactory.createFileInfoReader(file);
		IFileInfo fileInfo = fileInfoReader.read();
		return fileInfo;
	}
	
	private CharSequence createUpdatedTarget(CharSequence target,
			Comparable<?> targetRevisionId, boolean isModified) {
		StringBuilder newTaget = new StringBuilder(target);
		StringBuilder dirtyPrefix = new StringBuilder(isModified?"*":"");
//		CharSequence revision = targetRevisionId.toString();
		if(_logger.isTraceEnabled())
			_logger.trace("createUpdatedTarget, target is:\n"+ target);
		int index = newTaget.indexOf("&");
		if(index==-1){//IDT (e.g. adding a field TODO: what about interface, extends, method
			index = newTaget.indexOf(")");
		}
		if(index==-1){//declare parents TODO: what about interface, extends, method
			index = newTaget.indexOf("`");//TODO: is this what I mean??? what do I want here?
		}
		if(index==-1){
			index = newTaget.indexOf("~");//TODO: is this what I mean??? what do I want here?
		}
		//If index=-1 someting is wrong and an exception will be thrown
		newTaget.insert(index, "["+ dirtyPrefix + //TODO: use append...
				(targetRevisionId!=null?targetRevisionId.toString():"undef")
				+"]");
		return newTaget;
	}

	public XMD updateTargetsParentDeclaration(XMD relationshipsInfo,
			List<ParentDeclarationInfo> parentDeclarationInfos) {	
		XMD clone = new XMD(relationshipsInfo); 

		Collection<List<IRelationship>> values = clone.getData().values();
		for (List<IRelationship> list : values) {
			for (IRelationship relationship : list) {
				@SuppressWarnings("unchecked")
				List<String> targets = relationship.getTargets();
				for (int i = 0; i < targets.size(); i++) {
					String target = targets.get(i);
					
					for (ParentDeclarationInfo parentDeclarationInfo : parentDeclarationInfos){
						String handle = parentDeclarationInfo.getHandle();
						if(handle != null && handle.equals(target)){
							if(_logger.isTraceEnabled())
								_logger.trace("updateTargetsParentDeclaration: parents found for target:\n" + target);
							String updatedTarget = updateParentDecleration(target, parentDeclarationInfo).toString();
							targets.set(i, updatedTarget);							
						}
					}									
				}
			}
		}
		return clone;
	}

	private CharSequence updateParentDecleration(String target,
			ParentDeclarationInfo parentDeclarationInfo) {
		return target + " " +parentDeclarationInfo.getAffectedType()+" "+ parentDeclarationInfo.getDetails();
		
	}
	
	
//	in parser?
//	private IResource findJavaTargetFilename(CharSequence target) {		
//		String pathStr = RelationshipParser.findPath(target);
//		IPath path = Path.fromOSString(pathStr); 
//		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path); 
//		return file;
//	}

}
