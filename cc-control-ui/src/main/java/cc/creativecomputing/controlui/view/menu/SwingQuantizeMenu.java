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
import cc.creativecomputing.controlui.timeline.controller.quantize.RasterQuantizer;


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
	
	public class RulerBasedQuantizer implements Quantizer{
		private int _myRaster;
		
		public RulerBasedQuantizer(int theRaster){
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

	private TimelineContainer _myTimelineContainer;
	
	private ButtonGroup _myButtonGroup;
	
	public SwingQuantizeMenu(TimelineContainer theTimelineContainer) {
		super("Quantize");

		setMnemonic(KeyEvent.VK_Q);

		_myTimelineContainer = theTimelineContainer;
		
		_myButtonGroup = new ButtonGroup();

		add(createItem("OFF", new OffQuantizer()));
		
		add(createItem("RASTER 0.01", new RasterQuantizer(0.01f)));
		add(createItem("RASTER 0.02", new RasterQuantizer(0.02f)));
		add(createItem("RASTER 0.05", new RasterQuantizer(0.05f)));
		add(createItem("RASTER 0.1", new RasterQuantizer(0.1f)));
		add(createItem("RASTER 0.2", new RasterQuantizer(0.2f)));
		add(createItem("RASTER 0.5", new RasterQuantizer(0.5f)));
		add(createItem("RASTER 1", new RasterQuantizer(1f)));
		add(createItem("RASTER 2", new RasterQuantizer(2f)));
		add(createItem("RASTER 5", new RasterQuantizer(5f)));
		add(createItem("RASTER 10", new RasterQuantizer(10f)));
		add(createItem("RASTER 20", new RasterQuantizer(20f)));
		add(createItem("RASTER 50", new RasterQuantizer(50f)));
		
		add(createItem("RULER 1", new RulerBasedQuantizer(1)));
		add(createItem("RULER 2", new RulerBasedQuantizer(2)));
		add(createItem("RULER 4", new RulerBasedQuantizer(4)));
		add(createItem("RULER 8", new RulerBasedQuantizer(8)));
		add(createItem("RULER 5", new RulerBasedQuantizer(5)));
		add(createItem("RULER 10", new RulerBasedQuantizer(10)));
		add(createItem("RULER 20", new RulerBasedQuantizer(20)));
	}

	private JRadioButtonMenuItem createItem(String theLabel, Quantizer theQuantizer){
		JRadioButtonMenuItem myResult = new JRadioButtonMenuItem(theLabel);
		myResult.addActionListener(new QuantizeAction(theQuantizer));
		_myButtonGroup.add(myResult);
		return myResult;
	}
}
