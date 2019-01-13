package cc.creativecomputing.opencv;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCMarker{
	public int id;
	public boolean active;
	public CCVector3 rotationVector = new CCVector3();
	public CCVector3 translationVector = new CCVector3();
	
	public CCQuaternion rotation = new CCQuaternion();
	
	public List<CCVector2> corners = new ArrayList<>(); 
}