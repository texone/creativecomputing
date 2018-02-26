package cc.creativecomputing.controlui.timeline.controller.quantize;

public enum CCQuatizeMode{
		OFF("OFF", new CCOffQuantizer()),
		
		TIME_10_MS("TIME 10 ms", new CCTimeQuantizer(0.01)),
		TIME_20_MS("TIME 20 ms", new CCTimeQuantizer(0.02f)),
		TIME_50_MS("TIME 50 ms", new CCTimeQuantizer(0.05f)),
		TIME_100_MS("TIME 100 ms", new CCTimeQuantizer(0.1f)),
		TIME_200_MS("TIME 200 ms", new CCTimeQuantizer(0.2f)),
		TIME_500_MS("TIME 500 ms", new CCTimeQuantizer(0.5f)),
		TIME_1_S("TIME 1 s", new CCTimeQuantizer(1f)),
		TIME_2_S("TIME 2 s", new CCTimeQuantizer(2f)),
		TIME_5_MS("TIME 5 s", new CCTimeQuantizer(5f)),
		TIME_10_S("TIME 10 s", new CCTimeQuantizer(10f)),
		TIME_15_S("TIME 15 s", new CCTimeQuantizer(15f)),
		TIME_20_S("TIME 20 s", new CCTimeQuantizer(20f)),
		TIME_30_S("TIME 30 s", new CCTimeQuantizer(30f)),
		TIME_60_S("TIME 60 s", new CCTimeQuantizer(60f)),
		
		SUBSTEP_1("SUBSTEP 1", new CCSubStepQuantizer(1)),
		SUBSTEP_2("SUBSTEP 2", new CCSubStepQuantizer(2)),
		SUBSTEP_4("SUBSTEP 4", new CCSubStepQuantizer(4)),
		SUBSTEP_8("SUBSTEP 8", new CCSubStepQuantizer(8)),
		SUBSTEP_16("SUBSTEP 16", new CCSubStepQuantizer(16)),
		SUBSTEP_32("SUBSTEP 32", new CCSubStepQuantizer(32));
		
		
		private final CCQuantizer _myQuantizer;
		
		private final String _myDesc;
		
		CCQuatizeMode(String theDesc, CCQuantizer theQuantizer){
			_myQuantizer = theQuantizer;
			_myDesc = theDesc;
		}
		
		public CCQuantizer quantizer(){
			return _myQuantizer;
		}
		
		public String desc(){
			return _myDesc;
		}
	}