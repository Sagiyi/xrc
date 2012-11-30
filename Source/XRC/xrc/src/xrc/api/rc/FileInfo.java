package xrc.api.rc;

import java.io.File;

/**
 * This is actually implementation for SVN, but the only place that is effected by it
 * is the use of long for the revision, so I put consider it also as default implementation,
 * this is the reason it's located here and not under svn package. 
 * @author Sagi Ifrah
 *
 */
public class FileInfo implements IFileInfo{
	private File _file;
	private long _revision;
	private long _wcRevision;
	private boolean _isModified;
	
	public FileInfo(File file, long _revision, boolean _isModified) {
		super();
		this._file = file;
		this._revision = _revision;
		this._isModified = _isModified;
	}
	
	public File getFile() {
		return _file;
	}

	public void setFile(File file) {
		this._file = file;		
	}

	public Long getRevision() {
		return _revision;
	}
	public void setRevision(long revision) {
		this._revision = revision;
	}
	public Long getWcRevision() {
		return _wcRevision;
	}
	public void setWcRevision(long wcRevision) {
		this._wcRevision = wcRevision;
	}
	public boolean isModified() {
		return _isModified;
	}
	public void setModified(boolean isModified) {
		this._isModified = isModified;
	}	
}