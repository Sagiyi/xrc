package xrc.api.rc.svn;

import java.io.File;

import xrc.api.common.RcException;
import xrc.api.rc.FileInfo;
import xrc.api.rc.IFileInfoReader;


public class FileInfoReader implements IFileInfoReader<FileInfo> {
	//consider use of static or not static... and singletons...
	private static SvnFileInfoReader _svnFileInfoReader = new SvnFileInfoReader();
	private File _file;
	
	public FileInfoReader(File file){
		super();		
		_file = file;			
	}				
	
	/* (non-Javadoc)
	 * @see xrc.api.rc.svn.IFileInfoReader#read()
	 */
	@Override
	public FileInfo read() throws RcException{
		return  _svnFileInfoReader.read(_file);
	}
	
	/* (non-Javadoc)
	 * @see xrc.api.rc.svn.IFileInfoReader#getRepositoryRootURL()
	 */
	@Override
	public String getRepositoryRootURL(String filename) throws RcException{
		return  _svnFileInfoReader.getRepositoryRootURL(filename);
	}
}
