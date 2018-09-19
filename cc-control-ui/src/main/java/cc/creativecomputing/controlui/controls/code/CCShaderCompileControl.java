package cc.creativecomputing.controlui.controls.code;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.parser.ExtendedHyperlinkListener;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RTextScrollPane;

import cc.creativecomputing.control.code.CCShaderSource;
import cc.creativecomputing.control.handles.CCShaderSourceHandle;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.controls.CCValueControl;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderCompileControl extends CCValueControl<CCShaderSource, CCShaderSourceHandle>{
	
	private class CCRealtimeCompileParser implements Parser{

		@Override
		public ExtendedHyperlinkListener getHyperlinkListener() {
			return null;
		}

		@Override
		public URL getImageBase() {
			return null;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public ParseResult parse(RSyntaxDocument theArg0, String theArg1) {
//			try {
////				CCLog.info(theArg0.getText(0, theArg0.getLength()));
//			} catch (BadLocationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			for(Diagnostic<?> diagnostic:_myHandle.value().diagnostics()){
//
//			}
//			CCLog.info(theArg0 + ":" + theArg1);
			return null;
		}
		
	}
	
	/**
	 * Create a simple provider that adds some Java-related completions.
	 */
	private CompletionProvider createCompletionProvider(String[] theKeywords) {

		// A DefaultCompletionProvider is the simplest concrete implementation
		// of CompletionProvider. This provider has no understanding of
		// language semantics. It simply checks the text entered up to the
		// caret position for a match against known completions. This is all
		// that is needed in the majority of cases.
		DefaultCompletionProvider provider = new DefaultCompletionProvider();

		// Add completions for all Java keywords. A BasicCompletion is just
		// a straightforward word completion.
		for(String myKey:theKeywords){
			provider.addCompletion(new BasicCompletion(provider, myKey));
		}
	      // Add a couple of "shorthand" completions. These completions don't
	      // require the input text to be the same thing as the replacement text.
	      provider.addCompletion(new ShorthandCompletion(provider, "sysout",
	            "System.out.println(", "System.out.println("));
	      provider.addCompletion(new ShorthandCompletion(provider, "syserr",
	            "System.err.println(", "System.err.println("));

	      return provider;

	   }

	private JButton _myButton;
	private JButton _myResetButton;
	
	private JFrame _myEditorFrame;
	private JSplitPane _mySplitPane;
	
	private JPanel _myContainer;
	
	private final RSyntaxTextArea _myTextArea;
	private final JTextArea _myErrorArea;
	
	private boolean _myTriggerEvent = true;

	public CCShaderCompileControl(CCShaderSourceHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		_myEditorFrame = new JFrame();
		_mySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);

		_myTextArea = new RSyntaxTextArea(20, 60);
		_myTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		_myTextArea.setCodeFoldingEnabled(true);
		_myTextArea.setText(_myHandle.value().sourceCode());
		_myTextArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent theE) {
				saveText();
			}
				
			@Override
			public void insertUpdate(DocumentEvent theE) {
				saveText();
			}
				
			@Override
			public void changedUpdate(DocumentEvent theE) {
			}
		});
		_myTextArea.addParser(new CCRealtimeCompileParser());
		
	 	setFont(_myTextArea, new Font("Courier New", Font.PLAIN, 18));
		
		addTemplates();
		
		// An AutoCompletion acts as a "middle-man" between a text component
		// and a CompletionProvider. It manages any options associated with
		// the auto-completion (the popup trigger key, whether to display a
		// documentation window along with completion choices, etc.). Unlike
		// CompletionProviders, instances of AutoCompletion cannot be shared
		// among multiple text components.
		AutoCompletion ac = new AutoCompletion(createCompletionProvider(theHandle.value().object().keywords()));
		ac.install(_myTextArea);
		
		RTextScrollPane sp = new RTextScrollPane(_myTextArea);
		_mySplitPane.setTopComponent(sp);
		_mySplitPane.setDividerLocation(500);
		
		_myErrorArea = new JTextArea();
		_myErrorArea.setPreferredSize(_myTextArea.getPreferredSize());
		JScrollPane sp2 = new JScrollPane(_myErrorArea);
		_mySplitPane.setBottomComponent(sp2);
		
		_myHandle.value().object().onError().add(object -> {
			_myErrorArea.setText(object.errorLog());
		});
		_myHandle.value().object().onCompile().add(object -> {
			_myErrorArea.setText("");
		});
	      
		_myTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.isMetaDown() && e.getKeyCode() == KeyEvent.VK_S && _myHandle.value().object().saveInFile()){
					_myHandle.value().save();
				}
			}
		});

		_myEditorFrame.setContentPane(_mySplitPane);
		_myEditorFrame.setTitle(theHandle.path().toString());
		_myEditorFrame.pack();
		_myEditorFrame.setLocationRelativeTo(null);
		
		if(CCControlApp.preferences.getInt(_myHandle.path().toString() + "/x", -1) != -1){
			CCLog.info(CCControlApp.preferences.getInt(_myHandle.path().toString() + "/x", -1), CCControlApp.preferences.getInt(_myHandle.path().toString() + "/y", -1));
			_myEditorFrame.setLocation(
				CCControlApp.preferences.getInt(_myHandle.path().toString() + "/x", -1), 
				CCControlApp.preferences.getInt(_myHandle.path().toString() + "/y", -1)
			);
			_myEditorFrame.setSize(
				CCControlApp.preferences.getInt(_myHandle.path().toString() + "/width", -1), 
				CCControlApp.preferences.getInt(_myHandle.path().toString() + "/height", -1)
			);
			_mySplitPane.setDividerLocation(CCControlApp.preferences.getInt(_myHandle.path().toString() + "/devider", -1));
		}
