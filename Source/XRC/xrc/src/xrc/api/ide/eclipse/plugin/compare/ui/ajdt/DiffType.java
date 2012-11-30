package xrc.api.ide.eclipse.plugin.compare.ui.ajdt;

import xrc.config.DiffTypeConstants;

/**
 * The separation / weird enum delegation that I used is in order to keep the constants away and as configurable as possible,
 * yet to keep the business (DiffTypes) in the right place. 
 * modification of existing diff-types is open using the config, but add/rename/remove diff-types is actual business and need modifications here.
 * @author Sagi Ifrah
 *
 */
public enum DiffType{
	BeforeRemoved(			DiffTypeConstants.BeforeRemoved) ,	
	BeforeAddedToExist(		DiffTypeConstants.BeforeAddedToExist),
	BeforeAddedToNonExist(	DiffTypeConstants.BeforeAddedToNonExist),//new marker - not need to modify existing marker
	BeforeModified(			DiffTypeConstants.BeforeModified),		
	
	AfterAdded(				DiffTypeConstants.AfterAdded),
	AfterRemovedToExist(	DiffTypeConstants.AfterRemovedToExist),
	AfterRemovedToNonExist(	DiffTypeConstants.AfterRemovedToNonExist),//new marker - not need to modify existing marker
	AfterModified(			DiffTypeConstants.AfterModified);
	
	private String _label;
	private String _markerType;
	
	private DiffType(DiffTypeConstants diffTypeConstants){
		this._label = diffTypeConstants.getLabel();
		this._markerType = diffTypeConstants.getMarkerType();
	}
	
	private DiffType(String label, String markerTypePrefix){
		this._label=label;
		this._markerType = markerTypePrefix;
	}
	
	public String getLabel(){
		return _label;
	}
	
	public String getMarkerType(){
		return _markerType;
	}
}