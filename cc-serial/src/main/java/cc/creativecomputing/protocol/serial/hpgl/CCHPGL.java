/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 *
 */

package cc.creativecomputing.protocol.serial.hpgl;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.serial.CCSerialModule;

/**
 * @author David Sjunnesson http://www.davidsjunnesson.com
 * @modified 01/24/2015
 * @version 1.0.1 (1) originally inspired by Tobias Toft blog post
 *          http://www.tobiastoft.com/posts/an-intro-to-pen-plotters
 * @author christianr
 *
 */
public class CCHPGL {
	boolean DEBUG = false;

	public double DEFAULT_FONT_HEIGHT = 0.2;
	public double DEFAULT_LABEL_DIRECTION = 0; // right

	double characterWidth = DEFAULT_FONT_HEIGHT;
	double characterHeight = DEFAULT_FONT_HEIGHT;
	double labelAngleHorizontal = 1;
	double labelAngleVertical = 0;
	

	

	static char END_OF_TEXT = 0x03;
	static char ESC_CHAR = 0x1B;

	public boolean bufferFull = false;

	@CCProperty(name = "bezier detail", min = 0, max = 100)
	private double bezierDetailLevel = 10;

	@CCProperty(name = "serial")
	private CCSerialModule _mySerial;

	public CCHPGL(CCHPPaperFormat paperType) {

		_mySerial = new CCSerialModule("HPGL", 9600);
		_mySerial.startEvents().add(input -> {
			// Initialize plotter
			write("IN;SP1;");
			CCLog.info("Plotter Initialized");
			inputWindow(paperType);

			// make sure that we set all the plotter variables to their
			// default values
			setFontHeight(DEFAULT_FONT_HEIGHT);
			labelDirection(DEFAULT_LABEL_DIRECTION);
		});
	}
	
	public boolean isConnected(){
		return _mySerial.active();
	}
	
	public static enum CCHPPaperFormat{
		A4,
		A3,
		A,
		B
	}
	
	public static enum CCHPGLCommand{
		/**
		 *  Input Scaling Point
		 */
		IP,
		/**
		 * Scale
		 */
		SC,
		/**
		 * Input window
		 */
		IW,
		/**
		 * Rotate coordinate system
		 */
		RO,
		/**
		 * Page feed
		 */
		PG,
		/**
		 * Pen Up
		 */
		PU,
		/**
		 * Pen Down
		 */
		PD,
		/**
		 * Plot Absolute
		 */
		PA ,
		/**
		 * Relative Coordinate Pen Move
		 */
		PR ,
		/**
		 * Absolute Arc Plot
		 */
		AA ,
		/**
		 * Relative Arc Plot
		 */
		AR ,
		/**
		 * Circle
		 */
		CI ,
		/**
		 * Edge Absolute Rectangle
		 */
		EA, 
		/**
		 * Edge Relative Rectangle
		 */
		ER, 
		/**
		 * Edge Wedge
		 */
		EW, 
		/**
		 * Fill Absolute Rectangle
		 */
		RA, 
		/**
		 * Fill Relative Rectangle
		 */
		RR, 
		/**
		 * Fill Wedge
		 */
		WG, 
		/**
		 * Fill Type
		 */
		FT, 
		/**
		 * Line Type
		 */
		LT, 
		/**
		 * Pen Width
		 */
		PW, 
		/**
		 * Symbol Mode
		 */
		SM, 
		/**
		 * Select Pen
		 */
		SP, 
		/**
		 * Tick Length
		 */
		TL, 
		/**
		 * X Tick
		 */
		XT, 
		/**
		 * Y Tick
		 */
		YT, 
		/**
		 * Pen Thickness
		 */
		PT,
		/**
		 * Standard Set Definition
		 */
		CS, 
		/**
		 * Alternate Set Definition
		 */
		CA, 
		/**
		 * Select Standard Font
		 */
		SS, 
		/**
		 * Select Alternate Font
		 */
		SA, 
		/**
		 * Define Label Terminator
		 */
		DT, 
		/**
		 * Define Label
		 */
		LB, 
		/**
		 * Absolute Direction
		 */
		DI, 
		/**
		 * Relative Direction
		 */
		DR, 
		/**
		 * Character Plot
		 */
		CP, 
		/**
		 * Set Absolute Character Size
		 */
		SI, 
		/**
		 * Set Relative Character Size
		 */
		SR, 
		/**
		 * Set Character Slant
		 */
		SL, 
		/**
		 * User-defined Character
		 */
		UC 
	}

	// COMMUNICATION
	
	public void write(String hpgl) {
		if(!_mySerial.active())return;
		if (DEBUG) {
			CCLog.info(hpgl);
		}

		// if the plotter has reported that the buffer is full wait for it
		// to clear
		while (bufferFull == true) {
			CCLog.info("Waiting for buffer to clear");
//			parent.delay(100);
		}

		// send the hpgl string to the plotter
		_mySerial.output().write(hpgl);
//		parent.delay(20); // seems to need some time to react on full
		// buffer

	}
	