//		_myEditorFrame.setVisible(CCControlApp.preferences.getBoolean(_myHandle.path().toString() + "/open" , false));
		
		_myEditorFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				CCControlApp.preferences.put(_myHandle.path().toString() + "/open" , true + "");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				CCControlApp.preferences.put(_myHandle.path().toString() + "/open" , false + "");
			}
		});
		
		_myEditorFrame.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				saveWindowPosition();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				saveWindowPosition();
			}
			
		});
		
		theHandle.events().add(theValue -> {
			_myTriggerEvent = false;
			_myTextArea.setText(((CCShaderSource)theValue).sourceCode());
			_myTriggerEvent = true;

		});
		
//		theHandle.value().events().add(theCompiler -> {
//			_myErrorArea.setText(_myHandle.value().errorLog());
//		});
// 
        //Create the Button.
		
		_myContainer = new JPanel();
        ((FlowLayout)_myContainer.getLayout()).setVgap(0);
        ((FlowLayout)_myContainer.getLayout()).setHgap(10);
        _myContainer.setBackground(new Color(0,0,0,0));
       
        _myButton = new JButton("edit");
        _myButton.addActionListener(theE -> {
        	_myEditorFrame.setVisible(true);
		});
        CCUIStyler.styleButton(_myButton, 30, 13);
        _myContainer.add(_myButton);
        
        _myResetButton = new JButton("reset");
        _myResetButton.addActionListener(theE -> {
        	_myTextArea.setText(_myHandle.value().sourceCode());
		});
        CCUIStyler.styleButton(_myResetButton, 30, 13);
        _myContainer.add(_myResetButton);
	}
	
	
	private void saveWindowPosition(){
//		CCControlApp.preferences.put(_myHandle.path().toString() + "/x" , _myEditorFrame.getX() + "");
//		CCControlApp.preferences.put(_myHandle.path().toString() + "/y" , _myEditorFrame.getY() + "");
//		CCControlApp.preferences.put(_myHandle.path().toString() + "/width" , _myEditorFrame.getWidth() + "");
//		CCControlApp.preferences.put(_myHandle.path().toString() + "/height" , _myEditorFrame.getHeight() + "");
//		CCControlApp.preferences.put(_myHandle.path().toString() + "/devider" , _mySplitPane.getDividerLocation() + "");
	}
	
	private void saveText(){
		if(!_myTriggerEvent)return;
		_myHandle.value().sourceCode(_myTextArea.getText());
		if(_myHandle.value().object().saveInFile() && _myHandle.value().object().autoSave()){
			_myHandle.value().save();
		}
	}
	
	private void addTemplates(){
		JMenu myTemplateMenue = new JMenu("templates");
		Map<Path, JMenu> myMenuMap = new HashMap<>();
		JMenu myFolder = myTemplateMenue;
		for(Path myPath:_myHandle.value().object().templates()){
			myFolder = myTemplateMenue;
			for(int i = 0; i < myPath.getNameCount() - 1; i++){
				Path myName = myPath.getName(i);
				if(!myMenuMap.containsKey(myName)){
					JMenu mySubFolder = new JMenu(myName.toString());
					myFolder.add(mySubFolder);
					myMenuMap.put(myName, mySubFolder);
				}
				myFolder = myMenuMap.get(myName);
			}
			String myExtension = CCNIOUtil.fileExtension(myPath);
			if(myExtension != null){
				JMenuItem myItem = new JMenuItem(myPath.getFileName().toString().replace(".glsl", ""));
				myItem.addActionListener(e -> {
					int myPosition = _myTextArea.getCaretPosition();
					_myTextArea.insert(_myHandle.value().object().templateSource(myPath) + "\\\\n", myPosition);
				});
				myFolder.add(myItem);
			}
			
		}
		_myTextArea.getPopupMenu().add(myTemplateMenue);
	}
	/**
	 * Set the font for all token types.
	 * 
	 * @param textArea The text area to modify.
	 * @param font The font to use.
	 */
	public void setFont(RSyntaxTextArea textArea, Font font) {
		if (font != null) {
			SyntaxScheme ss = textArea.getSyntaxScheme();
			ss = (SyntaxScheme) ss.clone();
			for (int i = 0; i < ss.getStyleCount(); i++) {
				if (ss.getStyle(i) != null) {
					ss.getStyle(i).font = font;
	            }
	         }
	         textArea.setSyntaxScheme(ss);
	         textArea.setFont(font);
		}
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5,  5, 1, 5));
		thePanel.add(_myContainer, constraints(1, theY, 2, GridBagConstraints.LINE_START, 5, 15, 1, 5));
	}

	@Override
	public CCShaderSource value() {
		return _myHandle.value();
	}
}
