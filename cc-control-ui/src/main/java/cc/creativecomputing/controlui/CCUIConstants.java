/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui;

import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCColor;

/**
 * @author christianriekoff
 *
 */
public class CCUIConstants {
	
	public static CCFont<?> DEFAULT_FONT;
	public static CCFont<?> DEFAULT_FONT_2;
	public static CCFont<?> MENUE_FONT;

	public static int SCALE = 1;
	
	public static float DEFAULT_TRACK_CONTROL_WEIGHT = 0.25f * SCALE;
	
	public static boolean CREATE_MUTE_BUTTON = true;
	public static boolean CREATE_SPEED_CONTROL = true;
	public static boolean CREATE_BPM_CONTROL = true;
	
	public static boolean CREATE_EDIT_MENU = true;
	public static boolean CREATE_UNDO_ENTRIES = true;
	
	public static boolean SHOW_CURVE_TOOL_OPTIONS = true;
	
	public static CCTimelineTools[] CURVE_TOOLS = new CCTimelineTools[] {
		CCTimelineTools.CURVE,
		CCTimelineTools.BEZIER_POINT,
		CCTimelineTools.LINEAR_POINT,
		CCTimelineTools.STEP_POINT,
		CCTimelineTools.SELECT
	};
	
	public static final CCColor LINE_COLOR = new CCColor(0.14f, 0.86f, 0.9f, 0.6f);
    public static final CCColor FILL_COLOR = new CCColor(0.14f, 0.86f, 0.9f, 0.2f);
    public static final CCColor DOT_COLOR = new CCColor(0.14f, 0.6f, 1.0f, 0.9f);
    public static final CCColor SPECIAL_DOT_COLOR = new CCColor(0.9f, 0.2f, 0.2f, 1.0f);
    
	public static final CCColor SELECTION_COLOR = new CCColor(0.8f, 0.5f, 0.5f, 0.2f);
	public static final CCColor SELECTION_BORDER_COLOR = new CCColor(0.8f, 0.2f, 0.2f, 0.6f);
    
    public static final int MAX_GRID_LINES = 200;
    public static final int MAX_RULER_LABELS = 10;
    public static final double MIN_RULER_INTERVAL = 0.250;
    public static final double DEFAULT_RANGE = 10;
    
	public static final double PICK_RADIUS = 10;
    
    public static final int CURVE_POINT_SIZE = 4;
}
