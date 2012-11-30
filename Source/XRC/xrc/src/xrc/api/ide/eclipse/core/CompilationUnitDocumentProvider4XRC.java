package xrc.api.ide.eclipse.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitAnnotationModelEvent;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider;
import org.eclipse.jdt.internal.ui.javaeditor.JavaMarkerAnnotation;
import org.eclipse.jdt.internal.ui.text.java.IProblemRequestorExtension;
import org.eclipse.jdt.internal.ui.text.spelling.JavaSpellingReconcileStrategy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.svn.core.connector.SVNConnectorException;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.ui.repository.RepositoryFileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import xrc.api.common.RcException;
import xrc.api.ide.IMarkersReader;
import xrc.api.ide.IMarkersWriter;
import xrc.api.ide.eclipse.core.dto.MarkersInfo;
import xrc.api.ide.eclipse.core.dto.XMD;
import xrc.api.ide.eclipse.core.io.MarkersInfoWriter;
import xrc.api.ide.eclipse.core.io.XMDReader;
import xrc.api.ide.eclipse.plugin.svn.SvnDecoratedRepositoryFileEditorInput;
import xrc.api.rc.IRcFactory;
import xrc.api.rc.IRcPropertyReader;
import xrc.api.rc.IRcPropertyWriter;
import xrc.config.ConfigManager;




public class CompilationUnitDocumentProvider4XRC extends CompilationUnitDocumentProvider{// implements ICompilationUnitDocumentProvider, IAnnotationModelFactory {

	private static ConfigManager _configManager = ConfigManager.getInstance();

	private final static Logger _logger = Logger.getLogger(CompilationUnitDocumentProvider4XRC.class);

	
	/**
	 * Annotation model dealing with java marker annotations and temporary problems.
	 * Also acts as problem requester for its compilation unit. Initially inactive. Must explicitly be
	 * activated.
	 */
	protected static class CompilationUnitAnnotationModel extends ResourceMarkerAnnotationModel implements IProblemRequestor, IProblemRequestorExtension, Serializable {
	
		private ConfigManager _configManager = ConfigManager.getInstance();
		private IRcFactory rcFactory = _configManager.createRcFactory();
		
		IMarker[] _markers;//Sagi 
		
