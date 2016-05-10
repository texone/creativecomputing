package cc.creativecomputing.kle.elements;

import java.util.ArrayList;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.math.CCMath;

public class CCSequenceMapping<ChannelType extends CCSequenceChannel> extends ArrayList<ChannelType> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4284860961918867581L;

	private final CCKleChannelType _myType;

	private final int _myColumns;
	private final int _myRows;
	private final int _myDepth;
	
	private final int _myFrameRate;
	private final int _myBitDepth;
	
	private final int[] _myColumnIDs;
	private final int[] _myRowIDs;
	private final int[] _myDepthIDs;
	private final double[] _myMins;
	private final double[] _myMaxs;
	
	private final double[][][] _myMinMatrix;
	private final double[][][] _myMaxMatrix;
	
	private double _myOverallMin = Double.MAX_VALUE;
	private double _myOverallMax = -Double.MAX_VALUE;
	
	public CCSequenceMapping(CCXMLElement theMappingXML){
		_myType = CCKleChannelType.valueOf(theMappingXML.attribute("name").toUpperCase());
		_myColumns = theMappingXML.intAttribute("columns");
		_myRows = theMappingXML.intAttribute("rows");
		_myDepth = theMappingXML.intAttribute("depth");
		_myFrameRate = theMappingXML.intAttribute("frameRate");
		_myBitDepth = theMappingXML.intAttribute("bits");
		
		_myMinMatrix = new double[_myColumns][_myRows][_myDepth];
		_myMaxMatrix = new double[_myColumns][_myRows][_myDepth];
		
		int myChannels = theMappingXML.countChildren();
		_myColumnIDs = new int[myChannels];
		_myRowIDs = new int[myChannels];
		_myDepthIDs = new int[myChannels];
		_myMins = new double[myChannels];
		_myMaxs = new double[myChannels];
		int counter = 0;
		
		for(CCXMLElement myChannelXML:theMappingXML){
			int myID = myChannelXML.intAttribute("id", counter++);
			int myColumn = myChannelXML.intAttribute("column", 0);
			int myRow = myChannelXML.intAttribute("row", 0);
			int myDepth = myChannelXML.intAttribute("depth", 0);
			double myMin = myChannelXML.doubleAttribute("min", 0);
			double myMax = myChannelXML.doubleAttribute("max", 1);
			
			_myColumnIDs[myID] = myColumn;
			_myRowIDs[myID] = myRow;
			_myDepthIDs[myID] = myDepth;
			_myMins[myID] = myMin;
			_myMaxs[myID] = myMax;
			
			_myMinMatrix[myColumn][myRow][myDepth] = myMin;
			_myMaxMatrix[myColumn][myRow][myDepth] = myMax;
			
			_myOverallMin = CCMath.min(_myOverallMin, myMin);
			_myOverallMax = CCMath.max(_myOverallMax, myMax);
		}
	}
	
	@Override
	public boolean add(ChannelType theChannel) {
		theChannel.column(_myColumnIDs[theChannel.id()]);
		theChannel.row(_myRowIDs[theChannel.id()]);
		theChannel.depth(_myDepthIDs[theChannel.id()]);
		theChannel.min(_myMins[theChannel.id()]);
		theChannel.max(_myMaxs[theChannel.id()]);
		return super.add(theChannel);
	}
	
	public CCKleChannelType type(){
		return _myType;
	}
	
	public int columns(){
		return _myColumns;
	}
	
	public int rows(){
		return _myRows;
	}
	
	public int depth(){
		return _myDepth;
	}
	
	public int frameRate(){
		return _myFrameRate;
	}
	
	public int bitDepth(){
		return _myBitDepth;
	}
	
	public double min(int theColumn, int theRow, int theDepth){
		return _myMinMatrix[theColumn][theRow][theDepth];
	}
	
	public double max(int theColumn, int theRow, int theDepth){
		return _myMaxMatrix[theColumn][theRow][theDepth];
	}
	
	public double min(){
		return _myOverallMin;
	}
	
	public double max(){
		return _myOverallMax;
	}
	
	public CCXMLElement toXML(){
		CCXMLElement myMappingXML = new CCXMLElement("mapping");
		myMappingXML.addAttribute("name", _myType.id());
		myMappingXML.addAttribute("columns", _myColumns);
		myMappingXML.addAttribute("rows", _myRows);
		myMappingXML.addAttribute("depth", _myDepth);
		myMappingXML.addAttribute("frameRate", _myFrameRate);
		myMappingXML.addAttribute("bits", _myBitDepth);
		for(CCSequenceChannel myChannel:this){
			myMappingXML.addChild(myChannel.mappingXML());
		}
		return myMappingXML;
	}
}
