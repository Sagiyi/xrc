package xrc.api.rc.svn;

import java.io.File;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import xrc.api.common.RcException;
import xrc.api.rc.FileInfo;
import xrc.api.rc.IFileInfo;
import xrc.api.rc.IFileInfoReader;


public class SvnFileInfoReader {
	
	private final static Logger _logger = Logger.getLogger(SvnFileInfoReader.class);

	
	private static SVNStatusClient _svnStatusClient;
	private static SVNWCClient _svnwcClient;
	
	private static final String _repositoryUrl = "file:////i://aopWorkspace//repos"; 
	
	public SvnFileInfoReader(){	
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
		_svnStatusClient = svnClientManager.getStatusClient();
		_svnwcClient = svnClientManager.getWCClient();
	}
		
	public FileInfo read(File file) throws RcException{
		SVNStatus status;
		try {
			status = _svnStatusClient.doStatus(file, false);
			long lastCommitedRevision = status.getCommittedRevision().getNumber();
			long workingRevision = status.getRevision().getNumber(); //TODO: understand the difference between last-commit and working-copy revisions. (see status -verbose doc).
			//is this also useful?
			boolean isModified = status.getContentsStatus().equals(SVNStatusType.STATUS_MODIFIED);
			return new FileInfo(file, lastCommitedRevision, isModified);
		} catch (SVNException e) {
			throw new RcException(e);
		}
	}
	
	//TODO: move mains to Tests.
	public static void main1(String[] args) throws SVNException {
		SvnFileInfoReader reader = new SvnFileInfoReader();
		//check if a file is modified.
		//get the revision of a file.
		
		File wcPath = new File("C:\\aop\\New\\workspace\\xrc\\src\\xrc\\temp\\MarkersInfoCreator.java");
		SVNStatus stat = _svnStatusClient.doStatus(wcPath, false);
		System.out.println(stat.getContentsStatus());
		System.out.println(stat.getRevision().getID());
		System.out.println(stat.getRevision().getNumber());
		System.out.println(stat.getRevision().getName());		
	}
	
	public static void main(String[] args) throws SVNException, RcException {
//		SvnFileInfoReader reader = new SvnFileInfoReader();
		//check if a file is modified.
		//get the revision of a file.
		
		File wcPath = new File("C:\\aop\\New\\workspace\\xrc\\src\\xrc\\temp\\MarkersInfoCreator.java");
//		SVNStatus stat = _svnStatusClient.doStatus(wcPath, false);
		IFileInfoReader reader = new FileInfoReader(wcPath);		
		IFileInfo fileInfo = reader.read();		
		System.out.println(fileInfo.getRevision());
		System.out.println(fileInfo.isModified());
				
	}	
	
	
	public String getRepositoryRootURL(String filename) throws RcException{		
//		filename = "C:/junk/aop/runtime-New_configuration/thesis.delivery.test.aspectj.simplestexample/src/thesis/delivery/test/aspectj/simplestexample/Cat.java";
		File file = new File(filename);
		SVNInfo svnInfo = null;
		try{
			svnInfo = _svnwcClient.doInfo(file, SVNRevision.WORKING);
		} catch (SVNException e) {
			throw new RcException(e);
		}
		SVNURL url = svnInfo.getRepositoryRootURL();
		return url.toString();
	}
}
