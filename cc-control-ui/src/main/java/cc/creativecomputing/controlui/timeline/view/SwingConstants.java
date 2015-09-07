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

import java.awt.Color;

/**
 * @author christianriekoff
 *
 */
public class SwingConstants {
	public static final Color LINE_COLOR = new Color(0.14f, 0.86f, 0.9f, 0.6f);
    public static final Color FILL_COLOR = new Color(0.14f, 0.86f, 0.9f, 0.2f);
    public static final Color DOT_COLOR = new Color(0.14f, 0.6f, 1.0f, 0.9f);
    public static final Color SPECIAL_DOT_COLOR = new Color(0.9f, 0.2f, 0.2f, 1.0f);
    
	public static final Color SELECTION_COLOR = new Color(0.8f, 0.5f, 0.5f, 0.2f);
	public static final Color SELECTION_BORDER_COLOR = new Color(0.8f, 0.2f, 0.2f, 0.6f);
    
    public static final int MAX_GRID_LINES = 200;
    public static final int MAX_RULER_LABELS = 10;
    public static final double MIN_RULER_INTERVAL = 0.250;
    public static final double DEFAULT_RANGE = 10;
}
