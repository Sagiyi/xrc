package xrc.api.rc.svn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNPropertyData;

import xrc.api.common.RcException;
import xrc.api.rc.IRcPropertyReader;
import xrc.api.rc.IRepositoryResource;


public class PropertyReader<PropertyValueType extends Serializable> implements IRcPropertyReader<PropertyValueType> {

	private SvnPropertyReader _snvPropertyReader; 
	private IRepositoryResource _repositoryResource;
	
	
	public PropertyReader(IRepositoryResource repositoryResource) {
		super();
		this._snvPropertyReader = new SvnPropertyReader();//TODO: move to singleton, don't need new instances.
		this._repositoryResource = repositoryResource;
	}

	@Override
	public PropertyValueType read(String propName) throws RcException {
		return read(propName, true);
	}

	@Override
	public PropertyValueType read(String propName,boolean isDirectFromRepository) throws RcException {
		try {
			String filename = _repositoryResource.getUrl();
			long revision = (Long) _repositoryResource.getRevisionId();
			SVNPropertyData data;
			if(isDirectFromRepository){
				data = _snvPropertyReader.read(propName, filename, (int)revision);
			}else{//read from WC.
				data = _snvPropertyReader.read(propName, filename);
			}
			if(data==null){ return null; }
			byte[] dataBuffer = data.getValue().getBytes();
			PropertyValueType myResourceInfo = (PropertyValueType)getObject(dataBuffer);
		return myResourceInfo;
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			throw new RcException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RcException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();//TODO: remove and verify the caller do this.
			throw new RcException(e);
		}
	}
	
	
	public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object obj = ois.readObject();
		ois.close();
		bis.close();
		return obj;
	}
}
