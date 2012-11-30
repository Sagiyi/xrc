package xrc.api.ide;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

import xrc.api.common.RcException;


public interface IMarkersReader {

	public abstract IMarker[] read(IResource resource) throws RcException;

}