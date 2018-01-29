package cc.creativecomputing.controlui.patch;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.view.CCTextInputDialog;
import cc.creativecomputing.math.CCMath;

public class CCPatchView {
	
	private final Canvas _myCanvas;
	
    private final CCConnectionView _myConnectionView;

    private final List<CCNodeView> _myViews = new ArrayList<>();

    private int _myX = 0;
    private int _myY = 0;
	
	public CCPatchView(int theWidth, int theHeight){
	    
	  //Creating the canvas.
	    _myCanvas = new Canvas();
	
	    _myCanvas.setSize(theWidth - 330, theHeight);
	    _myCanvas.setBackground(new Color(225,225,225));
	    _myCanvas.setVisible(true);
	    _myCanvas.setFocusable(false);

		_myConnectionView = new CCConnectionView(_myViews);
		_myCanvas.addMouseListener(_myConnectionView);
	    _myCanvas.addMouseMotionListener(_myConnectionView);
	    
	    JPanel myInfoPanel = new JPanel();
	    myInfoPanel.setLayout(new BorderLayout());
	    myInfoPanel.setBackground(Color.GRAY);
        
		JScrollPane myScrollPane = new JScrollPane(myInfoPanel);
		myScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
//		_myScrollPane.setPreferredSize(new Dimension(800,800));
		myScrollPane.setBackground(Color.GREEN);
		myScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    

	    JSplitPane myControlsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        CCUIStyler.styleSplitPane(myControlsSplitPane);
        myControlsSplitPane.setLeftComponent(_myCanvas);
        myControlsSplitPane.setDividerLocation(theWidth - 330);
        myControlsSplitPane.setRightComponent(myScrollPane);
	
	    //Putting it all together.
        JFrame frame = new JFrame("patch");
    	
	    frame.add(myControlsSplitPane);
	    

	    frame.setSize(theWidth, theHeight);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(false);
	    frame.setVisible(true);
	    
	    _myCanvas.createBufferStrategy(3);
	    _myCanvas.addMouseListener(new MouseAdapter() {
	    	@Override
			public void mousePressed(MouseEvent e) {
				
				if (e.getButton() == MouseEvent.BUTTON3 || e.isAltDown()) {
					_myX = e.getX();
					_myY = e.getY();
					new CCTextInputDialog(
						"Insert Node", 
						"Node Name","insert",text ->{
				    			CCNode myNode = new CCNode(text, new CCPinOutput());
				    			addNode(myNode, _myX, _myY);
						}
					)
					.location(e.getXOnScreen(), e.getYOnScreen())
					.size(300,200)
					.open();
    				} 
			}
		});
	}
	
	public void draw(){
		BufferStrategy bufferStrategy = _myCanvas.getBufferStrategy();
	    Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.clearRect(0, 0, _myCanvas.getWidth(), _myCanvas.getHeight());

        for(CCNodeView myView:_myViews){
	        myView.draw(graphics);
        }
        _myConnectionView.draw(graphics);

        bufferStrategy.show();
        graphics.dispose();
	}
	
	public void addNode(CCNode theNode, int theX, int theY){
		CCNodeView myView = new CCNodeView(theNode, theX, theY);
	    _myViews.add(myView);
	    _myCanvas.addMouseListener(myView);
	    _myCanvas.addMouseMotionListener(myView);
	}
	
	public static void main(String[] args){

		final int width = 1200;
	    final int height = width / 16 * 9;
	
	    //Creating the frame.
		CCPatchView myPatchView = new CCPatchView(width, height);
	   
	    for(int i = 0; i < 10;i++){
	    	CCNode myNode = new CCNode("node " + i, new CCPinOutput());
	    	myNode.inputs.add(new CCPinInput());
	    	myNode.inputs.add(new CCPinInput());
		    myNode.inputs.add(new CCPinInput());
		    
		    myPatchView.addNode(myNode, (int)CCMath.random(0,width), (int)CCMath.random(0,height));
	    }
	    boolean running = true;
	
	    while (running) {
	        myPatchView.draw();
	    }
	}
}

