/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.kle.formats;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.CCEffect;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectManager;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionCalculations;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder;
import cc.creativecomputing.math.CCMath;

public class CCSequenceFormatsTest {

    private static CCAnimator _myAnimator;
    private static CCSequenceRecorder _myRecorder;
	private static CCKleEffectables _mySequenceElements;
	
	private static class CCKleTestAnimation extends CCEffect{
		
		private float _myPhase = 0;

		@Override
		public void update(double theDeltaTime) {
			_myPhase += theDeltaTime * 0.01f;
			CCLog.info(theDeltaTime);
		}
		
		@Override
		public double[] applyTo(CCEffectable theElement) {
			return new double[]{CCMath.sin(_myPhase), CCMath.sin(_myPhase)};
		}
		
	}
	
	@BeforeClass
	static public void createTestElements(){
		CC2Motor2ConnectionCalculations myBounds = new CC2Motor2ConnectionCalculations();
		_mySequenceElements = new CCKleEffectables(
			CCNIOUtil.dataPath("config"), 
			myBounds,
			160
		);
		
		CCKleEffectManager myAnimator = new CCKleEffectManager(_mySequenceElements, CCKleChannelType.MOTORS, "x", "y");
		myAnimator.put("test", new CCKleTestAnimation());
		
		_myAnimator = new CCAnimator();
		_myAnimator.listener().add(myAnimator);
		_myRecorder = new CCSequenceRecorder(null,_mySequenceElements, _myAnimator);
		_myRecorder.recording(CCKleChannelType.MOTORS).updateSteps = 1;
		_myRecorder.recording(CCKleChannelType.LIGHTS).export = false;
		_myRecorder.fadeSeconds(0);
		_myRecorder.startRecord(CCKleChannelType.MOTORS);
		_myAnimator.fixedUpdateTime = 1f/5;
		for(int i = 0; i < 10000; i++){
			_myAnimator.update();
//			_myRecorder.recordFrame();
		}
	}
	
	private void testFormat(CCKleFormats theFormat, String thePath){
		
        CCSequence myRecordedSequence = _myRecorder.sequence(CCKleChannelType.MOTORS);
        CCSequence myLoadedSequence;
        if(theFormat.isFolder()){
        	Path myPath = CCNIOUtil.dataPath(thePath);
    		theFormat.save(myPath, _mySequenceElements.mappings().get(CCKleChannelType.MOTORS), myRecordedSequence);
            myLoadedSequence= theFormat.load(myPath, _mySequenceElements.mappings().get(CCKleChannelType.MOTORS));
        }else{
        	Path myPath = CCNIOUtil.dataPath(thePath).resolve("motors." + theFormat.extension());
    		theFormat.save(myPath,  _mySequenceElements.mappings().get(CCKleChannelType.MOTORS), myRecordedSequence);
            myLoadedSequence= theFormat.load(myPath, _mySequenceElements.mappings().get(CCKleChannelType.MOTORS));
        }

        assertEquals(myRecordedSequence, myLoadedSequence);
	}
	
	private void testContainer(CCKleContainers theContainer, String thePath){
//		_myRecorder.container(theContainer);
//		_myRecorder.save(CCNIOUtil.dataPath(thePath));
		
		switch(theContainer){
		case KLE_1:
			Map<CCKleChannelType, CCSequence> mySequenceMap = new CCKleV1Container().load(CCNIOUtil.dataPath(thePath), _mySequenceElements);
			 CCSequence myRecordedSequence = _myRecorder.sequence(CCKleChannelType.MOTORS);
			 CCSequence myLoadedSequence = mySequenceMap.get(CCKleChannelType.MOTORS);
			 assertEquals(myRecordedSequence, myLoadedSequence);
			break;
		default:
		}
	}

//    @Test
    public void testRecordCSV() {
    	testFormat(CCKleFormats.CSV, "export/csv");
    }
    
//    @Test
    public void testRecordPNG() {
    	testFormat(CCKleFormats.PNG, "export/png");
    }
    
    @Test
    public void testRecordBIN() {
    	testFormat(CCKleFormats.BIN, "export/test_bin");
    }
    
//    @Test
    public void testRecordKLE() {
    	testContainer(CCKleContainers.KLE_1, "export/kle1.kle");
    }
    
//    @Test
    public void testSequenceIO(){
    	CCSequence myRecordedSequence = _myRecorder.sequence(CCKleChannelType.MOTORS);
    	
    	CCSequence myLoadedSequence;
    	myLoadedSequence = CCKleIO.load(CCNIOUtil.dataPath("export/test_bin/motors.bin"), _mySequenceElements.mappings().get(CCKleChannelType.MOTORS));
    	assertEquals(myRecordedSequence, myLoadedSequence);
		
    	myLoadedSequence = CCKleIO.load(CCNIOUtil.dataPath("export/csv"), _mySequenceElements.mappings().get(CCKleChannelType.MOTORS));
    	assertEquals(myRecordedSequence, myLoadedSequence);

    	myLoadedSequence = CCKleIO.load(CCNIOUtil.dataPath("export/png"), _mySequenceElements.mappings().get(CCKleChannelType.MOTORS));
    	assertEquals(myRecordedSequence, myLoadedSequence);

    	myLoadedSequence = CCKleIO.load(CCNIOUtil.dataPath("export/kle1.kle"), _mySequenceElements.mappings().get(CCKleChannelType.MOTORS));
    	
    	assertEquals(myRecordedSequence, myLoadedSequence);
    }
    
    @AfterClass
    static public void cleanup(){
//    	CCNIOUtil.deleteIfExists(CCNIOUtil.dataPath("export"));
    }

}
