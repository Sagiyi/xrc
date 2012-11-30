package xrc.api.ide.eclipse.core.io.svn;

import org.apache.log4j.Logger;

import xrc.api.ide.eclipse.core.io.XMDProcessor;
import xrc.api.rc.FileInfo;
import xrc.api.rc.IFileInfo;
import xrc.api.rc.IRcFactory;
import xrc.config.ConfigManager;


public class SvnXMDProcessor extends XMDProcessor {
	
	private final static Logger _logger = Logger.getLogger(SvnXMDProcessor.class);

	private ConfigManager _configManager = ConfigManager.getInstance();
	private IRcFactory _rcFactory = _configManager.createRcFactory();
	
	//TODO: is this been called? should I change it to protected? (+in parent)
	protected CharSequence createUpdatedTarget(CharSequence target,IFileInfo fileInfo) {
		Comparable<?> targetRevisionId = fileInfo.getRevision();
		boolean isModified = fileInfo.isModified();
		Comparable<?> wcRevision = ((FileInfo)fileInfo).getWcRevision();
		StringBuilder newTaget = new StringBuilder(target);
		StringBuilder dirtyPrefix = new StringBuilder(isModified?"*":"");
//		CharSequence revision = targetRevisionId.toString();
		if(_logger.isDebugEnabled())
			_logger.debug("target is:\n" + target);
		int index = newTaget.indexOf("&");
		newTaget.insert(index, "["+ dirtyPrefix + //TODO: use append...
				(targetRevisionId!=null?targetRevisionId.toString():"undef")
				+"]");
		return newTaget;
	}
}
