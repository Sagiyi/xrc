package xrc.api.compare.engine;

import xrc.api.compare.IXMDDifference;
import xrc.api.ide.eclipse.core.dto.XMD;
import xrc.api.ide.eclipse.plugin.compare.ui.ajdt.DiffType;

/**
 * RelationshipsInfoDifference is a DTO that contains all the difference between two RelationshipsInfo objects.
 * each RelationshipsInfo contains only the difference (see {@link XMDDiffEngine})
 * 
 * see also {@link DiffType}
 */
public class XMDDifference implements IXMDDifference{
			
	private XMD _before;
	private XMD _beforeRemoved;
	private XMD _beforeAddedToExist;
	private XMD _beforeAddedToNonExist;
	
	private XMD _after;
	private XMD _afterAdded; 
	private XMD _afterRemovedToExist;
	private XMD _afterRemovedToNonExist;	
	
	public XMDDifference(
			XMD before,
			XMD beforeRemoved,
			XMD beforeAddedToExist,
			XMD beforeAddedToNonExist,
			XMD after,
			XMD afterAdded,
			XMD afterRemovedToExist,
			XMD afterRemovedToNonExist) {
		super();
		this._before = before;
		this._beforeRemoved = beforeRemoved;
		this._beforeAddedToExist = beforeAddedToExist;
		this._beforeAddedToNonExist = beforeAddedToNonExist;
		this._after = after;
		this._afterAdded = afterAdded;
		this._afterRemovedToExist = afterRemovedToExist;
		this._afterRemovedToNonExist = afterRemovedToNonExist;
	}

	

	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getBefore()
	 */
	@Override
	public XMD getBefore() {
		return _before;
	}

	public void setBefore(XMD before) {
		this._before = before;
	}
	
	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getBeforeRemoved()
	 */
	@Override
	public XMD getBeforeRemoved() {
		return _beforeRemoved;
	}

	public void setBeforeRemoved(XMD beforeRemoved) {
		this._beforeRemoved = beforeRemoved;
	}

	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getBeforeAddedToExist()
	 */
	@Override
	public XMD getBeforeAddedToExist() {
		return _beforeAddedToExist;
	}

	public void setBeforeAddedToExist(XMD beforeAddedToExist) {
		this._beforeAddedToExist = beforeAddedToExist;
	}

	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getBeforeAddedToNonExist()
	 */
	@Override
	public XMD getBeforeAddedToNonExist() {
		return _beforeAddedToNonExist;
	}

	public void setBeforeAddedToNonExist(XMD beforeAddedToNonExist) {
		this._beforeAddedToNonExist = beforeAddedToNonExist;
	}

	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getAfterAdded()
	 */
	@Override
	public XMD getAfter() {
		return _after;
	}

	public void setAfter(XMD after) {
		this._after = after;
	}

	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getAfterAdded()
	 */
	@Override
	public XMD getAfterAdded() {
		return _afterAdded;
	}

	public void setAfterAdded(XMD afterAdded) {
		this._afterAdded = afterAdded;
	}

	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getAfterRemovedToExist()
	 */
	@Override
	public XMD getAfterRemovedToExist() {
		return _afterRemovedToExist;
	}

	public void setAfterRemovedToExist(XMD afterRemovedToExist) {
		this._afterRemovedToExist = afterRemovedToExist;
	}

	/* (non-Javadoc)
	 * @see xrc.api.compare.engine.IXMDDifference#getAfterRemovedToNonExist()
	 */
	@Override
	public XMD getAfterRemovedToNonExist() {
		return _afterRemovedToNonExist;
	}

	public void setAfterRemovedToNonExist(XMD afterRemovedToNonExist) {
		this._afterRemovedToNonExist = afterRemovedToNonExist;
	}	
}
