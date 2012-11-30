package xrc.api.rc.svn;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

//TODO: make it singleton
public class SvnPropertyReader {	
	
	private static SVNWCClient _svnwcClient;
	private static final String _repositoryUrl = "file:////i://aopWorkspace//repos"; 
	
	public SvnPropertyReader(){	
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
		 *  read the property from working copy.
		 * @param propName
		 * @param svnPropValue
		 * @param filename
		 * @return 
		 */
	public SVNPropertyData read(String propName, String filename){
//		filename="C:\\junk\\aop\\runtime-New_configuration\\thesis.delivery.test.aspectj.simplestexample\\src\\thesis\\delivery\\test\\aspectj\\simplestexample\\Cat.java";
//		propName="testtt";
			try {			
				File file = new File(filename);				
				return _svnwcClient.doGetProperty(file,									   
				   propName,
				   SVNRevision.WORKING,
				   SVNRevision.WORKING);								
			} catch (SVNException e) {
				e.printStackTrace();
			}			
			return null;
	}
		
	public SVNPropertyData read(String propName, String filename, int revision) throws SVNException{					
			File file = new File(filename);				
			return _svnwcClient.doGetProperty(SVNURL.parseURIEncoded(//_repositoryUrl + 
																					filename), 								   
			   propName,
			   SVNRevision.create(revision),
			   SVNRevision.create(revision));	
	}
	
	public static void main(String[] args) {
		SvnPropertyReader svnPropertiesReader = new SvnPropertyReader();		
		SVNPropertyData data = svnPropertiesReader.read("propNameee644e"					
				,"C:\\I\\aopWorkspace\\eclipseWorkspace\\test.eclipse.compare.examples\\test"
				);
		String val = new String(data.getValue().getBytes());
		System.out.println("val = "+val+"\nmain:done");
	}
	
}