	private void write(CCHPGLCommand theCommand, Object...theParameters){
		StringBuffer myCommand = new StringBuffer();
		myCommand.append(myCommand);
		for(Object myParameter:theParameters){
			myCommand.append(" ");
			myCommand.append(myParameter);
		}
		myCommand.append(";");
		
		write(myCommand.toString());
	}
	
	// Plot area and unit setting instructions
	
	/**
	 * The coordinate values used are absolute values in graphics units.
	 * Sets the location of the scaling points(P1,P2).
	 * Coordinate values for P1X,P1Y,P2X and P2Y are given as integer numbers.
	 * The IP instruction is ignored when the set coordinates are outside the print area.
	 * Using this instruction without a parameter field initializes the scaling points(P1,P2)
	 * P2X and P2Y may be omitted. (If P2X and P2Y are omitted, P2 is set automatically so as not to alter the
	 * distance between P1 and P2).
	 * @param theP1X X coordinate of P1 
	 * @param theP1Y Y coordinate of P1
	 * @param theP2X X coordinate of P2 
	 * @param theP2Y Y coordinate of P2
	 */
	public void inputScalingPoint(int theP1X, int theP1Y, int theP2X, int theP2Y){
		write(CCHPGLCommand.IP, theP1X, theP1Y, theP2X, theP2Y);
	}

	/**
	 * Sets the scale for the coordinates the user wants to establish.
	 * Coordinate values for Xmin, Xmax, Ymin, and Ymax are given as real numbers.
	 * Using this instruction without a parameter field turns the scaling off.
	 * The technical terms, user unit and graphics unit, used in this manual are defined as follows;
	 * User unit : the unit of the coordinates set by the SC instruction
	 * Graphics unit : the unit (1/1016 of an inch) of the coordinates not set by the SC instruction
	 * <pre>
	 * 10 '*** SCEX ***
	 * 20 LPRINT "IN; IP3000,2000,4500,3500;SP1;SC0,120,0,120;"
	 * 30 FOR T=0 TO 2*3.1416+3.1416/20 STEP 3.1416/20
	 * 40 X=COS(T)*100
	 * 50 Y=SIN(T)*100
	 * 60 LPRINT "PA";X;",";Y;";PD;"
	 * 70 NEXT T
	 * 80 LPRINT "PU;"
	 * 90 END
	 * </pre>
	 * @param theXMin X coordinate of P1
	 * @param theXMax X coordinate of P2
	 * @param theYMin Y coordinate of P1 
	 * @param theYMax Y coordinate of P2
	 */
	public void scale(int theXMin, int theXMax, int theYMin, int theYMax){
		write(CCHPGLCommand.SC, theXMin, theXMax, theYMin, theYMax);
	}
	
	/**
	 * This instruction sets the window inside which plotting can be performed.
	 * Graphic units are always used. Coordinate values for X1, Y1, X2, and Y2 are integer numbers from 0 to 32,767.
	 * The order of the pairs (X1, Y1) and (X2, Y2) may be reversed with no change in the window created: "IW
	 * X1, Y1, X2, Y2" is identical in effect to "IW X2, Y2, X1, Y1".
	 * Using this instruction without a parameter field releases limitations on the plot area.
	 * @param theX1 Window lower left X coordinate
	 * @param theY1 Window lower left Y coordinate
	 * @param theX2 Window upper right X coordinate
	 * @param theY2 Window upper right Y coordinate
	 */
	public void inputWindow(int theX1, int theY1, int theX2, int theY2){
		write(CCHPGLCommand.IW, theX1, theY1, theX2, theY2);
	}
	
	public void inputWindow(CCHPPaperFormat theFormat){
		switch(theFormat){
		case A4: 
			inputWindow(430,200,10430,7400);
			break;
		case A3: // a3
			inputWindow(380,430,15580,10430);
			break;
		case A: // size A
			inputWindow(80,320,10080,7520);
			break;
		case B: // size B
			inputWindow(620,80,15820,10080);
		}
	}
	
	public void noInputWindow(){
		write(CCHPGLCommand.IW);
	}
	
	/**
	 * This instruction rotates the coordinate system.
	 * A value of 0 or 90 must be used for q.
	 * Using this instruction without a parameter field sets the rotation of the coordinate system to 0 degrees.
	 * @param q  Angle in degrees through which the coordinate system is rotated
	 */
	public void rotate(int q){
		write(CCHPGLCommand.RO, q);
	}

	/**
	 * Executes a page feed
	 * After page feeding, the cursor position return to the home position (0, 0).
	 */
	public void pageFeed(){
		write(CCHPGLCommand.PG);
	}
	
	// Pen Control and Plot Instructions
	
	/**
	 * X and Y are either relative or absolute, depending on whether a PA or a PR was the last plot command
	 * executed. The absolute coordinates are set as default.
	 * Moves the cursor to the specified coordinates after raising the pen.
	 * Using this instruction without a parameter field raises the pen without changing the cursor position.
	 * When scaling is on, user coordinates are used.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When scaling is off graphics units are used.
	 * When there is no scaling, the coordinates values for X and Y are integer numbers
	 * @param theX X coordinate of the cursor movement destination
	 * @param theY Y coordinate of the cursor movement destination
	 */
	public void penUP(int theX, int theY){
		write(CCHPGLCommand.PU, theX, theY);
	}
	