		public void connect(IDocument document) {
			if(_logger.isDebugEnabled())
				_logger.debug("connect");
			super.connect(document);
		}
				
	
		//sagi
		public void connect(IDocument document, IRepositoryResource originalSvnRepositoryResource) {		
			IResource resource= this.getResource();
			try {				
				xrc.api.rc.IRepositoryResource repositoryResource = rcFactory.createRepositoryResource(originalSvnRepositoryResource);
				IRcPropertyReader<XMD> propertyReader = rcFactory.createPropertyReader(repositoryResource, XMD.class);
				IMarkersReader markersReader = new XMDReader(propertyReader );
				
				_markers = markersReader.read(resource);
				try {
					IMarker[] diffMarkers = resource.findMarkers("org.eclipse.ajdt.ui.changedadvicemarker", true, 0);//  "org.eclipse.ajdt.ui.beforeadvicemarker", true, 0);//"org.eclipse.ajdt.ui.changedadvicemarker", true, 0);
					IMarker[] diffMarkers2 = new IMarker[_markers.length+diffMarkers.length];
					int i = 0;
					for (; i < _markers.length; i++) {
						diffMarkers2[i] = _markers[i];
					}
					for (int j = 0; i < diffMarkers2.length; i++,j++) {
						diffMarkers2[i] = diffMarkers[j];
					}
					_markers = diffMarkers2;//TODO: add diffMarkers to _markers.
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				} catch (RcException e) {				
				e.printStackTrace();
			}
			super.connect(document);
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID= 1L;

		private static class ProblemRequestorState {
			boolean fInsideReportingSequence= false;
			List fReportedProblems;
		}

		private ThreadLocal fProblemRequestorState= new ThreadLocal();
		private int fStateCount= 0;

		private ICompilationUnit fCompilationUnit;
		private List fGeneratedAnnotations= new ArrayList();
		private IProgressMonitor fProgressMonitor;
		private boolean fIsActive= false;
		private boolean fIsHandlingTemporaryProblems;

		private ReverseMap fReverseMap= new ReverseMap();
		private List fPreviouslyOverlaid= null;
		private List fCurrentlyOverlaid= new ArrayList();
		private Thread fActiveThread;


		public CompilationUnitAnnotationModel(IResource resource) {
			super(resource);
		}
		
		//sagi
		private long _revision;
		public CompilationUnitAnnotationModel(IResource resource, long revision) {						
			super(resource);
			this._revision = revision;
		}

		public void setCompilationUnit(ICompilationUnit unit)  {
			fCompilationUnit= unit;
		}

		protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
			if (JavaMarkerAnnotation.isJavaAnnotation(marker))
				return new JavaMarkerAnnotation(marker);
			return super.createMarkerAnnotation(marker);
		}

		/*
		 * @see org.eclipse.jface.text.source.AnnotationModel#createAnnotationModelEvent()
		 */
		protected AnnotationModelEvent createAnnotationModelEvent() {
			return new CompilationUnitAnnotationModelEvent(this, getResource());
		}

		protected Position createPositionFromProblem(IProblem problem) {
			int start= problem.getSourceStart();
			int end= problem.getSourceEnd();

			if (start == -1 && end == -1)
				return new Position(0);

			if (start == -1)
				return new Position(end);

			if (end == -1)
				return new Position(start);

			int length= end - start + 1;
			if (length < 0)
				return null;

			return new Position(start, length);
		}

		/*
		 * @see IProblemRequestor#beginReporting()
		 */
		public void beginReporting() {
			ProblemRequestorState state= (ProblemRequestorState) fProblemRequestorState.get();
			if (state == null)
				internalBeginReporting(false);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.java.IProblemRequestorExtension#beginReportingSequence()
		 */
		public void beginReportingSequence() {
			ProblemRequestorState state= (ProblemRequestorState) fProblemRequestorState.get();
			if (state == null)
				internalBeginReporting(true);
		}

		/**
		 * Sets up the infrastructure necessary for problem reporting.
		 *
		 * @param insideReportingSequence <code>true</code> if this method
		 *            call is issued from inside a reporting sequence
		 */
		private void internalBeginReporting(boolean insideReportingSequence) {
			if (fCompilationUnit != null && fCompilationUnit.getJavaProject().isOnClasspath(fCompilationUnit)) {
				ProblemRequestorState state= new ProblemRequestorState();
				state.fInsideReportingSequence= insideReportingSequence;
				state.fReportedProblems= new ArrayList();
				synchronized (getLockObject()) {
					fProblemRequestorState.set(state);
					++fStateCount;
				}
			}
		}

		/*
		 * @see IProblemRequestor#acceptProblem(IProblem)
		 */
		public void acceptProblem(IProblem problem) {
			if (fIsHandlingTemporaryProblems || problem.getID() == JavaSpellingReconcileStrategy.SPELLING_PROBLEM_ID) {
				ProblemRequestorState state= (ProblemRequestorState) fProblemRequestorState.get();
				if (state != null)
					state.fReportedProblems.add(problem);
			}
		}

		/*
		 * @see IProblemRequestor#endReporting()
		 */
		public void endReporting() {
			ProblemRequestorState state= (ProblemRequestorState) fProblemRequestorState.get();
			if (state != null && !state.fInsideReportingSequence)
				internalEndReporting(state);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.java.IProblemRequestorExtension#endReportingSequence()
		 */
		public void endReportingSequence() {
			ProblemRequestorState state= (ProblemRequestorState) fProblemRequestorState.get();
			if (state != null && state.fInsideReportingSequence)
				internalEndReporting(state);
		}

		private void internalEndReporting(ProblemRequestorState state) {
			int stateCount= 0;
			synchronized(getLockObject()) {
				-- fStateCount;
				stateCount= fStateCount;
				fProblemRequestorState.set(null);
			}

			if (stateCount == 0)
				reportProblems(state.fReportedProblems);
		}

		/**
		 * Signals the end of problem reporting.
		 *
		 * @param reportedProblems the problems to report
		 */
		private void reportProblems(List reportedProblems) {
			if (fProgressMonitor != null && fProgressMonitor.isCanceled())
				return;

			boolean temporaryProblemsChanged= false;

			synchronized (getLockObject()) {

				boolean isCanceled= false;

				fPreviouslyOverlaid= fCurrentlyOverlaid;
				fCurrentlyOverlaid= new ArrayList();

				if (fGeneratedAnnotations.size() > 0) {
					temporaryProblemsChanged= true;
					removeAnnotations(fGeneratedAnnotations, false, true);
					fGeneratedAnnotations.clear();
				}

				if (reportedProblems != null && reportedProblems.size() > 0) {

					Iterator e= reportedProblems.iterator();
					while (e.hasNext()) {

						if (fProgressMonitor != null && fProgressMonitor.isCanceled()) {
							isCanceled= true;
							break;
						}

						IProblem problem= (IProblem) e.next();
						Position position= createPositionFromProblem(problem);
						if (position != null) {

							try {
								ProblemAnnotation annotation= new ProblemAnnotation(problem, fCompilationUnit);
								overlayMarkers(position, annotation);
								addAnnotation(annotation, position, false);
								fGeneratedAnnotations.add(annotation);

								temporaryProblemsChanged= true;
							} catch (BadLocationException x) {
								// ignore invalid position
							}
						}
					}
				}

				removeMarkerOverlays(isCanceled);
				fPreviouslyOverlaid= null;
			}

			if (temporaryProblemsChanged)
				fireModelChanged();
		}

		private void removeMarkerOverlays(boolean isCanceled) {
			if (isCanceled) {
				fCurrentlyOverlaid.addAll(fPreviouslyOverlaid);
			} else if (fPreviouslyOverlaid != null) {
				Iterator e= fPreviouslyOverlaid.iterator();
				while (e.hasNext()) {
					JavaMarkerAnnotation annotation= (JavaMarkerAnnotation) e.next();
					annotation.setOverlay(null);
				}
			}
		}

		/**
		 * Overlays value with problem annotation.
		 * 
		 * @param value the value
		 * @param problemAnnotation the problem annotation
		 */
		private void setOverlay(Object value, ProblemAnnotation problemAnnotation) {
			if (value instanceof  JavaMarkerAnnotation) {
				JavaMarkerAnnotation annotation= (JavaMarkerAnnotation) value;
				if (annotation.isProblem()) {
					annotation.setOverlay(problemAnnotation);
					fPreviouslyOverlaid.remove(annotation);
					fCurrentlyOverlaid.add(annotation);
				}
			} else {
			}
		}

		private void  overlayMarkers(Position position, ProblemAnnotation problemAnnotation) {
			Object value= getAnnotations(position);
			if (value instanceof List) {
				List list= (List) value;
				for (Iterator e = list.iterator(); e.hasNext();)
					setOverlay(e.next(), problemAnnotation);
			} else {
				setOverlay(value, problemAnnotation);
			}
		}

		/**
		 * Tells this annotation model to collect temporary problems from now on.
		 */
		private void startCollectingProblems() {
			fGeneratedAnnotations.clear();
		}

		/**
		 * Tells this annotation model to no longer collect temporary problems.
		 */
		private void stopCollectingProblems() {
			if (fGeneratedAnnotations != null)
				removeAnnotations(fGeneratedAnnotations, true, true);
			fGeneratedAnnotations.clear();
		}

		/*
		 * @see IProblemRequestor#isActive()
		 */
		public synchronized boolean isActive() {
			return fIsActive && fActiveThread == Thread.currentThread();
		}

		/*
		 * @see IProblemRequestorExtension#setProgressMonitor(IProgressMonitor)
		 */
		public void setProgressMonitor(IProgressMonitor monitor) {
			fProgressMonitor= monitor;
		}

		/*
		 * @see IProblemRequestorExtension#setIsActive(boolean)
		 */
		public synchronized void setIsActive(boolean isActive) {
			Assert.isLegal(!isActive || Display.getCurrent() == null); // must not be enabled from UI threads
			fIsActive= isActive;
			if (fIsActive)
				fActiveThread= Thread.currentThread();
			else
				fActiveThread= null;
		}

		/*
		 * @see IProblemRequestorExtension#setIsHandlingTemporaryProblems(boolean)
		 * @since 3.1
		 */
		public void setIsHandlingTemporaryProblems(boolean enable) {
			if (fIsHandlingTemporaryProblems != enable) {
				fIsHandlingTemporaryProblems= enable;
				if (fIsHandlingTemporaryProblems)
					startCollectingProblems();
				else
					stopCollectingProblems();
			}

		}

		private Object getAnnotations(Position position) {
			synchronized (getLockObject()) {
				return fReverseMap.get(position);
			}
		}

		/*
		 * @see AnnotationModel#addAnnotation(Annotation, Position, boolean)
		 */
		protected void addAnnotation(Annotation annotation, Position position, boolean fireModelChanged) throws BadLocationException {
			super.addAnnotation(annotation, position, fireModelChanged);

			synchronized (getLockObject()) {
				Object cached= fReverseMap.get(position);
				if (cached == null)
					fReverseMap.put(position, annotation);
				else if (cached instanceof List) {
					List list= (List) cached;
					list.add(annotation);
				} else if (cached instanceof Annotation) {
					List list= new ArrayList(2);
					list.add(cached);
					list.add(annotation);
					fReverseMap.put(position, list);
				}
			}
		}

		/*
		 * @see AnnotationModel#removeAllAnnotations(boolean)
		 */
		protected void removeAllAnnotations(boolean fireModelChanged) {
			super.removeAllAnnotations(fireModelChanged);
			synchronized (getLockObject()) {
				fReverseMap.clear();
			}
		}

		/*
		 * @see AnnotationModel#removeAnnotation(Annotation, boolean)
		 */
		protected void removeAnnotation(Annotation annotation, boolean fireModelChanged) {
			Position position= getPosition(annotation);
			synchronized (getLockObject()) {
				Object cached= fReverseMap.get(position);
				if (cached instanceof List) {
					List list= (List) cached;
					list.remove(annotation);
					if (list.size() == 1) {
						fReverseMap.put(position, list.get(0));
						list.clear();
					}
				} else if (cached instanceof Annotation) {
					fReverseMap.remove(position);
				}
			}
			super.removeAnnotation(annotation, fireModelChanged);
		}
		
		/** Sagi
		 * 
		 */
		public void commit(IDocument document) throws CoreException {
			super.commit(document);
			saveToFile(this);
		}
		
		/**Sagi
		 * 
		 */
		protected IMarker[] retrieveMarkers() throws CoreException {
			if(_revision!=0){
				return _markers;  
			}else{
				return super.retrieveMarkers();
			}
		}
		
				
		/** Sagi
		 * 
		 */
		private void saveToFile(CompilationUnitAnnotationModel compilationUnitAnnotationModel){
			IResource resource= compilationUnitAnnotationModel.getResource();
			
			String localFilename = resource.getLocation().toString();
//			IRcPropertyWriter<MarkersInfo> propertyWriter = new PropertyWriter<MarkersInfo>(localFilename);
//			IMarkersWriter markersWriter = new MarkersInfoWriter(propertyWriter);
			
//			IRcFactory rcFactory = _configManager.createRcFactory();
			IRcPropertyWriter<MarkersInfo> propertyWriter;
			try {
				propertyWriter = rcFactory.createPropertyWriter(localFilename, MarkersInfo.class);
			
			IMarkersWriter markersWriter = new MarkersInfoWriter(propertyWriter);
//			RelationshipsInfoWriter = new ???TODO
//			MarkerStreamer ms = new MarkerStreamer();
			
				markersWriter.write(resource);	//its ok, it write aspecj: property, not the one use with the details... that the listener writes
				AdviceListenerManager adviceListenerManager = AdviceListenerManager.getInstance();
//				IAJBuildListener listener = new AJBuildListener(resource);
//				AJBuilder.addAJBuildListener(listener);
				adviceListenerManager.addListener(resource);
				
			} catch (RcException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
	}
	
	/**
	 * Constructor
	 */
	public CompilationUnitDocumentProvider4XRC() {
		super();		
		JavaPlugin.getDefault().setCompilationUnitDocumentProvider(this);
	}

	/*
	 * @see org.eclipse.core.filebuffers.IAnnotationModelFactory#createAnnotationModel(org.eclipse.core.runtime.IPath)
	 * @since 3.4
	 */
	public IAnnotationModel createAnnotationModel(IPath path) {
		IResource file= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (file instanceof IFile)
			return new CompilationUnitAnnotationModel(file);//AARC class, that's the reason for overloading
		return new AnnotationModel();
	}
	
	/**
	 * sagi TODO: consider managing (create/delete) IResouce (probably File) for managing the markers.
	 * 		remember where the temp marker files are located and put it there.
	 * 		IPath markersLocation = workspace.getMetaArea().getMarkersLocationFor(root);
	 * TODO: probably hooking to the process that close the eclipse, and there delete the temp project is the best simple cleanup.
	 * Consider space (quota) checking
	 * Note: I considered also path.getWorkspace().getWorking... but there I can't reuse the eclipse management of resources as markers. 
	 * @param path
	 * @param revision
	 * @param useDiffMarkers 
	 * @return
	 * @throws CoreException
	 */
	public static IAnnotationModel createAnnotationModel(IFile path, int revision, boolean useDiffMarkers) throws CoreException {

		String relativeFilename = constructRelativeFilename(path, revision, useDiffMarkers);
		IFile tempRevisionDecorationFile = createTempMarkersResource(path, relativeFilename);		
		return new CompilationUnitAnnotationModel(tempRevisionDecorationFile, revision);
	}

	public static String constructRelativeFilename(IFile path, int revision, boolean useDiffMarkers){
		String relativeFilename = path.getFullPath().toString();
		relativeFilename = relativeFilename.substring(0, relativeFilename.lastIndexOf("."));
		relativeFilename = relativeFilename.substring(relativeFilename.lastIndexOf("/"));		
		relativeFilename = relativeFilename + "_rev_" + revision+ (useDiffMarkers?"diff":"")+ ".temp";//TODO...
		return relativeFilename;
	}
	
	public static IFile createTempMarkersResource(IFile path, String relativeFilename) throws CoreException{
		IProject tempProject4Revisions = path.getWorkspace().getRoot().getProject("tempProjectForRevisionDecorateions");
		if(!tempProject4Revisions.exists()){
			tempProject4Revisions.create(null);
		};
		tempProject4Revisions.open(null);
		tempProject4Revisions.setHidden(true);
		
		IFile tempRevisionDecorationFile = tempProject4Revisions.getFile(relativeFilename);
		if(!tempRevisionDecorationFile.exists()){		
			tempRevisionDecorationFile.create(path.getContents(), IResource.FORCE | IResource.DERIVED | IResource.TEAM_PRIVATE | IResource.HIDDEN , null);
		}
		return tempRevisionDecorationFile;
	}

	//TODO: see in aarc.temp, maybe should fix or use some ideas.
	public void connect(Object element) throws CoreException {
		super.connect(element);
		if (getFileInfo(element) != null)
			return;
		if (element instanceof RepositoryFileEditorInput){//TODO: test without the ||, and remove if working..   
			//			|| element instanceof aarc.eclipse.plugin.svn.SvnDecoratedRepositoryFileEditorInput) {
			_model = new AnnotationModel();
			long revision = -1;
			String name = "Failure";// will be something like Dog.java...
			String pathString = "Failure";
			try {//TODO: Sagi handle also element of type RepositoryFileEditorInput	was (DecoratedRepositoryFileEditorInput)				

				IRepositoryResource repResource= (((RepositoryFileEditorInput)element)).getRepositoryResource();
				name = repResource.getName();
				revision = ((RepositoryFileEditorInput)element).getRepositoryResource().getRevision();
				String fullUrlString = repResource.getUrl();
				String repUrlString = repResource.getRepositoryLocation().getUrl();
				pathString = fullUrlString.substring(repUrlString.length());

				boolean el = ResourcesPlugin.getWorkspace().getRoot().exists(new Path("/thesis.delivery.test.aspectj.simplestexample/src/thesis/delivery/test/aspectj/simplestexample/Dog.java"));
//				IFile ll = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("/thesis.delivery.test.aspectj.simplestexample/src/thesis/delivery/test/aspectj/simplestexample/Dog.java"));
								IFile ll = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
								ll = findEclipseResource(repResource);
				boolean useDiffMarkers = false;
				if(element instanceof SvnDecoratedRepositoryFileEditorInput){
					useDiffMarkers = true;
				}
				_model = createAnnotationModel(ll, (int)revision, useDiffMarkers);
					
				name = ll.getFullPath().toString();
				IDocument document = getParentProvider().getDocument(element);
				((CompilationUnitAnnotationModel)_model).connect(document, repResource);//name, revision);//getDocument(element));
			} catch (SVNConnectorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//New!!!Sagi - to handle cases where the project is not in the root of the SVN
	//need to be merged with the same method in AspectJStructureDiffViewer, now it's different IRepositoryResource argument in each
		public static IFile findEclipseResource(IRepositoryResource repResource){
//			String name = repResource.getName();
			String fullUrlString = repResource.getUrl();
			String repUrlString = repResource.getRepositoryLocation().getUrl();
			String pathString = fullUrlString.substring(repUrlString.length());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
			if(file.exists()){
				return file;
			}
			while (pathString.contains("\\") || pathString.contains("/")){ //both seperators can be use by SVN.
				int sepIndex = Math.max(pathString.indexOf("\\"), pathString.indexOf("/"));
				pathString = pathString.substring(sepIndex+1);
				_logger.warn("findEclipseResource: pathString=" + pathString);
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
				if(file.exists()){
					return file;
				}
			} //TODO: improve. not efficient but rare to enter to this loop.
			_logger.error("resource not found");
			return null;
		}
		
	IAnnotationModel _model;
	/*
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#getAnnotationModel(java.lang.Object)
	 * @since 3.2
	 */
	public IAnnotationModel getAnnotationModel(Object element) {
		IAnnotationModel model= super.getAnnotationModel(element);
		
		if (element instanceof RepositoryFileEditorInput){//TODO: test without the ||, and remove if working..    || element instanceof aarc.eclipse.plugin.svn.SvnDecoratedRepositoryFileEditorInput) {
			RepositoryFileEditorInput new_name = (RepositoryFileEditorInput) element;
			return _model;
		}
		if (model != null)
			return model;
			
		return null;
	}

}

