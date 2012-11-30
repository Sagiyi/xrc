package xrc.api.ide.eclipse.plugin.svn;

import org.eclipse.compare.ISharedDocumentAdapter;
import org.eclipse.compare.SharedDocumentAdapter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.text.IDocument;
import org.eclipse.team.svn.core.connector.SVNConnectorException;
import org.eclipse.team.svn.core.resource.IRepositoryFile;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.ui.compare.ResourceCompareInput.ResourceElement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * TODO: low-priority: consider extending TextFileDocumentProvider 
 * 		 and override method createFileInfo(Object). [I removed a commented code that did it, I probably thought it's a good way to achieve something) - it (the class) was called (in the comments) DecoratedTextFileDocumentProvider]
 * 		 I also removed commented class (with constructor only): DecoratedRepositoryFileEditorInput extends RepositoryFileEditorInput
 * 		 low-priority: understand the evolution of this class
 * 			previously in getAdapter: return new SvnSharedDocumentAdapter((ResourceCompareInput.ResourceElement)adaptableObject);
 * 			and SvnSharedDocumentAdapter had ResourceElement member (init in constructor)
 * 			was: PROPERTIES = new Class[]{ResourceElement.class};
 * 			was in getDocumentKey: IFile file = getFile(element);... (see parent to understand)
 * 		high-priority: if there are problems it might be super() that I added to constructor. remove it, or this comment-line.
 * 		medium-priority: move the inner class outside and verify it works.
 * 		medium-low priority: doc the way it works, and why we need this.
 * 			note: It was in package org.eclipse.compare;
 * 
 * @author Sagi Ifrah
 *
 */
public class SvnSharedDocumentAdapterFactory implements IAdapterFactory {
	
	private static Class<?>[] PROPERTIES= new Class[]{ISharedDocumentAdapter.class};
		
	public class SvnSharedDocumentAdapter extends SharedDocumentAdapter{
		public SvnSharedDocumentAdapter(){
			super();
		}

		public void flushDocument(IDocumentProvider provider,
				IEditorInput documentKey, IDocument document, boolean overwrite)
				throws CoreException {
			// TODO Auto-generated method stub			
		}
		
		public IEditorInput getDocumentKey(Object element) {
			IRepositoryResource file = ((ResourceElement)element).getRepositoryResource();
			try {
				if(file != null  && file.exists() && file instanceof IRepositoryFile){
					return new SvnDecoratedRepositoryFileEditorInput((IRepositoryFile) file );
				}
			} catch (SVNConnectorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	//note: Can't use generic Class<?> since need to override with same method signature
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if(adaptableObject!= null && 
				adapterType == ISharedDocumentAdapter.class){
			return new SvnSharedDocumentAdapter();//(ResourceCompareInput.ResourceElement)adaptableObject);
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return PROPERTIES;
	}

}
