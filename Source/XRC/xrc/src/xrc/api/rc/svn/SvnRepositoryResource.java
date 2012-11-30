package xrc.api.rc.svn;

import org.apache.log4j.Logger;
import org.eclipse.team.svn.core.connector.SVNConnectorException;

import xrc.api.common.RcException;
import xrc.api.ide.eclipse.core.AdviceListenerManager;



public class SvnRepositoryResource implements xrc.api.rc.IRepositoryResource {
	
	private final static Logger _logger = Logger.getLogger(AdviceListenerManager.class);

	org.eclipse.team.svn.core.resource.IRepositoryResource originalSvnRepositoryResource;
	
	public SvnRepositoryResource(org.eclipse.team.svn.core.resource.IRepositoryResource originalSvnRepositoryResource){
		this.originalSvnRepositoryResource = originalSvnRepositoryResource;
		if(_logger.isDebugEnabled())
			_logger.debug("Costructor: "+ this.toString());
	}
	
	@Override
	public Comparable<?> getRevisionId() throws RcException {
		try {
			return originalSvnRepositoryResource.getRevision();
		} catch (SVNConnectorException e) {
			// TODO Auto-generated catch block
			throw new RcException(e);
		}		
	}

	@Override
	public String getName() {		
		return originalSvnRepositoryResource.getName();
	}

	@Override
	public String getUrl() {
		return originalSvnRepositoryResource.getUrl();
	}

	@Override
	public String getLocalPath() {		
		return originalSvnRepositoryResource.getRepositoryLocation().getUrl();//TODO:check this
	}

	@Override
	public String getRepositoryPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		try {
			return "SnvRepositoryResource [revisionId=" + getRevisionId().toString() + ", name="
			+ getName() + ", url=" + getUrl() + ", localPath=" + getLocalPath()
			+ ", repositoryPath=" + getRepositoryPath() + "]";
		} catch (RcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "RepositoryResource [revisionId= failedTogetRevisionId" + ", name="
			+ getName() + ", url=" + getUrl() + ", localPath=" + getLocalPath()
			+ ", repositoryPath=" + getRepositoryPath() + "]";
		}
	}
}
