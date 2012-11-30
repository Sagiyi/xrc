package xrc.config;

public enum DiffTypeConstants{
	BeforeRemoved(			"(>-)",		"xrc.changedAdviceRemove"),
	BeforeAddedToExist(		"(>e+)",	"xrc.changedAdviceAdd"),
	BeforeAddedToNonExist(	"(>ne+)",	"xrc.changedAdviceAdd"),//new marker - not need to modify existing marker
	BeforeModified(			"(>M*)",	"xrc.changedAdviceModified"),		
	
	AfterAdded(				"(+)",		"xrc.changedAdviceAdd"),
	AfterRemovedToExist(	"(e-)",		"xrc.changedAdviceRemove"),
	AfterRemovedToNonExist(	"(ne-)",	"xrc.changedAdviceRemove"),//new marker - not need to modify existing marker
	AfterModified(			"(M*)",		"xrc.changedAdviceModified");
	
	private String _label;
	private String _markerType;
	
	private DiffTypeConstants(String label, String markerTypePrefix){
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