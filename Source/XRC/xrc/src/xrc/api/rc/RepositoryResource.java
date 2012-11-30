package xrc.api.rc;

import xrc.api.common.RcException;

/**
 * This is actually implementation for SVN, but the only place that is effected by it
 * is the use of long for the revisionId, so I put consider it also as default implementation,
 * this is the reason it's located here and not under svn package. 
 * @author Sagi Ifrah
 *
 */

public class RepositoryResource implements xrc.api.rc.IRepositoryResource{

	private long _revisionId;
	private String _name;
	private String _url;
	private String _localPath;
	private String _repositoryPath;
	
	//TODO: consider use the svnFactory for create..
	public RepositoryResource(String _name, long _revisionId,
			String _localPath, String _url, String _repositoryPath) {
		super();
		this._name = _name;
		this._revisionId = _revisionId;
		this._localPath = _localPath;
		this._url = _url;
		this._repositoryPath = _repositoryPath;
	}

	@Override
	public Comparable<?> getRevisionId() throws RcException {
		return _revisionId;
	}

	@Override
	public String getName() throws RcException {
		return _name;
	}

	@Override
	public String getUrl() throws RcException {
		return _url;
	}

	@Override
	public String getLocalPath() throws RcException {
		return _localPath;
	}

	@Override
	public String getRepositoryPath() throws RcException {
		return _repositoryPath;
	}

	@Override
	public String toString() {
		return "RepositoryResource [revisionId=" + _revisionId + ", name="
				+ _name + ", url=" + _url + ", localPath=" + _localPath
				+ ", repositoryPath=" + _repositoryPath + "]";
	}

	
}
