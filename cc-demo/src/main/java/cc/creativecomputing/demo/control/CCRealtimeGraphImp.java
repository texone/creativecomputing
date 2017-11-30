package cc.creativecomputing.demo.control;

import cc.creativecomputing.demo.control.CCRealtimeCodingDemo.CCRealtimeGraph;
import java.lang.Object;
import cc.creativecomputing.graphics.CCGraphics;

public class CCRealtimeGraphImp implements CCRealtimeGraph{

	public void draw(CCGraphics theCCGraphics){
		g.color(255,0,0);
		theCCGraphics.rect(0,0,100,100);
	}

	public Object[] parameters(){
		return null;
	}

	public void onRecompile(){
	}


}