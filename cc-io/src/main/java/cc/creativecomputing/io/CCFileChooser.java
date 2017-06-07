package cc.creativecomputing.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

import cc.creativecomputing.core.logging.CCLog;

public class CCFileChooser extends JFileChooser{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2598283666202397587L;

	private CCFileFilter _myFileFilter;
	
	private String _myExtension = null;
	
	public CCFileChooser(){
		super();
		_myCurrentDirectory = Paths.get(".");
	}
	
	public CCFileChooser(String theDescription, String...theExtensions){
		this();
		_myExtension = theExtensions.length == 1 ? theExtensions[0] : null;
		_myFileFilter = new CCFileFilter(theDescription, theExtensions);
		setFileFilter(_myFileFilter);
		
	}


	private Path _myCurrentDirectory;
	
	public int show(String theText){
		setCurrentDirectory(_myCurrentDirectory.toFile());
		return showDialog(getParent(),theText);
	}
	
	public Path path(){
		Path myChoosenPath = getSelectedFile().toPath();
		_myCurrentDirectory = myChoosenPath.getParent();
		return myChoosenPath;
	}
	
	public String extension(){
		if(getFileFilter() instanceof CCFileFilter){
			return getFileFilter().getDescription();
		}
		return null;
	}
	
	public void setDirectory(Path thePath){
		_myCurrentDirectory = thePath;
	}
	
	public Path chosePath(final String theText) {
		setCurrentDirectory(_myCurrentDirectory.toFile());
		int myRetVal = showDialog(getParent(),theText);
		if (myRetVal == JFileChooser.APPROVE_OPTION) {
			try {
				Path myChoosenPath = getSelectedFile().toPath();
				
				if(myChoosenPath.startsWith(CCNIOUtil.applicationPath)){
					myChoosenPath = CCNIOUtil.applicationPath.relativize(myChoosenPath);
				}
				String myExtension = _myExtension;
				if(getFileFilter() instanceof CCFileFilter){
					myExtension = getFileFilter().getDescription();
				}
				if(myExtension != null && getDialogType() != OPEN_DIALOG  && getFileSelectionMode() != DIRECTORIES_ONLY && !CCNIOUtil.exists(myChoosenPath)){
				
					String myEnteredExtension = CCNIOUtil.fileExtension(myChoosenPath);
					if(myEnteredExtension == null){
						myChoosenPath = myChoosenPath.resolveSibling(myChoosenPath.getFileName() + "." + myExtension);
					}
				}

				_myCurrentDirectory = myChoosenPath.getParent();
				
				return myChoosenPath;
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public void resetPath() {
		_myCurrentDirectory = Paths.get(".");
	}
	
	public static void main(String[] args) {
		CCFileChooser myChooser = new CCFileChooser();
		myChooser.addChoosableFileFilter(new CCFileFilter("xml", "xml"));
		myChooser.addChoosableFileFilter(new CCFileFilter("json", "json"));
		myChooser.setDialogTitle("YO YO");
		CCLog.info(myChooser.chosePath("texone"));
	}
}
