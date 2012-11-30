package xrc.api.ide.eclipse.core.io;

import java.io.File;

import org.apache.log4j.Logger;
import org.aspectj.asm.internal.HandleProviderDelimiter;
import org.aspectj.asm.internal.Relationship;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class RelationshipParser {
	
	private final static Logger _logger = Logger.getLogger(RelationshipParser.class);

	Relationship _relationship;

	private RelationshipParser(Relationship relationship) {
		super();
		this._relationship = relationship;
	}
	
//	public static String getJavaTargetResource(String target){
//		char javaResourceStartDelimeter = HandleProviderDelimiter.COMPILATIONUNIT.getDelimiter();
//		char javaResourceEndDelimeter = HandleProviderDelimiter.TYPE.getDelimiter();
//		int beginIndex = target.indexOf(javaResourceStartDelimeter) + 1;
//		int endIndex = target.indexOf(javaResourceEndDelimeter);
//		String resourceStr = target.substring(beginIndex, endIndex);
//		return resourceStr;
//	}
	
	public static IResource findFilename(String pathStr) {		 
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
//		String pathStr = RelationshipParser.findPath(target);		
		IPath path = Path.fromOSString(pathStr);
		
		IFile file = root.getFileForLocation(path); 
		return file;
	}
	
	public static String findPath(CharSequence target) {
		if(_logger.isDebugEnabled())
			_logger.debug("findTarget: target:\n" + target);
		StringBuilder targetSB = new StringBuilder(target);
		target = removeVersion(targetSB);
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IPath location = root.getLocation();
		String basePathStr = location.toOSString()+ File.separator; 
			
		StringBuilder basePath = new StringBuilder(basePathStr);
		//TODO: SAGI: handle case where the path of the package is not same as the package name 
		//e.g. rename the package name..
		int compilationUnitIndex = Math.max(//the target maight be java or aj
				targetSB.indexOf(HandleProviderDelimiter.COMPILATIONUNIT.getDelimiter()+""),
				targetSB.indexOf(HandleProviderDelimiter.ASPECT_CU.getDelimiter()+""));
		int typeIndex = Math.max(//the target maight be java or aj
				targetSB.indexOf(HandleProviderDelimiter.TYPE.getDelimiter()+""),
				Math.max(targetSB.indexOf(HandleProviderDelimiter.ASPECT_TYPE.getDelimiter()+""),
						 targetSB.indexOf("}")));//previously was the constant in HandleProviderDelimiter.ASPECT_TYPE.
		
		StringBuilder mainRelative = new StringBuilder(
				target.subSequence(
						targetSB.indexOf(HandleProviderDelimiter.JAVAPROJECT.getDelimiter()+"")+1,
						targetSB.indexOf(HandleProviderDelimiter.PACKAGEFRAGMENT.getDelimiter()+"")));
		StringBuilder secondaryRelative = new StringBuilder(
				target.subSequence(
						targetSB.indexOf(HandleProviderDelimiter.PACKAGEFRAGMENT.getDelimiter()+"")+1,
						compilationUnitIndex));
		for(int index = secondaryRelative.indexOf("."); index!=-1; index = secondaryRelative.indexOf(".")){
			secondaryRelative.setCharAt(index, '\\');//File.separator);//'\\');
		}
		StringBuilder RelativeFilename = new StringBuilder(
				target.subSequence(
						compilationUnitIndex + 1,
						typeIndex));
		StringBuilder filename = basePath.append(mainRelative).append(File.separator).append(secondaryRelative).append(File.separator).append(RelativeFilename);//was "\\" instead of File.pathSeparatorChar
		return filename.toString();
	}

	private static StringBuilder removeVersion(StringBuilder target) {
		int startVersion = target.indexOf("[");//TODO: becarefull, it also use for type delimieter (see HandleProviderDelimiter).
		if(startVersion <0 )
			return target;		
		String version = target.substring(startVersion);
		int endVersion = version.indexOf("]");
		//validation
		if(endVersion < 0){
			String message = "validation failed. Expected to find [version], ']' is not found after '[' in target:\n"+target;
			_logger.warn("removeVersion: " +message);
			return target;
		}
			
		String versionOnly = version.substring(1,endVersion);
		if(versionOnly.startsWith("*"))
			versionOnly = versionOnly.substring(1);
		try{
			Long ver = Long.valueOf(versionOnly);
		}catch (NumberFormatException e){
			String message = "validation failed. Expected for long version ["+ versionOnly+"] in tagrget:\n"+target;
			_logger.error("removeVersion: " +message);
		}
			
		target.delete(startVersion, startVersion+endVersion+1);
		return target;
	}

	public static void testRemoveVersion(String[] args) {
		System.out.println(
				removeVersion(
						new StringBuilder("=thesis.delivery.test.aspectj.simplestexample/src<thesis.delivery.test.aspectj.simplestexample*CatAspect.aj}CatAspect[*330]&before"))
//										   =thesis.delivery.test.aspectj.simplestexample/src<thesis.delivery.test.aspectj.simplestexample*CatAspect.aj}CatAspect      &before

				);
	}
	
	public static void main(String[] args) {
		String target ="=accountexample/src<accountexample{Account.java[Account~withdraw~I";
		//=accountexample/src<accountexample*BlackListAdvice.aj}BlackListAdvice~validateAccount~QAccount;
//		System.out.println(getJavaTargetResource(target));
		System.out.println(RelationshipParser.findFilename(null));
	}
	
}
