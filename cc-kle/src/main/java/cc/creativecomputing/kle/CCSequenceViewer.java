package cc.creativecomputing.kle;

import java.nio.file.Path;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCMath;

public class CCSequenceViewer {

	private CCSequence _mySequence;
	
	private int _myColumns;
	private int _myRows;
	private CCDataObject _mySetup;
	private float[][] _myMin;
	private float[][] _myMax;
	
	private CCGLProgram _myInitValueShader;
	private CCGLProgram _myDrawValueShader;
	
	private CCGLProgram _myDrawGraphShader;
	private CCGLProgram _myDrawSimShader;
	
	private CCShaderBuffer _myMotionData;
	private CCGraphics g;
	
	public static enum CCSequenceDrawMode{
		POSITION, SPEED, ACCELERATION, JERK
	}
	
	public static enum CCSequenceDrawStyle{
		IMAGE, LINE, LINES, SIMULATION, SIMULATIONS
	}
	
	private float _myMaxSpeed;
	private float _myMaxAcceleration;
	private float _myMaxJerk;
	
	private int _myLine = 0;
	private float _myLineScale = 0;
	
	private CCSequenceDrawMode _myDrawMode = CCSequenceDrawMode.POSITION;
	private CCSequenceDrawStyle _myDrawStyle = CCSequenceDrawStyle.LINE;
	private CCShaderBuffer _myOutputBuffer;
	
