package xrc.api.ide.eclipse.plugin.svn;

import org.eclipse.team.svn.core.resource.IRepositoryFile;
import org.eclipse.team.svn.ui.repository.RepositoryFileEditorInput;
/**
 * TODO: medium-low priority: doc the way it works, and why we need this.
 * 		note: It was in package org.eclipse.compare;
 * @author Sagi Ifrah
 *
 */
public class SvnDecoratedRepositoryFileEditorInput extends RepositoryFileEditorInput{

	public SvnDecoratedRepositoryFileEditorInput(IRepositoryFile resource) {
		super(resource);	
	}		
}
