package cc.creativecomputing.kle.analyze;

import cc.creativecomputing.core.CCProperty;

public class CCAnalyzeSettings{

	@CCProperty(name = "history size", min = 1, max = 500) int _cHistorySize = 1;
	@CCProperty(name = "time based") boolean _cTimeBased = false;
	@CCProperty(name = "time scale", min = 1, max = 300) double _cTimeScale = 300;
	@CCProperty(name = "time offset", min = 0, max = 1800)double _cTimeOffset = 0;
	@CCProperty(name = "fade out") boolean _cFadeOut = false;
	@CCProperty(name = "alpha", min = 0, max = 1) float _cAlpha = 1f;
	@CCProperty(name = "draw violations")boolean _cDrawViolations = true;
	@CCProperty(name = "draw turns")boolean _cDrawTurns = true;
	@CCProperty(name = "draw values")boolean _cDrawValues = true;
	@CCProperty(name = "violation point size", min = 1, max = 20)double _cViolationPointSize = 5;
	@CCProperty(name = "draw points")boolean _cDrawPoints = false;
	@CCProperty(name = "point size", min = 1, max = 20)double _cPointSize;
}