	public CCSequenceViewer(CCGraphics theGraphics, Path theFormatFile){
		g = theGraphics;
		
		_mySetup = CCDataIO.createDataObject(theFormatFile);
		_myColumns = 0;
		_myRows = 0;
		
		CCDataObject myMapping = _mySetup.getObject("mapping");
		CCDataArray myChannels = myMapping.getArray("channel");
		for(int i = 0; i < myChannels.size();i++){
			CCDataObject myChannelObject = myChannels.getObject(i);
			int myColumn = myChannelObject.getInt("column");
			int myRow = myChannelObject.getInt("row");
			_myColumns = CCMath.max(_myColumns, myColumn);
			_myRows = CCMath.max(_myRows, myRow);
		}
		_myColumns++;
		_myRows++;
		
		
		_myMin = new float[_myColumns][_myRows];
		_myMax = new float[_myColumns][_myRows];
		
		for(int i = 0; i < myChannels.size();i++){
			CCDataObject myChannelObject = myChannels.getObject(i);
			int myColumn = myChannelObject.getInt("column");
			int myRow = myChannelObject.getInt("row");
			_myMin[myColumn][myRow] = myChannelObject.getFloat("min");
			_myMax[myColumn][myRow] = myChannelObject.getFloat("max");
		}
		
		_myInitValueShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "init_values_vert.glsl"), 
			CCNIOUtil.classPath(this, "init_values_frag.glsl")
		);
		
		_myDrawValueShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "draw_shader_vert.glsl"), 
			CCNIOUtil.classPath(this, "draw_shader_frag.glsl")
		);
		
		_myDrawGraphShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "drawLine_vert.glsl"), 
			CCNIOUtil.classPath(this, "drawLine_frag.glsl")
		);
		
		_myDrawSimShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "drawSim_vert.glsl"), 
			CCNIOUtil.classPath(this, "drawSim_frag.glsl")
		);
	}
	
	public void maxSpeed(float theMaxSpeed){
		_myMaxSpeed = theMaxSpeed;
	}
	
	public void maxAcceleration(float theMaxAcceleration){
		_myMaxAcceleration = theMaxAcceleration;
	}
	
	public void maxJerk(float theMaxJerk){
		_myMaxJerk = theMaxJerk;
	}
	
	public void line(int theLine){
		_myLine = theLine;
	}
	
	public void lineScale(float theLineScale){
		_myLineScale = theLineScale;
	}
	
	public void drawMode(CCSequenceDrawMode theDrawMode){
		_myDrawMode = theDrawMode;
	}
	
	public void drawStyle(CCSequenceDrawStyle theDrawStyle){
		_myDrawStyle = theDrawStyle;
	}
	
	private void drawImage(CCGraphics g){
		_myOutputBuffer.beginDraw();
		g.clear();
		g.texture(0,_myMotionData.attachment(0));
		_myDrawValueShader.start();
		_myDrawValueShader.uniform1i("motionData", 0);
		_myDrawValueShader.uniform1f("maxSpeed", _myMaxSpeed);
		_myDrawValueShader.uniform1f("maxAcc", _myMaxAcceleration);
		_myDrawValueShader.uniform1f("maxJerk", _myMaxJerk);
		_myDrawValueShader.uniform1i("drawMode", _myDrawMode.ordinal());
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0,0,0);
		g.vertex(0, 0);
		g.textureCoords2D(0,_myMotionData.width(),0);
		g.vertex(_myMotionData.width(), 0);
		g.textureCoords2D(0,_myMotionData.width(),_myMotionData.height());
		g.vertex(_myMotionData.width(),   _myMotionData.height());
		g.textureCoords2D(0,0,_myMotionData.height());
		g.vertex(0,   _myMotionData.height());
		g.endShape();
		_myDrawValueShader.end();
		g.noTexture();
		_myOutputBuffer.endDraw();
	}
	
	private void drawLines(CCGraphics g, int theStartLine, int theLines){
		_myDrawGraphShader.start();
		g.texture(0,_myOutputBuffer.attachment(0));
		_myDrawGraphShader.uniform1i("motionData", 0);
		_myDrawGraphShader.uniform1f("scale", _myLineScale);
		_myDrawGraphShader.uniform1i("drawMode", _myDrawMode.ordinal());
		for(int line = theStartLine; line < theStartLine + theLines; line++){
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int i = 0; i < _myOutputBuffer.width();i++){
				g.textureCoords2D(0,i + 0.5f, line + 0.5f);
				float x = CCMath.map(i + 0.5f, 0, _myOutputBuffer.width(), -_myOutputBuffer.width() / 2, _myOutputBuffer.width() / 2);
				g.vertex(x, 0);
			}
			g.endShape();
		}
		g.noTexture();
		_myDrawGraphShader.end();
	}
	
	private void drawSim(CCGraphics g, int theStartLine, int theLines){
		_myDrawSimShader.start();
		g.texture(0,_myOutputBuffer.attachment(0));
		_myDrawSimShader.uniform1i("motionData", 0);
		_myDrawSimShader.uniform1f("scale", _myLineScale);
		_myDrawSimShader.uniform1i("drawMode", _myDrawMode.ordinal());
		for(int line = theStartLine; line < theStartLine + theLines; line++){
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int i = 0; i < _myOutputBuffer.width();i+=2){
				g.textureCoords2D(0,i + 0.5f, line + 0.5f);
				float x = CCMath.map(i + 0.5f, 0, _myOutputBuffer.width(), -_myOutputBuffer.width() / 2, _myOutputBuffer.width() / 2);
				g.vertex(x, 0);
			}
			g.endShape();
		}
		g.noTexture();
		_myDrawSimShader.end();
	}
	
	public void draw(CCGraphics g){
		if(_mySequence == null)return;
		
		drawImage(g);
		
		switch(_myDrawStyle){
		case IMAGE:
			g.color(255);
			g.image(_myOutputBuffer.attachment(0), -_myOutputBuffer.width() / 2, -_myOutputBuffer.height() / 2);
			break;
		case LINE:
			g.color(0);
			drawLines(g,_myLine,1);
			break;
		case LINES:
			g.color(0, 55);
			drawLines(g,0,_myOutputBuffer.height());
			break;
		case SIMULATION:
			g.color(0);
			drawSim(g,0,1);
			break;
		case SIMULATIONS:
			g.color(0, 25);
			drawSim(g,0,_myOutputBuffer.height());
			break;
		}
		
	}
	
	public void load(Path theFile){
		if(theFile == null)return;
		
		if(theFile.endsWith(".bin")){
//			_mySequence = new CCSequenceBinFormat().load(theFile);
//			return;
		}else{
			return;
		}

		_myMotionData = new CCShaderBuffer(_mySequence.length(), _myColumns * _myRows);
		_myMotionData.beginDraw();
		g.clearColor(255);
		g.clear();
		_myInitValueShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(int c = 0; c < _myColumns; c++){
			for(int r = 0; r < _myRows; r++){
				for(int i = 0; i < _mySequence.length();i++){
					float myY = c * _myRows + r;
					
					double myValue = CCMath.norm(_mySequence.frame(i).data()[c][r][0] * 10, _myMin[c][r], _myMax[c][r]);
					g.textureCoords3D(0, (float)_mySequence.frame(i).data()[c][r][0] * 10, _myMin[c][r], _myMax[c][r]);
//					g.textureCoords(0, myValue, myValue, myValue);
					g.vertex(i,myY);
				}
			}
		}
		g.endShape();
		_myInitValueShader.end();
		_myMotionData.endDraw();
		_myOutputBuffer = new CCShaderBuffer(_mySequence.length(), _myColumns * _myRows);
	}
}
