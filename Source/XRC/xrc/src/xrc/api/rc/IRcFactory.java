package xrc.api.rc;

import java.io.Serializable;

import xrc.api.common.RcException;
import xrc.api.rc.svn.PropertyReader;
import xrc.api.rc.svn.PropertyWriter;


public interface IRcFactory {

	public abstract <PropertyValueType extends Serializable> 
		PropertyWriter<PropertyValueType> createPropertyWriter(
				String localFilename) throws RcException;

	public abstract <PropertyValueType extends Serializable> 
		PropertyWriter<PropertyValueType> createPropertyWriter(
				String localFilename, Class<PropertyValueType> clazz) throws RcException;

	public abstract <PropertyValueType extends Serializable> 
		PropertyReader<PropertyValueType> createPropertyReader(
			IRepositoryResource repositoryResource) throws RcException;

	public abstract <PropertyValueType extends Serializable> 
	PropertyReader<PropertyValueType> createPropertyReader(
			IRepositoryResource repositoryResource,
			Class<PropertyValueType> clazz) throws RcException;

	public abstract <RcRepositoryResourceType> 
		IRepositoryResource createRepositoryResource(
			RcRepositoryResourceType repositoryResource) throws RcException;

	public abstract <FileInfoType extends IFileInfo> 
		IFileInfoReader<FileInfoType> createFileInfoReader(java.io.File file) throws RcException;

}