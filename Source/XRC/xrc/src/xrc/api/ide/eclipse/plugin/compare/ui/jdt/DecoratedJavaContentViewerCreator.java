package xrc.api.ide.eclipse.plugin.compare.ui.jdt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.jface.viewers.Viewer;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;


/**
 * Required when creating a DecoratedJavaMergeViewer from the plugin.xml file.
 */
public class DecoratedJavaContentViewerCreator implements IViewerCreator {

	public Viewer createViewer(Composite parent, CompareConfiguration mp) {
		return new DecoratedJavaMergeViewer(parent, SWT.NULL, mp);
	}
}