	/**
	 * X and Y are either relative or absolute, depending on whether a PA or a PR was the last plot command
	 * executed. The absolute coordinates are set as default.
	 * Moves the cursor to the specified coordinates after lowering the pen. (This plots a straight line.)
	 * Using this instruction without a parameter lowers the pen without changing the cursor position. ( One dot is
	 * plotted.) When scaling has been performed, the cursor is moved by user coordinates.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the cursor is moved by absolute coordinates in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers.
	 * @param theXY XY coordinates of the cursor movement destination
	 */
	public void penDown(int...theXY){
		write(CCHPGLCommand.PD, theXY);
	}
	
	/**
	 * X and Y are absolute values in user units or graphics units.
	 * Moves the cursor to the specified coordinates.
	 * Plots a straight line only when the pen is down.
	 * When scaling has been performed, the values for X and Y are integer numbers.
	 * When there is no scaling, the cursor is moved by absolute coordinates in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers.
	 * <pre>
	 * 10 '*** PAEX1 ***
	 * 20 LPRINT "IN;SP1;"
	 * 30 LPRINT "PA2000,6000;PD0,6000,2000,7500,2000,6000;PU2500,6000;"
	 * 40 LPRINT "PAPD4500,6000,2500,7500,2500,6000;PU10365,500;"
	 * 50 END
	 * 
	 * 10 ' *** PAEX2 ***
	 * 20 LPRINT "IN;SP1;SC0,100,0,100;"
	 * 30 LPRINT "PA50,30;PD25,30,50,50,50,30;PU55,30;"
	 * 40 LPRINT "PAPD80,30,55,50,55,30,PU;"
	 * 50 END
	 * </pre>
	 * @param theXY XY coordinates of the cursor movement destination
	 */
	public void plotAbsolute(int...theXY){
		write(CCHPGLCommand.PA, theXY);
	}
	
	/**
	 * Coordinates are relative to the current position in user units or graphics units.
	 * Plots a straight line only when the pen is down.
	 * When scaling has been performed, the cursor is moved by relative coordinates in user units.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the cursor is moved by relative coordinates in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers
	 * <pre>
	 * 10 ' *** prex 1 ***
	 * 20 LPRINT "IN;SP1;"
	 * 30 LPRINT "PA5000,4500,;PDPR-2000,0,2000,2000,0,-2000;PU500,0;"
	 * 40 LPRINT "PD2000,0,-2000,2000,0,-2000;PU;"
	 * 50 END
	 * </pre>
	 * @param theXY coordinates of the cursor movement destination
	 */
	public void plotRelative(int...theXY){
		write(CCHPGLCommand.PR, theXY);
	}
	
	/**
	 * X and Y coordinates are absolute coordinates in user units or graphics units.
	 * Starting from the current position, plots an arc centred on the absolute coordinates X, Y having the specified
	 * arc angle and chord angle, with the radius being the distance between the current position and the point X,Y.
	 * After plotting, the cursor position moves to the plot end point.
	 * Plotting is performed only when the pen is down.
	 * When the pen is up, plotting is not performed, but the cursor position moves to the plot end point.
	 * When scaling has been performed, the cursor is moved by absolute coordinates in user units.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the cursor is moved by absolute coordinates in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer number.
	 * The value for pc is a clamped real number.
	 * When qc is positive, counterclockwise plotting from the current point is performed.
	 * When qc is negative, plotting is made clockwise from the current position.
	 * The value for qd is a clamped real number.
	 * When qd is not specified, the chord angle is the default value ( 5 degrees ).
	 * <pre>
	 * 10 '*** AAEX ***
	 * 120 LPRINT "IN;SP1;IP2650,1325,7650,6325;"
	 * 130 LPRINT "SC0,100,0,100;"
	 * 140 LPRINT "PA0,30;"
	 * 150 LPRINT "PD;PA0,45;AA0,50,180;PA0,70;"
	 * 160 LPRINT "AA0,100,90;PA45,100;AA50,100,180;PA70,100;"
	 * 170 LPRINT "AA100,100,90;PA100,55;AA100,50,180;PA100,30;"
	 * 180 LPRINT "AA100,0,90;PA100,55;AA100,50,180;PA70,100;"
	 * 190 LPRINT "AA100,0,90;PA55,0;AA50,0,180;PA30,0;AA0,0,90;"
	 * 1100 LPRINT "PU;PA50,50,CI20;"
	 * 1110 END
	 * </pre>
	 * @param theX Arc centre X coordinate
	 * @param theY Arc centre Y coordinate
	 * @param theQC Arc angle in degrees 
	 * @param theQD Chord angle in degrees
	 */
	public void absoluteArc(int theX, int theY, int theQC, int theQD){
		write(CCHPGLCommand.AA, theX, theY, theQC, theQD);
	}
	
