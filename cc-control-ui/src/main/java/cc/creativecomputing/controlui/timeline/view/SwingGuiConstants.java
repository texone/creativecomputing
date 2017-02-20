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
package cc.creativecomputing.controlui.timeline.view;

import java.awt.Font;

import cc.creativecomputing.controlui.timeline.controller.TimelineTool;

/**
 * @author christianriekoff
 *
 */
public class SwingGuiConstants {

	public static int SCALE = 2;

	public static Font ARIAL_9 = new Font("Arial", 1, 9 * SCALE);//LucidaGrande
	public static Font ARIAL_BOLD_10 = new Font("Arial", Font.BOLD, 10 * SCALE);
	public static Font ARIAL_11 = new Font("Arial", 1, 11 * SCALE);
	
	public static float DEFAULT_TRACK_CONTROL_WEIGHT = 0.25f * SCALE;
	
	public static boolean CREATE_MUTE_BUTTON = true;
	public static boolean CREATE_SPEED_CONTROL = true;
	public static boolean CREATE_BPM_CONTROL = true;
	
	public static boolean CREATE_EDIT_MENU = true;
	public static boolean CREATE_UNDO_ENTRIES = true;
	
	public static boolean SHOW_CURVE_TOOL_OPTIONS = true;
	
	public static TimelineTool[] CURVE_TOOLS = new TimelineTool[] {
		TimelineTool.CURVE,
		TimelineTool.BEZIER,
		TimelineTool.LINEAR,
		TimelineTool.STEP,
		TimelineTool.MOVE
	};
}
