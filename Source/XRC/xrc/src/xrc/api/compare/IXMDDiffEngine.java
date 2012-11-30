package xrc.api.compare;

import xrc.api.common.RcException;
import xrc.api.ide.eclipse.core.dto.XMD;

/**
 * The IRelationshipsInfoDiffEngine is responsible for comparing two RelationshipsInfo objects,
 * and create an IRelationshipsInfoDifference - object that represents the difference 
 * @author Sagi Ifrah
 *
 */
public interface IXMDDiffEngine {

	public abstract IXMDDifference diff(
			XMD beforeRelationshipsInfo,
			XMD afterRelationshipsInfo) throws RcException;

}