	/**
	 * X and Y coordinates are relative coordinates in user units or graphics units.
	 * Starting from the current cursor position the command plots an arc whose centre is at the relative coordinate
	 * position (X,Y) and which has the specified arc and chord angles. The radius of the arc is the distance
	 * between the current position and the point (X,Y).
	 * After plotting the cursor position changes to the plot end point.
	 * Plotting is performed only when the pen is down.
	 * When the pen is up, plotting is not performed, but he cursor position moves to the plot end point.
	 * When scaling has been performed, the cursor is moved by relative coordinates in user units.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the cursor is moved by relative coordinates in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers.
	 * The value for qc is a clamped real number.
	 * When qc is positive, counterclockwise plotting from the current point is performed.
	 * When qc is negative, plotting is made clockwise from the current position.
	 * The value for qd is a clamped real number.
	 * When qd is not specified, the chord angle is the default value ( 5 degrees ).
	 * <pre>
	 * 10 '*** AREX1 ***
	 * 20 LPRINT "IN;SP1;IP2650,1325,7650,6325;"
	 * 30 LPRINT "SC-100,100,-100,100;"
	 * 40 LPRINT "PA-80,-80;PD;AR0,50,90;AR50,0,90;PU;"
	 * 50 END
	 * 
	 * 10 ' *** AREX2 ***
	 * 20 LPRINT "IN;SP1;IP2650,1325,7650,6325;"
	 * 30 LPRINT "SC-100,100,-100,100;"
	 * 40 LPRINT "PA-100,70;PD;PR30,0;AR-,-70,-90;AR70,0,90;PR60,0;PU;"
	 * 50 END
	 * 100 END
	 * </pre>
	 * @param theX Arc centre X coordinate
	 * @param theY Arc centre Y coordinate
	 * @param theQC Arc angle in degrees 
	 * @param theQD Chord angle in degrees
	 * @param theQD Chord angle in degrees
	 */
	public void relativeArc(int theX, int theY, int theQC, int theQD){
		write(CCHPGLCommand.AR, theX, theY, theQC, theQD);
	}
	
	/**
	 * Plots a circle centred on the current position with a radius r and chord angle qd.
	 * After plotting, the cursor returns to its point of origin at the centre of the circle.
	 * Plotting is performed whether the pen is up or down.
	 * When scaling has been performed, the circle is plotted in user units.
	 * Also, when scaling has been performed, the value for r is a real number.
	 * When scaling is off, the circle is plotted in graphics units.
	 * When there is no scaling, the coordinate value for r is an integer number.
	 * When qd is not specified, the chord angle is the default value (5 degrees).
	 * <pre>
	 * 10 '*** CIEX1 ***
	 * 20 LPRINT "IN;SP1;IP2650,1325,7650,6325;"
	 * 30 LPRINT "SC-100,100,-100,100;"
	 * 40 LPRINT "PA-60,50;CI40,45;"
	 * 50 LPRINT "PA60,50;CI40,30;"
	 * 60 LPRINT "PA-60,-50;CI40,15;"
	 * 70 LPRINT "PA60,-50;CI40,5;"
	 * 80 END
	 * 
	 * 10 '*** CIEX2 ***
	 * 20 LPRINT "IN;SP1;IP2650,1325,8650,7325;"
	 * 30 LPRINT "SC0,170,0,170;"
	 * 40 LPRINT "PA100,100;LT;CI10,5;LT0;CI-20,5;LT1;CI30,5;"
	 * 50 LPRINT "LT2;CI-40,5;LT3;CI50,5;LT4;CI-
	 * 60,5;LT5;CI70,5;LT6;CI80,5;"
	 * 60 END
	 * 
	 * 10 '*** CIEX3 ***
	 * 20 LPRINT "IN;SP1;IP2650,1325,7650,6325;"
	 * 30 LPRINT "SC-1000,1000,-1000,1000;"
	 * 40 LPRINT "PA-800,800;"
	 * 50 GOSUB 130
	 * 60 LPRINT "PA200,800;"
	 * 70 GOSUB 130
	 * 80 LPRINT "PA-800,-200;"
	 * 90 GOSUB 130
	 * 100 LPRINT"PA200,-200;"
	 * 110 GOSUB 130
	 * 120 END
	 * 130 LPRINT "CI70;PR600,0;CI70;PR-300,-300;CI250;"
	 * 140 LPRINT "PR-300,-300;CI70;PR600,0;CI70;"
	 * 150 RETURN
	 * </pre>
	 * @param theR Radius of circle ( in user units or graphic units )
	 * @param theQD  Chord angle ( in degrees )
	 */
	public void circlePlot(int theR, int theQD){
		write(CCHPGLCommand.CI, theR, theQD);
	}
	
	// The polygon group
	
	/**
	 * X and Y coordinates are absolute coordinates in user units or graphics units.
	 * Plots the rectangle formed by the current position and the opposite angle specified by X and Y.
	 * After plotting the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down.
	 * When scaling has been performed, the rectangle is plotted in user units.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the rectangle is plotted in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers.
	 * <pre>
	 * 10 '*** EAEX ***
	 * 20 LPRINT "IN;SP1;PA7000,4000;"
	 * 30 LPRINT "PT.3;FT1;RA6000,3000;"
	 * 40 LPRINT "SP3,;EA6000,3000;"
	 * 50 LPRINT "SP4;FT3,100;RA8000,3000;"
	 * 60 LPRINT "SP3,;EA8000,3000;"
	 * 70 LPRINT "SP5;PT.3;FT2;RA8000,5000;"
	 * 80 LPRINT "SP3;EA8000,5000;"
	 * 90 LPRINT "SP6;FT4,100,45;RA6000,5000;"
	 * 100 LPRINT "SP3;EA6000,5000;PG"
	 * 110 END
	 * </pre>
	 * @param theX X coordinate of opposite angle for the rectangle
	 * @param theY Y coordinate of opposite angle for the rectangle
	 */
	public void edgeRectangleAbsolute(int theX, int theY){
		write(CCHPGLCommand.EA, theX, theY);
	}
	
