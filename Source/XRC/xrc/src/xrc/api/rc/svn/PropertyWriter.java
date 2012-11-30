package xrc.api.rc.svn;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;

import xrc.api.common.RcException;
import xrc.api.rc.IRcPropertyWriter;



public class PropertyWriter<PropertyValueType extends Serializable> implements IRcPropertyWriter<PropertyValueType> {//IRcPropertyWriter<MarkersInfo>

	private final static Logger _logger = Logger.getLogger(PropertyWriter.class);	

	private File _file;
	private SvnPropertyWriter _snvPropertyWriter;
	
	
	public PropertyWriter(String localFilename) {
		super();		
		this._snvPropertyWriter = new SvnPropertyWriter();
		this._file = new File(localFilename);
	}


	@Override
	public boolean write(String propName, PropertyValueType provValue) throws RcException {
//		public boolean write(String propName, MarkersInfo provValue) throws RcException {

		byte[] dataBuffer;
		try {
			dataBuffer = getBytes(provValue);
			if(_logger.isDebugEnabled())
				_logger.debug("file is "+ _file);
			_snvPropertyWriter.write(propName, dataBuffer, _file);
		} catch (IOException e) {
			throw new RcException(e);
		} catch (SVNException e) {
			//if e is because file "is not under version control", only log a warning
			//it might be a new file, that has not inserted yet to version control.
			_logger.warn(e.getCause(),e);
			if(e.getMessage().contains("is not under version control")){				
				_logger.warn(e.getMessage()+",\nit might be a new file, that has not inserted yet to version control.");
			}//TODO else 
			throw new RcException(e);			
		}	
		return true;
	}

	//from sun
	public static byte[] getBytes(Object obj) throws java.io.IOException{//W3
	      ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	      ObjectOutputStream oos = new ObjectOutputStream(bos); 
	      oos.writeObject(obj);
	      oos.flush(); 
	      oos.close(); 
	      bos.close();
	      byte [] data = bos.toByteArray();
	      return data;
	}
}
