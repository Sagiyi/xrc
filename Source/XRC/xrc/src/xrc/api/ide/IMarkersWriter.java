package xrc.api.ide;

import org.eclipse.core.resources.IResource;

import xrc.api.common.RcException;


public interface IMarkersWriter {

	public abstract boolean write(IResource resource) throws RcException;

}