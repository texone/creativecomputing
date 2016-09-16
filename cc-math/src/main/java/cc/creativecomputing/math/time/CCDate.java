package cc.creativecomputing.math.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

public class CCDate {

//	public static String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	private static DateFormat OUTPUT_FORMAT = new SimpleDateFormat(ISO_8601_FORMAT);
	
	/**
     * first month of the year in the Gregorian and Julian calendars.
     */
    public final static int JANUARY = 0;

    /**
     * second month of the year in the Gregorian and Julian calendars.
     */
    public final static int FEBRUARY = 1;

    /**
     * third month of the year in the Gregorian and Julian calendars.
     */
    public final static int MARCH = 2;

    /**
     * 
     * fourth month of the year in the Gregorian and Julian calendars.
     */
    public final static int APRIL = 3;

    /**
     * fifth month of the year in the Gregorian and Julian calendars.
     */
    public final static int MAY = 4;

    /**
     * sixth month of the year in the Gregorian and Julian calendars.
     */
    public final static int JUNE = 5;

    /**
     * seventh month of the year in the Gregorian and Julian calendars.
     */
    public final static int JULY = 6;

    /**
     * eighth month of the year in the Gregorian and Julian calendars.
     */
    public final static int AUGUST = 7;

    /**
     * ninth month of the year in the Gregorian and Julian calendars.
     */
    public final static int SEPTEMBER = 8;

    /**
     * 
     * tenth month of the year in the Gregorian and Julian calendars.
     */
    public final static int OCTOBER = 9;

    /**
     * eleventh month of the year in the Gregorian and Julian calendars.
     */
    public final static int NOVEMBER = 10;

    /**
     * twelfth month of the year in the Gregorian and Julian calendars.
     */
    public final static int DECEMBER = 11;
	
    /**
	 * Returns the shortest day of the month
	 * @return shortest day of the month
	 */
	public static CCDate shortestDay(){
		CCDate myResult = new CCDate();
		myResult.month(11);
		myResult.day(21);
		return myResult;
	}
	
	/**
	 * Returns the longest day of the month
	 * @return longest day of the month
	 */
	public static CCDate longestDay() {
		CCDate myResult = new CCDate();
		myResult.month(5);
		myResult.day(21);
		return myResult;
	}
	
	public static CCDate createFromDoubleTime(double theTime){
		CCDate myResult = new CCDate();
		myResult.fromDoubleTime(theTime);
		return myResult;
	}
	
	public static CCDate createFromJulianDate(double theJulianDate){
		CCDate myResult = new CCDate();
		myResult.fromJulianDate(theJulianDate);
		return myResult;
	}

	public static CCDate blend(CCDate theStartDate, CCDate theEndDate, double theBlend){
		long myBlendedMillis = CCMath.blend(theStartDate.timeInMilliSeconds(), theEndDate.timeInMilliSeconds(), theBlend);
		return new CCDate(myBlendedMillis);
	}
	
	private Calendar _myCalendar;
	
	public CCDate(long theTime){
		this();
		_myCalendar.setTimeInMillis(theTime);
		
	}
	
	public CCDate(Date theDate){
		this();
		_myCalendar.setTime(theDate);
	}
	
	public CCDate(){
		_myCalendar = new GregorianCalendar ();
	}
	
	public CCDate(int theYear, int theMonth, int theDate){
		this();
		set(theYear, theMonth, theDate);
	}
	
	public CCDate(int theYear, int theMonth, int theDate, int theHour, int theMinute){
		this();
		set(theYear, theMonth, theDate, theHour, theMinute);
	}
	
	public CCDate(int theYear, int theMonth, int theDate, int theHour, int theMinute, int theSecond){
		this();
		set(theYear, theMonth, theDate, theHour, theMinute, theSecond);
	}
	
