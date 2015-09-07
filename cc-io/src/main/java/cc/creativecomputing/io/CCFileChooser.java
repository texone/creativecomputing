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
	
	public Path chosePath(final String theText) {
		setCurrentDirectory(_myCurrentDirectory.toFile());
		int myRetVal = showDialog(getParent(),theText);
		if (myRetVal == JFileChooser.APPROVE_OPTION) {
			try {
				Path myChoosenPath = getSelectedFile().toPath();
				CCLog.info(getSelectedFile());
				CCLog.info(myChoosenPath);
				if(_myExtension != null && getDialogType() != OPEN_DIALOG  && getFileSelectionMode() != DIRECTORIES_ONLY){
				
					String myExtension = CCNIOUtil.fileExtension(myChoosenPath);
					if(myExtension == null){
						myChoosenPath = myChoosenPath.getParent().resolve(CCNIOUtil.fileName(myChoosenPath) + "." + _myExtension);
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
