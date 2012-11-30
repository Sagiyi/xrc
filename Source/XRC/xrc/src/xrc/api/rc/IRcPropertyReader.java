package xrc.api.rc;

import java.io.Serializable;

import xrc.api.common.RcException;


public interface IRcPropertyReader<PropertyValueType extends Serializable> {	
	PropertyValueType read(String propName) throws RcException;//for markers return MarkersInfo
	PropertyValueType read(String propName, boolean isDirectFromRepository) throws RcException;//boolean decides if the read is from WC (filepath) or repository (url)

}
