package xrc.api.rc;

import java.io.Serializable;

import xrc.api.common.RcException;


public interface IRcPropertyWriter<PropertyValueType extends Serializable> {
	boolean write(String propName, PropertyValueType provValue) throws RcException;//for markers provValue is MarkersInfo
}