	/**
	 * Coordinates are relative to the current position in user units or graphics units.
	 * Plots the rectangle formed by the current position and the opposite angle specified by X and Y.
	 * After plotting the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down.
	 * When scaling has been performed, the rectangle is plotted in user units.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the rectangle is plotted in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers.
	 * <pre>
	 * 10 '*** EREX ***
	 * 20 LPRINT "IN;SP1;PA5000,5000;"
	 * 30 LPRINT "PT.3;FT1;RR500,500;"
	 * 40 LPRINT "SP3,;ER500,500;"
	 * 50 LPRINT "PR500,0"
	 * 60 LPRINT "SP4;FT3,;RR500,500;"
	 * 70 LPRINT "SP3,;ER500,500;"
	 * 80 LPRINT "PR0,500;"
	 * 90 LPRINT "SP5;PT.3;FT2;RR500,500;"
	 * 100 LPRINT "SP3;ER500,500;"
	 * 110 LPRINT "SP6;FT4,100,45;RR-500,500;"
	 * 120 LPRINT "SP3;ER-500,500;PG"
	 * 130 END
	 * </pre>
	 * @param theX X coordinate of opposite angle for the rectangle
	 * @param theY Y coordinate of opposite angle for the rectangle
	 */
	public void edgeRectangleRelative(int theX, int theY){
		write(CCHPGLCommand.ER, theX, theY);
	}
	
	/**
	 * Plots a wedge centred on the current position with radius r, start point angle q1, arc angle qc, and chord angle
	 * qd.
	 * After plotting, the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down.
	 * When scaling has been performed, the circle is plotted in user units.
	 * Also, when scaling has been performed, the value for r is a real number.
	 * When there is no scaling, the circle is plotted in graphics units.
	 * When there is no scaling, the coordinate value for r is an integer number.
	 * The value for q1 is a clamped real number.
	 * q1 specifies the wedge starting point related to the 0 degree reference point.
	 * When q1 is positive, the positive direction of the X axis relative to the current position is set at 0 degrees, and
	 * the start point is sought in the counterclockwise direction. The opposite occurs when q1 is negative: the
	 * negative X axis is set at 0 degrees, and the start point is sought by going clockwise.
	 * qc specifies the angle of the wedge in degrees.
	 * The value for qc is a real number.
	 * Plotting proceeds counterclockwise when qc is positive, and clockwise when negative.
	 * The value for qd is a clamped real number.
	 * When qd is not specified, the chord angle is the default value ( 5 degrees )
	 * <pre>
	 * 10 ' *** EWEX ***
	 * 20 LPRINT "IN;SP2;FT3,100;"
	 * 30 LPRINT "PA5000,4000;"
	 * 40 LPRINT "WG1250,90,180,5;"
	 * 50 LPRINT "SP3;EW1250,90,180,5;"
	 * 60 LPRINT "SP4,FT4,100,45;"
	 * 70 LPRINT "WG1250,270,120;"
	 * 80 LPRINT "SP3;EW1250,270,120;"
	 * 80 LPRINT "SP1;PT.3;FT1;"
	 * 100 LPRINT "WG1250,30,60;"
	 * 110 LPRINT "SP3;EW1250,30,60;PG;"
	 * 120 END
	 * </pre>
	 * @param theR Radius in user units or graphics units 
	 * @param theQ1 Start point angle
	 * @param theQC Arc angle 
	 * @param theQD Chord angle
	 */
	public void edgeWedge(int theR, int theQ1, int theQC, int theQD){
		write(CCHPGLCommand.EW, theR, theQ1, theQC, theQD);
	}
	
	/**
	 * X and Y coordinates are absolute coordinates in user units or graphics units.
	 * Fill in the rectangle formed by the current position and the opposite angle specified by X and Y.
	 * After plotting, the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down.
	 * When scaling has been performed, the rectangle is plotted in user units.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the rectangle is plotted in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers.
	 * <pre>
	 * 10 '*** RAEX ***
	 * 20 LPRINT "IN;SP1;PA5000,4000;"
	 * 30 LPRINT "PT.3;FT1;RA4250,3250;"
	 * 40 LPRINT "FT3,100;RA5750,3250;"
	 * 50 LPRINT "FT2;RA5750,4750;"
	 * 60 LPRINT "FT4,100,45;RA4250,4750;"
	 * 70 END
	 * </pre>
	 * @param theX X coordinate of opposite angle for the rectangle
	 * @param theY Y coordinate of opposite angle for the rectangle
	 */
	public void fillRectangleAbsolute(int theX, int theY){
		write(CCHPGLCommand.RA, theX, theY);
	}
	
