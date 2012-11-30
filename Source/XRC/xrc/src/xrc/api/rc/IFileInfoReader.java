package xrc.api.rc;

import xrc.api.common.RcException;

public interface IFileInfoReader<FileInfoType extends IFileInfo>  {

	public abstract FileInfoType read() throws RcException;//expect to store the file, that's why no file argument here.
	public abstract String getRepositoryRootURL(String filename) throws RcException;

}