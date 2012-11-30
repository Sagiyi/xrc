package xrc.api.rc.svn;

import java.io.File;
import java.io.Serializable;

import xrc.api.common.RcException;
import xrc.api.rc.FileInfo;
import xrc.api.rc.IFileInfo;
import xrc.api.rc.IFileInfoReader;
import xrc.api.rc.IRcFactory;
import xrc.api.rc.IRepositoryResource;



public class SvnFactory implements IRcFactory {
	
	/* (non-Javadoc)
	 * @see xrc.rcapi.svn.IRcFactory#createPropertyWriter(java.lang.String)
	 */
	@Override
	public <PropertyValueType extends Serializable> 
		PropertyWriter<PropertyValueType> createPropertyWriter(String localFilename){
			return new PropertyWriter<PropertyValueType>(localFilename);
	}
	
	/* (non-Javadoc)
	 * @see xrc.rcapi.svn.IRcFactory#createPropertyWriter(java.lang.String, java.lang.Class)
	 */
	@Override
	public <PropertyValueType extends Serializable>
		PropertyWriter<PropertyValueType> createPropertyWriter(String localFilename, Class<PropertyValueType> clazz){
				return new PropertyWriter<PropertyValueType>(localFilename);
	}
	
	/* (non-Javadoc)
	 * @see xrc.rcapi.svn.IRcFactory#createPropertyReader(xrc.rcapi.IRepositoryResource)
	 */
	@Override
	public <PropertyValueType extends Serializable> 
		PropertyReader<PropertyValueType> createPropertyReader(IRepositoryResource repositoryResource){
			return new PropertyReader<PropertyValueType>(repositoryResource);
	}
	
	/* (non-Javadoc)
	 * @see xrc.rcapi.svn.IRcFactory#createPropertyReader(xrc.rcapi.IRepositoryResource, java.lang.Class)
	 */
	@Override
	public <PropertyValueType extends Serializable> 
	PropertyReader<PropertyValueType> createPropertyReader(IRepositoryResource repositoryResource, Class<PropertyValueType> clazz){
		return new PropertyReader<PropertyValueType>(repositoryResource);
	}
	
	/* (non-Javadoc)
	 * @see xrc.api.rc.IRcFactory#createRepositoryResource(RcRepositoryResourceType)
	 */
	@Override
	public <RcRepositoryResourceType> IRepositoryResource createRepositoryResource(RcRepositoryResourceType repositoryResource){
		//TODO: decide if to wrap with RcException or leave the ClassCastException to handle this
//		if (repositoryResource instanceof org.eclipse.team.svn.core.resource.IRepositoryResource) {
			org.eclipse.team.svn.core.resource.IRepositoryResource originalSvnRepositoryResource = (org.eclipse.team.svn.core.resource.IRepositoryResource) repositoryResource;
			return new SvnRepositoryResource(originalSvnRepositoryResource);
//		}
	}

	
	@Override
	public <FileInfoType extends IFileInfo> IFileInfoReader<FileInfoType>  createFileInfoReader(File file) throws RcException {
		return (IFileInfoReader<FileInfoType>) new FileInfoReader(file);
	}
		
}