	/**
	 * Coordinates are relative to the current position in user units or graphics units.
	 * Fill in the rectangle formed by the current position and the opposite angle specified by X and Y.
	 * After plotting the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down.
	 * When scaling has been performed, the rectangle is plotted in user units.
	 * Also, when scaling has been performed, the values for X and Y are real numbers.
	 * When there is no scaling, the rectangle is plotted in graphics units.
	 * When there is no scaling, the coordinate values for X and Y are integer numbers
	 * <pre>
	 * 10 '*** PREX ***
	 * 20 LPRINT "IN;SP1;PA5000,5000;"
	 * 30 LPRINT "PT.3;FT1;RR500,500;"
	 * 35 LPRINT "PR500,0;"
	 * 40 LPRINT "FT3,70;RR500,500;"
	 * 45 LPRINT "PR0,500;"
	 * 50 LPRINT "FT2;RR500,500;"
	 * 60 LPRINT "FT4,70,45;RR-500,500;"
	 * 70 END
	 * </pre>
	 * @param theX X coordinate of opposite angle for the rectangle
	 * @param theY Y coordinate of opposite angle for the rectangle
	 */
	public void fillRectangleRelative(int theX, int theY){
		write(CCHPGLCommand.RR, theX, theY);
	}
	
	/**
	 * Fill in a wedge centred on the current position with radius r, start point angle q1, arc angle qc, and chord
	 * angle qd.
	 * After plotting, the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down.
	 * When scaling has been performed, the circle is plotted in user units.
	 * Also, when scaling has been performed, the value for r is a real number.
	 * When there is no scaling, the circle is plotted in graphics units.
	 * When there is no scaling, the coordinate value for r is an integer number.
	 * The value for q1 is a clamped real number.
	 * When q1 is positive, the positive direction of the X axis relative to the current position is set at 0 degrees, and
	 * the start point is sought in the counterclockwise direction. The opposite occurs when q1 is negative: the
	 * negative X axis is set at 0 degrees, and the start point is sought by going clockwise.
	 * The value for qc is a clamped real number.
	 * Plotting proceeds counterclockwise when qc is positive, and clockwise when negative.
	 * The value for qd is a clamped real number.
	 * When qd is not specified, the chord angle is the default value ( 5 degrees ).
	 * <pre>
	 * 10 ' *** WGEX ***
	 * 20 LPRINT "IN;SP2;FT3,100;"
	 * 30 LPRINT "PA5000,4000;"
	 * 40 LPRINT "WG1250,90,180,5;"
	 * 50 LPRINT "SP4;FT4,100,45;"
	 * 60 LPRINT "WG1250,270,120;"
	 * 70 LPRINT "SP1;PT.3;FT1;"
	 * 80 LPRINT "WG1250,30,60;PG;"
	 * 90 END
	 * </pre>
	* @param theR Radius in user units or graphics units 
	 * @param theQ1 Start point angle
	 * @param theQC Arc angle 
	 * @param theQD Chord angle
	 */
	public void fillWedge(int theR, int theQ1, int theQC, int theQD){
		write(CCHPGLCommand.WG, theR, theQ1, theQC, theQD);
	}
	
	// Plot Function Instructions
	
	/**
	 * It is used together with FP, RA, RR, and WG command, and model of shading (painting out and hatching) is specified.
	 *FT (model (,space (,angle)));
	 *Model is as follows. Initial value is {@linkplain CCHPGLFillType#SOLID_LINES_BIDIRECTIONAL}.
	 * @author christianr
	 *
	 */
	public static enum CCHPGLFillType{
		/**
		 * Painting out interactive at space specified by PT command (FT command interval and angle are ignored)
		 */
		SOLID_LINES_BIDIRECTIONAL(1),
		/**
		 * It is painting out (FT command space and angle are ignored) of the single direction at space specified by PT command.
		 */
		SOLID_LINES_SINGLE_DIRECTIONAL(2),
		/**
		 * Hatching which is the single direction at space and angle which were specified by FT command
		 */
		PARALLEL_LINES(3),
		/**
		 * It is crossing hatching at space and angle which were specified by FT command.
		 */
		CROSS_HATCHING(4);
		
		private int id;
		
		private CCHPGLFillType(int theID){
			id = theID;
		}
	}
	
	/**
	 * Sets the fill type, interval, and angle when filling an area.
	 * The fill interval when when n is 1 or 2 is the interval set by the pen thickness (PT) instruction.
	 * Any value given for d when n is 1 or 2 will be ignored.
	 * If d is omitted, the fill interval already specified will be used.
	 * If d is 0, the default value will be used ( 1% of distance from P1 to P2. )
	 * The value of d is a clamped real number .
	 * If q is omitted, the fill angle already specified will be used.
	 * The value of q is a clamped real number.
	 * @param theFillType Fill type
	 * @param theFillInterval Fill interval (interval between the parallel lines of the area being filled)
	 * @param theFillAngle  Fill angle (degrees )
	 */
	public void fillType(CCHPGLFillType theFillType, int theFillInterval, int theFillAngle){
		write(CCHPGLCommand.FT, theFillType.id, theFillInterval, theFillAngle);
	}
	
