package me.lima;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class help provide thread-safe date/time parsing.
 * The Java default DateTimeFormat is not thread safe. Lots of programmers make
 * mistake on this.
 * The util class is hoping to help resolve the issue by providing simple and easy
 * interfaces for them.
 *
 * @author Li Ma
 *
 */
public class ThreadSafeDateParser {
	private static final ThreadLocal<Map<String, DateFormat>> PARSERS = new ThreadLocal<Map<String, DateFormat>>() {
		protected Map<String, DateFormat> initialValue() {
			return new HashMap<String, DateFormat>();
		}
	};
	static private final DateFormat getParser(final String Pattern) {
		Map<String, DateFormat> parserMap = PARSERS.get();
		DateFormat df = parserMap.get(Pattern);
		if ( null == df ){
			//if parser for the same pattern does not exist yet, create one and save it into map
			df = new SimpleDateFormat(Pattern);
			parserMap.put(Pattern, df);
		}
		return df;
	}
	
	/**
	 * Static Public and Thread-Safe method to parse a date from the give String
	 * @param StrDate input string to parse
	 * @param Pattern date format pattern of the input string
	 * @return Date value of the input string
	 * @throws ParseException If parse exception happened
	 */
	static public Date parse(final String StrDate, final String Pattern ) throws ParseException{
		return getParser(Pattern).parse(StrDate);
	}
	
	/**
	 * Static Public and Thread-Safe method to parse a date from the give String
	 * and return the long value of the result
	 * @param StrDate input string to parse
	 * @param Pattern date format pattern of the input string
	 * @return Long date value of the input string
	 * @throws ParseException If parse exception happened
	 * @throws ParseException
	 */
	static public long parseLongDate(final String StrDate, final String Pattern) throws ParseException{
		return parse(StrDate, Pattern).getTime();
	}
	
	/**
	 * A thread-safe method to format a given Date based-on the given pattern
	 * @param TheDate Date to be formatted
	 * @param Pattern Pattern used to format the date
	 * @return String of formatted date
	 */
	static public String format(final Date TheDate, final String Pattern){
		return getParser(Pattern).format(TheDate);
	}
	
	/**
	 * A thread-safe method to format a given Date(in long) based-on the given pattern
	 * @param TheDate Date in long to be formatted
	 * @param Pattern Pattern used to format the date
	 * @return String of formatted date
	 */
	static public String format(final long TheDate, final String Pattern){
		return getParser(Pattern).format(new Date(TheDate));
	}
	
	static public void close() {
		PARSERS.get().clear();
		PARSERS.remove();
	}
}