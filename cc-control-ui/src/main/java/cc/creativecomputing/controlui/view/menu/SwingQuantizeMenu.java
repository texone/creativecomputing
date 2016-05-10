package cc.creativecomputing.controlui.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.quantize.OffQuantizer;
import cc.creativecomputing.controlui.timeline.controller.quantize.Quantizer;
import cc.creativecomputing.math.CCMath;


@SuppressWarnings("serial")
public class SwingQuantizeMenu extends JMenu {
	
	private class QuantizeAction implements ActionListener{
		
		private Quantizer _myQuantizer;
		
		private QuantizeAction(Quantizer theQuantizer){
			_myQuantizer = theQuantizer;
		}

		@Override
		public void actionPerformed(ActionEvent theE) {
			_myTimelineContainer.activeTimeline().quantizer(_myQuantizer);
		}
		
	}
	
	public class SubStepQuantizer implements Quantizer{
		private int _myRaster;
		
		public SubStepQuantizer(int theRaster){
			_myRaster = theRaster;
		}

		@Override
		public double quantize(double theTime) {
			if(_myRaster <= 0)return 0;
			return _myTimelineContainer.activeTimeline().transportController().rulerInterval().quantize(theTime, _myRaster);
		}

		@Override
		public int drawRaster() {
			return _myRaster;
		}

	}
	
	public class TimeQuantizer implements Quantizer{
		
		private float _myRaster;
		
		public TimeQuantizer(float theRaster){
			_myRaster = theRaster;
		}

		@Override
		public double quantize(double theTime) {
			if(_myRaster <= 0)return theTime;
			return CCMath.round(theTime / _myRaster) * _myRaster;
		}

		@Override
		public int drawRaster() {
			return (int)(_myTimelineContainer.activeTimeline().transportController().rulerInterval().interval() / _myRaster);
		}
		
	}

	private TimelineContainer _myTimelineContainer;
	
	private ButtonGroup _myButtonGroup;
	
	public SwingQuantizeMenu(TimelineContainer theTimelineContainer) {
		super("Quantize");

		setMnemonic(KeyEvent.VK_Q);

		_myTimelineContainer = theTimelineContainer;
		
		_myButtonGroup = new ButtonGroup();

		add(createItem("OFF", new OffQuantizer()));
		
		add(createItem("TIME 10 ms", new TimeQuantizer(0.01f)));
		add(createItem("TIME 20 ms", new TimeQuantizer(0.02f)));
		add(createItem("TIME 50 ms", new TimeQuantizer(0.05f)));
		add(createItem("TIME 100 ms", new TimeQuantizer(0.1f)));
		add(createItem("TIME 200 ms", new TimeQuantizer(0.2f)));
		add(createItem("TIME 500 ms", new TimeQuantizer(0.5f)));
		add(createItem("TIME 1 s", new TimeQuantizer(1f)));
		add(createItem("TIME 2 s", new TimeQuantizer(2f)));
		add(createItem("TIME 5 s", new TimeQuantizer(5f)));
		add(createItem("TIME 10 s", new TimeQuantizer(10f)));
		add(createItem("TIME 15 s", new TimeQuantizer(15f)));
		add(createItem("TIME 20 s", new TimeQuantizer(20f)));
		add(createItem("TIME 30 s", new TimeQuantizer(30f)));
		add(createItem("TIME 60 s", new TimeQuantizer(60f)));
		
		add(createItem("SUBSTEP 1", new SubStepQuantizer(1)));
		add(createItem("SUBSTEP 2", new SubStepQuantizer(2)));
		add(createItem("SUBSTEP 4", new SubStepQuantizer(4)));
		add(createItem("SUBSTEP 8", new SubStepQuantizer(8)));
		add(createItem("SUBSTEP 16", new SubStepQuantizer(16)));
		add(createItem("SUBSTEP 32", new SubStepQuantizer(32)));
	}

	private JRadioButtonMenuItem createItem(String theLabel, Quantizer theQuantizer){
		JRadioButtonMenuItem myResult = new JRadioButtonMenuItem(theLabel);
		myResult.addActionListener(new QuantizeAction(theQuantizer));
		_myButtonGroup.add(myResult);
		return myResult;
	}
}
