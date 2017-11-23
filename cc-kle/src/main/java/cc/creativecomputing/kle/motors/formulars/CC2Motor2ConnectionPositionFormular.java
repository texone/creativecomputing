package cc.creativecomputing.kle.motors.formulars;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionSetup;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

public abstract class CC2Motor2ConnectionPositionFormular {
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor();

	public abstract double rotation(CC2Motor2ConnectionSetup theSetup);
	
	public CCVector2 position(CC2Motor2ConnectionSetup theSetup){
		return new CCVector2();
	}
	
	public CCColor color(){
		return _myColor;
	}
}
