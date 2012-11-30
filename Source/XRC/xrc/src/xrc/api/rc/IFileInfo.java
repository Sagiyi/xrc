package xrc.api.rc;

import java.io.File;
//TODO: consider merging to IRepositoryResource
public interface IFileInfo {
	public File getFile();
	public boolean isModified();
	public Comparable<?> getRevision();//TODO: veridy if in we want the WC revision, or the revision... maight be a bug here in this!
}
