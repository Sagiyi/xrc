package xrc.api.rc;

import xrc.api.common.RcException;

public interface IRepositoryResource {
	
	Comparable<?> getRevisionId() throws RcException;// in SVN it's the long - getRevision()
	String getName() throws RcException;;// In SVN
	
	
	String getUrl() throws RcException;;//???
	String getLocalPath() throws RcException;;//??? can I get the expected location in local copy? 
	String getRepositoryPath() throws RcException;;//??? required??? is it relative path??? = getUrl??
}
