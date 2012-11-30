package xrc.api.rc.svn;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.team.svn.core.connector.SVNConnectorException;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

//TODO: make it singleton
//TODO: clean from unnecessary methods.
public class SvnPropertyWriter {	
	
	private static SVNWCClient _svnwcClient;
//	private static final String _repositoryUrl = "";
	
	public SvnPropertyWriter(){	
		init();
	}
	
	public void init(){			
		FSRepositoryFactory.setup();
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		/*
		 * Creates an instance of SVNClientManager providing authentication
		 * information (name, password) and an options driver
		 */
		SVNClientManager svnClientManager = SVNClientManager.newInstance(options, "", "");//options,name,pwd
		_svnwcClient = svnClientManager.getWCClient();
	}
	
		/**
		 * The good method...
		 *  
		 * @param propName
		 * @param propValue
		 * @param filename
		 * @throws SVNException 
		 */
	public void write(String propName, byte[] propValue, File file) throws SVNException{
							
				SVNPropertyValue svnPropValue = SVNPropertyValue.create(propName, propValue);  //I didn't see meaning for propName here, I'll put there the same name as above.
				_svnwcClient.doSetProperty(file,									   
				   propName,
				   svnPropValue,			   									   
				   false,//skipChecks 
				   SVNDepth.EMPTY,//apply only current path
				   null,//ISVNPropertyHandler handler
				   null);//Collection<String> changeLists (filter)
//				_svnwcClient.doCleanup(file.getParentFile());
	}
	
	

	public static void main(String[] args) throws SVNException {
		SvnPropertyWriter svnPropertiesWriter = new SvnPropertyWriter();		
		svnPropertiesWriter.write("propName5455"
				,"propValue2241".getBytes()			
				,new File("C:\\I\\aopWorkspace\\eclipseWorkspace\\test.eclipse.compare.examples\\test")
				);
		System.out.println("main:done");
	}
	
	
	
	
	
	/**
	 * @deprecated unless there is reason to use IFile here...
	 * @param propName
	 * @param propValue
	 * @param file
	 * @throws SVNException 
	 */
	public void write(String propName, byte[] propValue, IFile file) throws SVNException{
		String filename = file.getLocation().toString();		
		write(propName, propValue, new File(filename));
	}
	
	/**
	 * 
	 * @deprecated set the property directly on the repository, instead of on the local file.
	 * 
	 * @param propName
	 * @param svnPropValue
	 * @param repositoryUrl
	 * @param filename
	 * @param revision
	 */
	public void write(String propName, SVNPropertyValue svnPropValue, String repositoryUrl, String fileUrl, int revision){
		try{		
			_svnwcClient.doSetProperty(SVNURL.parseURIEncoded(repositoryUrl + fileUrl),									   
					   propName,
					   svnPropValue,
					   SVNRevision.create(revision),
					   "",//string//message
					   null,//SVNProperties									   
					   false,//skipChecks 
					   null);
		} catch (SVNException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * @deprecated set the property directly on the repository, instead of on the local file.
	 * 
	 * @param propName
	 * @param propValue
	 * @param repositoryResource
	 */
	public void write(String propName, SVNPropertyValue propValue, IRepositoryResource repositoryResource) {
		//write(repositoryUrl, fileUrl, revision);
		try {//TODO: or doSetRevisionProperty ???
									
			_svnwcClient.doSetProperty(SVNURL.parseURIEncoded(repositoryResource.getUrl()),									   
									   propName,
									   propValue,
									   SVNRevision.create(repositoryResource.getRevision()),
									   "",//string//message
									   null,//SVNProperties									   
									   false,//skipChecks 
									   null);
			
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (SVNConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}		
	}
}