	public static enum CCHPGLLinePattern{
		/**
		 * Point is plotted at specifying point
		 */
		POINT(0), 
		/**
		 * Dotted line of point
		 */
		DOTTED(1),
		/**
		 * Short dotted line
		 */
		SHORT_DOTTED(2), 
		/**
		 * Long dotted line
		 */
		LONG_DOTTED(3), 
		/**
		 * Short dashed line
		 */
		SHORT_DASHED(4), 
		/**
		 * Long dashed line
		 */
		LONG_DASHED(5),
		/**
		 * Two-point phantom line
		 */
		TWO_POINT_PHANTOM(6);
		
		private int id;
		
		private CCHPGLLinePattern(int theID){
			id = theID;
		}
	}
	
	/**
	 * Specifies the line type and pattern length.
	 * When the n parameter field is omitted, a solid line is selected.
	 * When the p parameter field is omitted, the pattern length is 4% of the distance between P1 and P2 (default
	 * value ).
	 * The value of n is a clamped integer number.
	 * The value of p is a clamped real number from 0.0000 to 127.9999.
	 * When p is omitted, the previously set line pattern length is used.
	 * @param theLinePattern Line pattern number
	 * @param thePatternLength  Line pattern length (percentage or millimeters of distance between P1 and P2)
	 */
	public void lineType(CCHPGLLinePattern theLinePattern, int thePatternLength){
		write(CCHPGLCommand.LT, theLinePattern.id, thePatternLength);
	}
	
	/**
	 * This command specifies the width of the currently selected pen.
	 * The value of w is an integer number from 1 to 10
	 * @param theWidth width (unit = 1/300 inch)
	 */
	public void penWidth(int theWidth){
		write(CCHPGLCommand.PW, theWidth);
	}
	
	/**
	 * The command specifies the symbol to be drawn.
	 * When the PA,PR,PD or PU instruction is used, the specified symbol will be drawn at the end of each vector.
	 * The specified symbol will be drawn at the end of each vector even if the pen is up when the PA or PR
	 * instructions are used.
	 * Omitting the parameter field cancels the symbol mode.
	 * <pre>
	 * 10 '*** SMEX ***
	 * 20 LPRINT "IN;SP1;SM*;PA500,1500;"
	 * 30 LPRINT "PD600,1590,670,1860,850,1960,1320,1900,1940,2350:"
	 * 40 LPRINT "PU;SM;PA500,500;SM3;"
	 * 50 LPRINT "PA550,800,680,720,800,950,1150,1230,1870,1350;PU;"
	 * 60 LPRINT "SM;PA1850,600;PD;SMY;PA3000,1450;"
	 * 70 LPRINT "SMZ;PA3300,1150;SMX;PA1850,600;PU;"
	 * 80 END
	 * </pre>
	 * @param theChar ASCII character or symbol code
	 */
	public void symbolMode(char theChar){
		write(CCHPGLCommand.SM, theChar);
	}
	
	/**
	 * Cancels the symbol mode
	 */
	public void noSymbolMode(){
		write(CCHPGLCommand.SM);
	}
	
	/**
	 * Selects the pen specified by the pen number.
	 * The value for n must be an integer from 0 to 6
	 * @param thePen Pen number
	 */
	public void selectPen(int thePen){
		write(CCHPGLCommand.SP, thePen);
	}
	
	/**
	 * Tick length is a percentage of the vertical and horizontal distances between P1 and P2.
	 * Sets the length of tick marks for the XT and YT instructions.
	 * Values for l1 and l2 are clamped real numbers.
	 * When the parameter field is omitted, the default values for tick length are used(for both l1 and l2, these are
	 * 0.5% of the horizontal and vertical distances between P1 and P2).
	 * <pre>
	 * 10 '*** TLEX ***
	 * 30 FOR I=1 TO 10
	 * 40 LPRINT "PR800,0;XT;"
	 * 50 NEXT I
	 * 60 LPRINT "TL;PU;PA300,279;PD;"
	 * 70 GOSUB 1000
	 * 80 LPRINT "TL1,0;PU;PA1100,279;PD;"
	 * 20 LPRINT "IN;PA300,279;SP2;PD;TL90;XT;";
	 * 90 GOSUB 1000
	 * 100 LPRINT "TL0,5;PU;PA1900,279;"
	 * 110 GOSUB 1000
	 * 120 LPRINT "PA300,6759;TL80;YT;PU;"
	 * 130 END
	 * 1000 '* SUBROUTINE DRAW TICKS *
	 * 1010 FOR J=1 TO 8
	 * 1020 LPRINT "PRO,720;YT;"
	 * 1030 NEXT J
	 * 1040 RETURN
	 * </pre>
	 * @param theL1 Length of ticks in the positive X- and Y-axes
	 * @param theL2 Length of ticks in the negative X- and Y-axes
	 */
	public void tickLength(int theL1, int theL2){
		write(CCHPGLCommand.TL, theL1, theL2);
	}
	
	/**
	 * Plots vertical tick marks as specified by the TL instruction from the current position.
	 * After plotting, the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down.
	 */
	public void xTick(){
		write(CCHPGLCommand.XT);
	}
	
