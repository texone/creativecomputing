package cc.creativecomputing.demo;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

public class CCWatchApp extends CCGL2Adapter{
	private class CrashLog{
		List<CCVector2> _myChrashes = new ArrayList<CCVector2>();
		
		@CCProperty(name = "color")
		CCColor _cColor = new CCColor(255);
		
		@CCProperty(name = "active")
		boolean _cActive = false;
		
		
		public void display(CCGraphics g){
			if(!_cActive)return;
			g.color(_cColor);
			g.beginShape(CCDrawMode.POINTS);
			for(CCVector2 myPoint:_myChrashes){
				g.vertex(myPoint);
				
			}
			g.endShape();
		}
	}
	
	@CCProperty(name = "crash map")
	private Map<String, CrashLog> _myCrashMap = new LinkedHashMap<String, CCWatchApp.CrashLog>();
	
	@Override
	public void start(CCAnimator animator) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Path file:CCNIOUtil.list(CCNIOUtil.dataPath("watchright"), "log")){
			
			boolean nextDate = false;
			String dateString = null;
			Date myDate;
			int myx = 0;
			int myY = 0;
			for(String myLine:CCNIOUtil.loadStrings(file)){
				if(myLine.startsWith("----------")){
					nextDate = true;
					continue;
				}
				if(nextDate){
					nextDate = false;

					dateString = myLine;
					try {
						myDate = df.parse(dateString);
						Calendar myCalender = Calendar.getInstance();
						myCalender.setTime(myDate);
						int year = myCalender.get(Calendar.YEAR) - 2014;
						int day = myCalender.get(Calendar.DAY_OF_YEAR);
						myx = (year * 365 + day);
						myY = (myCalender.get(Calendar.MINUTE) + myCalender.get(Calendar.HOUR_OF_DAY) * 60);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				if(myLine.contains("threadid:"))continue;

				if(myLine.contains("Set environment variable:"))continue;
				
				if(myLine.contains("Monitoring heartbeat file"))continue;
				
				if(myLine.equals("StatusReport request from Network")){
					continue;
				}
				
				if(myLine.startsWith("Started successfully")){
					myLine = "Started successfully";
				}
				
				if(!_myCrashMap.containsKey(myLine)){
					CCLog.info(myLine);
					_myCrashMap.put(myLine, new CrashLog());
				}
				_myCrashMap.get(myLine)._myChrashes.add(new CCVector2(myY,myx));
				
			}
		}
	}
	
	@Override
	public void init(CCGraphics g) {
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	
	
	@Override
	public void display(CCGraphics g) {
		
		g.clear();
		g.pushMatrix();
		g.translate(-g.width()/2, -g.height()/2);
		for(CrashLog myLog:_myCrashMap.values()){
			myLog.display(g);
			
		}
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCLog.info(CCNIOUtil.dataPath("watch"));
		
		
		
		CCWatchApp demo = new CCWatchApp();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(2000, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
