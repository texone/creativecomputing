package cc.creativecomputing.controlui.controls.code;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.parser.ExtendedHyperlinkListener;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RTextScrollPane;

import cc.creativecomputing.control.code.CCShaderObject;
import cc.creativecomputing.control.handles.CCShaderCompileHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.controls.CCValueControl;

public class CCShaderCompileControl extends CCValueControl<CCShaderObject, CCShaderCompileHandle>{
	
	private class CCRealtimeCompileParser implements Parser{

		@Override
		public ExtendedHyperlinkListener getHyperlinkListener() {
			// TODO Auto-generated method stub
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

	private JButton _myButton;
	private JButton _myResetButton;
	
	private JFrame _myEditorFrame;
	
	private JPanel _myContainer;
	
	private final RSyntaxTextArea _myTextArea;
	private final JTextArea _myErrorArea;
	
	private boolean _myTriggerEvent = true;

	public CCShaderCompileControl(CCShaderCompileHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		_myEditorFrame = new JFrame();
		JSplitPane mySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);

		_myTextArea = new RSyntaxTextArea(20, 60);
		_myTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		_myTextArea.setCodeFoldingEnabled(true);
		_myTextArea.setText(_myHandle.value().sourceCode());
		_myTextArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value().sourceCode(_myTextArea.getText());
				_myErrorArea.setText(_myHandle.value().errorLog());
			}
				
			@Override
			public void insertUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value().sourceCode(_myTextArea.getText());
				_myErrorArea.setText(_myHandle.value().errorLog());
			}
				
			@Override
			public void changedUpdate(DocumentEvent theE) {
			}
		});
		_myTextArea.addParser(new CCRealtimeCompileParser());
		RTextScrollPane sp = new RTextScrollPane(_myTextArea);
		mySplitPane.setTopComponent(sp);
		mySplitPane.setDividerLocation(500);
		
		_myErrorArea = new JTextArea();
		_myErrorArea.setPreferredSize(_myTextArea.getPreferredSize());
		JScrollPane sp2 = new JScrollPane(_myErrorArea);
		mySplitPane.setBottomComponent(sp2);
	      

		_myEditorFrame.setContentPane(mySplitPane);
		_myEditorFrame.setTitle(theHandle.path().toString());
		_myEditorFrame.pack();
		_myEditorFrame.setLocationRelativeTo(null);
		
		_myEditorFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
			}
		});
		
		theHandle.events().add(theValue -> {
			_myTriggerEvent = false;
			_myTextArea.setText(((CCShaderObject)theValue).sourceCode());
			_myTriggerEvent = true;

		});
		
		theHandle.value().events().add(theCompiler -> {
			_myErrorArea.setText(_myHandle.value().errorLog());
		});
 
        //Create the Button.
		
		_myContainer = new JPanel();
        ((FlowLayout)_myContainer.getLayout()).setVgap(0);
        ((FlowLayout)_myContainer.getLayout()).setHgap(0);
       
        _myButton = new JButton("edit");
        _myButton.addActionListener(theE -> {
        	_myEditorFrame.setVisible(true);
		});
        CCUIStyler.styleButton(_myButton, 30, 15);
        _myContainer.add(_myButton);
        
        _myResetButton = new JButton("reset");
        _myResetButton.addActionListener(theE -> {
        	_myTextArea.setText(_myHandle.value().sourceCode());
		});
        CCUIStyler.styleButton(_myResetButton, 30, 15);
        _myContainer.add(_myResetButton);
	}
	
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5,  5, 1, 5));
		thePanel.add(_myContainer, constraints(1, theY, 2, GridBagConstraints.LINE_START, 5, 15, 1, 5));
	}

	@Override
	public CCShaderObject value() {
		// TODO Auto-generated method stub
		return _myHandle.value();
	}
}