	/**
	 * Plots horizontal tick marks as specified by the TL instruction from the current position.
	 * After plotting, the cursor returns to its point of origin.
	 * Plotting is performed whether the pen is up or down
	 * <pre>
	 * 10 '*** XTYTEX ***
	 * 20 LPRINT "IN;PA300,279;SP2;PD";
	 * 30 LPRINT "PR1300,0;XT;PR1300,0;XT;PU;"
	 * 40 END
	 * </pre>
	 */
	public void yTick(){
		write(CCHPGLCommand.YT);
	}
	
	/**
	 * Sets the line interval when filling in with solid lines.
	 * The value for d is a clamped number from 0.0000 to 5.0000.
	 * The default value (0.3 mm) is in effect when d is omitted.
	 * @param theD Fill line interval (mm)
	 */
	public void penThicknessSelect(double theD){
		write(CCHPGLCommand.PT,theD);
	}
	
	// Character Plot Instructions
	
	/**
	 * Character set number(*)
	 * @param theN
	 */
	public void characterSet(int theN){
		
	}
	 
	public void point(int theX, int theY){
		penUP(theX, theY);
		penDown();
	}
	
	public void circle(int theX, int theY, int theRadius) {
		penUP(theX, theY);
		circlePlot(theRadius, 5);
	}

	public void circle(int theX, int theY, int theRadius, int theQD) {
		penUP(theX, theY);
		circlePlot(theRadius, theQD);
	}
	
	public void rectStroke(int theX, int theY, int theWidth, int theHeight){
		penUP(theX, theY);
		edgeRectangleRelative(theWidth, theHeight);
	}
	
	public int getAvailableBuffer() {
		int freeBuffer = 0;
		// send the command to retrieve the buffer size from the plotter
		// get the current buffer size
		_mySerial.output().write(ESC_CHAR + ".B");

		long timeoutStart = System.currentTimeMillis();
		int responseWaitTime = 1000; // how long time we will wait for the
		// plotter to respond
		CCLog.info("Waiting on plotter response");
		while (_mySerial.input().available() < 1) {
			if ((System.currentTimeMillis() - timeoutStart) > responseWaitTime) {
				// if the plotter has not responded in the alloted time
				// we
				// break out and throws a error.
				CCLog.info("Plotter did not respond.");
				return 0;
			}
		}

		byte[] inBuffer = new byte[7];
		while (_mySerial.input().available() > 0) {
			inBuffer = _mySerial.input().readBytesUntil(0x0D);

			if (inBuffer != null) {

				String bufferAsString = new String(inBuffer);
				String digits = bufferAsString.replaceAll("[^0-9]", "");

				freeBuffer = Integer.parseInt(digits);
				CCLog.info("Free buffer:" + freeBuffer);

			} else {
				CCLog.info("Null returned from Plotter");
			}
		}

		return freeBuffer;
	}

	// UTILITY

	// LABELS

	public void writeLabel(String _label, int theXpos, int theYpos) {
		write("PU" + theXpos + "," + theYpos + ";"); // Position pen
		write("LB" + _label + END_OF_TEXT); // Draw label
	}

	// set in cm
	public void setFontHeight(double h) {
		characterWidth = h;
		characterHeight = h;
		write("SI" + characterWidth + "," + characterHeight + ";");
	}

	public double getFontHeight() {
		return characterWidth;
	}

	public void labelDirection(double angle) {
		// the thePrecision in our conversion is 4 decimals
		// which is higher then the plotters resolution
		String c = CCFormatUtil.nf(CCMath.cos(CCMath.radians(angle)), 1, 4);
		String s = CCFormatUtil.nf(CCMath.sin(CCMath.radians(angle)), 1, 4);
		write("DR" + c + "," + s + ";");
	}

	public void writeLabel(String _label, double theXpos, double theYpos) {
		writeLabel(_label, (int) theXpos, (int) theYpos);
	}

	// MOVEMENT

	public void moveTo(int theXpos, int theYpos) {
		write("PU" + theXpos + "," + theYpos + ";"); // Go to specified position

	}

	public void moveTo(double theXpos, double theYpos) {
		moveTo((int) theXpos, (int) theYpos);
	}

	public void lineTo(int theXpos, int theYpos) {
		write("PD" + theXpos + "," + theYpos + ";"); // Go to specified position
	}

	public void lineTo(double theXpos, double theYpos) {
		lineTo((int) theXpos, (int) theYpos);
	}

	// SHAPES

	public void line(double theX0, double theY0, double theX1, double theY1) {
		// _this.line(theX0 / 10, theY0 / 10, theX1 / 10, theY1 / 10);
		write("PU" + (int) theX0 + "," + (int) theY0 + ";");
		write("PD" + (int) theX1 + "," + (int) theY1 + ";");
	}

	public void bezier(int theX0, int theY0, int theX1, int theY1, int theX2, int theY2, int theX3, int theY3) {

		double increment = 1.0 / bezierDetailLevel;
		moveTo(theX0, theY0);

		for (double t = 0.00; t < 1.01; t = t + increment) {
			double myX = CCMath.bezierPoint(theX0, theX1, theX2, theX3, t);
			double myY = CCMath.bezierPoint(theY0, theY1, theY2, theY3, t);
			lineTo(myX, myY);
		}
	}

	public void bezierDetail(double _detail) {
		bezierDetailLevel = _detail;
	}

}