	public CCDate(String theDate, String thePattern){
		this();
		DateFormat myFormat = new SimpleDateFormat(thePattern);
		try {
			_myCalendar.setTime(myFormat.parse(theDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CCDate clone(){
		CCDate myResult = new CCDate();
		myResult.timeInMilliSeconds(timeInMilliSeconds());
		return myResult;
	}
	
	public void clear(){
		_myCalendar.clear();
	}
	
	public void year(int theYear){
		_myCalendar.set(Calendar.YEAR, theYear);
	}
	
	public int year(){
		return _myCalendar.get(Calendar.YEAR);
	}
	
	/**
	 * Sets the month of the date. The first month of
     * the year is <code>JANUARY</code> which is 0.
	 * @param theMonth
	 */
	public void month(int theMonth){
		_myCalendar.set(Calendar.MONTH, theMonth);
	}
	
	public int month(){
		return _myCalendar.get(Calendar.MONTH);
	}
	
	/**
	 * The first day of the month has value 1.
	 * @param theDay
	 */
	public void day(int theDay){
		_myCalendar.set(Calendar.DAY_OF_MONTH, theDay);
	}
	
	public int day(){
		return _myCalendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public void dayOfYear(int theDay){
		_myCalendar.set(Calendar.DAY_OF_YEAR, theDay);
	}
	
	public int dayOfYear(){
		return _myCalendar.get(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * Set the hour of the day for the 24-hour clock.
     * E.g., at 10:04:15.250 PM the hour is 22.
	 * @param theHour
	 */
	public void hours(int theHour){
		_myCalendar.set(Calendar.HOUR_OF_DAY, theHour);
	}
	
	public int hours(){
		return _myCalendar.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * Sets the minute within the hour.
     * E.g., at 10:04:15.250 PM the <code>MINUTE</code> is 4.
	 * @param theMinute
	 */
	public void minutes(int theMinute){
		_myCalendar.set(Calendar.MINUTE, theMinute);
	}
	
	public int minutes(){
		return _myCalendar.get(Calendar.MINUTE);
	}
	
	/**
	 * Sets the second within the minute.
     * E.g., at 10:04:15.250 PM the <code>SECOND</code> is 15.
	 * @param theSecond
	 */
	public void seconds(int theSecond){
		_myCalendar.set(Calendar.SECOND, theSecond);
	}
	
	public int seconds(){
		return _myCalendar.get(Calendar.SECOND);
	}
	
	public int milliSeconds(){
		return _myCalendar.get(Calendar.MILLISECOND);
	}
	
	public double hourProgress(){
		return (minutes() * 60 + seconds() + milliSeconds() / 1000d) / (60d * 60d);
	}
	
	public double dayProgress(){
		return (((hours() * 60 + minutes()) * 60) + seconds() + milliSeconds() / 1000d) / (24d * 60d * 60d);
	}
	
	public double yearProgress(){
		return _myCalendar.get(Calendar.DAY_OF_YEAR) / 365d;
	}
	
	public void set(int theYear, int theMonth, int theDate){
		_myCalendar.set(theYear, theMonth, theDate);
	}
	
	public void set(int theYear, int theMonth, int theDate, int theHour, int theMinute){
		_myCalendar.set(theYear, theMonth, theDate, theHour, theMinute);
	}
	
	public void set(int theYear, int theMonth, int theDate, int theHour, int theMinute, int theSecond){
		_myCalendar.set(theYear, theMonth, theDate, theHour, theMinute, theSecond);
	}
	
	/**
     * Returns this Calendar's time value in milliseconds.
     *
     * @return the current time as UTC milliseconds
     */
	public long timeInMilliSeconds(){
		return _myCalendar.getTimeInMillis();
	}
	
	public void timeInMilliSeconds(long theTime){
		_myCalendar.setTimeInMillis(theTime);
	}
	
	private static int  J1970 = 2440588;
	private static double msInDay = 1000 * 60 * 60 * 24;
	
	public double toJulianDate() { 
		return timeInMilliSeconds() / msInDay - 0.5 + J1970; 
	}
	
	public void fromJulianDate(double j ) { 
		_myCalendar.setTimeInMillis((long)((j + 0.5 - J1970) * msInDay)); 
	}
	
	public CCDate fromDoubleTime(double theTime){
		hours(CCMath.floor(theTime));
		
		double m = CCMath.frac(theTime) * 60;
		minutes(CCMath.floor(m));
		double s = CCMath.frac(m) * 60;
		seconds(CCMath.floor(s));
		
		return this;
	}
	
	public void fromDoubleDay(double theDay){
		_myCalendar.set(Calendar.DAY_OF_YEAR, (int)theDay);
	}
	
	public int timezoneOffset(){
		return -(_myCalendar.get(Calendar.ZONE_OFFSET) + _myCalendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
	}
	
	/**
	 * Returns whether this date is before the given date
	 * @param theDate the date to check
	 * @return <code>true</code> if this date is before the given date <code>false</code> otherwise
	 */
	public boolean before(CCDate theDate){
		return _myCalendar.before(theDate._myCalendar);
	}
	
	/**
	 * Returns whether this date is after the given date
	 * @param theDate the date to check
	 * @return <code>true</code> if this date is after the given date <code>false</code> otherwise
	 */
	public boolean after(CCDate theDate){
		return _myCalendar.after(theDate._myCalendar);
	}
	
	/**
	 * Returns whether this date is between the given dates
	 * @param theDate0 the date to check
	 * @param theDate1 the date to check
	 * @return <code>true</code> if this date is between the given dates <code>false</code> otherwise
	 */
	public boolean between(CCDate theDate0, CCDate theDate1){
		if(theDate1.after(theDate0))return after(theDate0) && before(theDate1);
		
		return after(theDate1) && before(theDate0);
	}
	
	public static void main(String[] args) {
//		System.out.println(Calendar.getInstance());
		CCLog.info(1463520941000l + "");
		CCLog.info(new CCDate(1458518390).timeInMilliSeconds());
		CCLog.info(new CCDate(1463520941000l).toString());

		CCLog.info(new CCDate().timeInMilliSeconds() + ":" + 1463520941);
	}
	
	@Override
	public String toString() {
		return OUTPUT_FORMAT.format(_myCalendar.getTime());
	}
}
