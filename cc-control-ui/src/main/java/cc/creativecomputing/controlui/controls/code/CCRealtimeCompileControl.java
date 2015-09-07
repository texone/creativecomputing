package cc.creativecomputing.controlui.controls.code;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.tools.Diagnostic;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.parser.ExtendedHyperlinkListener;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RTextScrollPane;

import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.control.code.CCRealtimeCompile.CCRealtimeCompileListener;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.handles.CCRealtimeCompileHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.controls.CCValueControl;
import cc.creativecomputing.core.logging.CCLog;

public class CCRealtimeCompileControl extends CCValueControl<CCRealtimeCompile<?>, CCRealtimeCompileHandle>{
	
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
			for(Diagnostic<?> diagnostic:_myHandle.value().diagnostics()){

			}
//			CCLog.info(theArg0 + ":" + theArg1);
			return null;
		}
		
	}

	private JButton _myButton;
	private JButton _myResetButton;
	
	private JFrame _myEditorFrame;
	
	private JPanel _myContainer;
	
	private String _myCurveKey = null;
	
	private final RSyntaxTextArea _myTextArea;
	private final JTextArea _myErrorArea;
	
	private boolean _myTriggerEvent = true;

	public CCRealtimeCompileControl(CCRealtimeCompileHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(new CCPropertyListener<CCRealtimeCompile>() {
			
			@Override
			public void onChange(CCRealtimeCompile theValue) {
				_myTriggerEvent = false;
				if(theValue.sourceCode().trim().equals("")){
					_myTextArea.setText(_myHandle.value().codeTemplate());
				}else{
					CCLog.info(theValue.sourceCode());
					_myTextArea.setText(theValue.sourceCode());
				}
				_myTriggerEvent = true;
			}
		});
		
		theHandle.value().events().add(new CCRealtimeCompileListener() {

			@Override
			public void onRecompile(CCRealtimeCompile theCompiler) {
				_myErrorArea.setText(errorLog());
			}
		});
		
		_myEditorFrame = new JFrame();
		JPanel cp = new JPanel(new BorderLayout());

		_myTextArea = new RSyntaxTextArea(20, 60);
		_myTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		_myTextArea.setCodeFoldingEnabled(true);
		_myTextArea.setText(_myHandle.value().codeTemplate());
		_myTextArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value().sourceCode(_myTextArea.getText());
			}
				
			@Override
			public void insertUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value().sourceCode(_myTextArea.getText());
			}
				
			@Override
			public void changedUpdate(DocumentEvent theE) {
			}
		});
		_myTextArea.addParser(new CCRealtimeCompileParser());
		RTextScrollPane sp = new RTextScrollPane(_myTextArea);
		cp.add(sp, BorderLayout.NORTH);
		
		_myErrorArea = new JTextArea();
		_myErrorArea.setPreferredSize(_myTextArea.getPreferredSize());
		JScrollPane sp2 = new JScrollPane(_myErrorArea);
		cp.add(sp2, BorderLayout.CENTER);
	      

		_myEditorFrame.setContentPane(cp);
		_myEditorFrame.setTitle("Text Editor Demo");
		_myEditorFrame.pack();
		_myEditorFrame.setLocationRelativeTo(null);
		
		_myEditorFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
			}
		});
 
        //Create the Button.
		
		_myContainer = new JPanel();
        ((FlowLayout)_myContainer.getLayout()).setVgap(0);
        ((FlowLayout)_myContainer.getLayout()).setHgap(0);
       
        
        
        _myButton = new JButton("edit");
        _myButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
//				if(_myCurveKey == null){
//					_myCurveFrame.track().trackData(new TrackData(null));
//				}else{
//					_myCurveFrame.track().trackData(value().curves().get(_myCurveKey));
//				}
//
//				_myEnvelopeCurve = value().curves().get(_myCurveKey);
//				value().currentCurve(_myCurveFrame.track().trackData());
				
				_myEditorFrame.setVisible(true);
			}
		});
        CCUIStyler.styleButton(_myButton, 30, 15);
        _myContainer.add(_myButton);
        
        _myResetButton = new JButton("reset");
        _myResetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				_myTextArea.setText(_myHandle.value().codeTemplate());
			}
		});
        CCUIStyler.styleButton(_myResetButton, 30, 15);
        _myContainer.add(_myResetButton);
	}
	
	private String errorLog(){
		StringBuffer myResult = new StringBuffer();
		for(Diagnostic<?> myDiagnostic:_myHandle.value().diagnostics()){
			myResult.append("\n");
			myResult.append( myDiagnostic.getKind() + " in " + myDiagnostic.getSource() + "\n");
			myResult.append( "LINE: " + myDiagnostic.getLineNumber() + "," + myDiagnostic.getMessage( null ) + "\n");
			myResult.append( "Position/Spalte: "+ myDiagnostic.getPosition()+","+myDiagnostic.getColumnNumber()  +"\n" );
			myResult.append( "Startpostion/Endposition:" + myDiagnostic.getStartPosition()+ "," + myDiagnostic.getEndPosition()+"\n"  );
		}
		return myResult.toString();
	}
	
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5,  5, 1, 5));
		thePanel.add(_myContainer, constraints(1, theY, 2, GridBagConstraints.LINE_START, 5, 15, 1, 5));
	}

	@Override
	public CCRealtimeCompile<?> value() {
		// TODO Auto-generated method stub
		return _myHandle.value();
	}
}
