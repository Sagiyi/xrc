package xrc.api.compare;

import xrc.api.ide.eclipse.core.dto.XMD;

/**
 * RelationshipsInfoDifference is a DTO that contains all the difference between two RelationshipsInfo objects.
 * each RelationshipsInfo contains only the difference (see {@link XMDDiffEngine})
 * 
 * see also {@link DiffType}
 */
public interface IXMDDifference {

	public abstract XMD getBefore();
	
	public abstract XMD getBeforeRemoved();

	public abstract XMD getBeforeAddedToExist();

	public abstract XMD getBeforeAddedToNonExist();

	public abstract XMD getAfter();
	
	public abstract XMD getAfterAdded();

	public abstract XMD getAfterRemovedToExist();

	public abstract XMD getAfterRemovedToNonExist();

}