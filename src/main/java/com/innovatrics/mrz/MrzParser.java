/**
 * Java parser for the MRZ records, as specified by the ICAO organization.
 * Copyright (C) 2011 Innovatrics s.r.o.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.innovatrics.mrz;

import com.innovatrics.mrz.types.MrzDate;
import com.innovatrics.mrz.types.MrzFormat;
import com.innovatrics.mrz.types.MrzSex;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses the MRZ records.
 *
 * @author Martin Vysny
 */
public class MrzParser {

	private static final Logger LOG = LoggerFactory.getLogger(MrzParser.class);

	private static final int[] MRZ_WEIGHTS = new int[]{7, 3, 1};
	private static final Map<String, String> EXPAND_CHARACTERS = new HashMap<String, String>();
	/**
	 * The filler character, '&lt;'.
	 */
	public static final char FILLER = '<';

	static {
		EXPAND_CHARACTERS.put("Ä", "AE"); // Ä
		EXPAND_CHARACTERS.put("ä", "AE"); // ä
		EXPAND_CHARACTERS.put("Å", "AA"); // Å
		EXPAND_CHARACTERS.put("å", "AA"); // å
		EXPAND_CHARACTERS.put("Æ", "AE"); // Æ
		EXPAND_CHARACTERS.put("æ", "AE"); // æ
		EXPAND_CHARACTERS.put("Ĳ", "IJ"); // Ĳ
		EXPAND_CHARACTERS.put("ĳ", "IJ"); // ĳ
		EXPAND_CHARACTERS.put("Ö", "OE"); // Ö
		EXPAND_CHARACTERS.put("ö", "OE"); // ö
		EXPAND_CHARACTERS.put("Ø", "OE"); // Ø
		EXPAND_CHARACTERS.put("ø", "OE"); // ø
		EXPAND_CHARACTERS.put("Ü", "UE"); // Ü
		EXPAND_CHARACTERS.put("ü", "UE"); // ü
		EXPAND_CHARACTERS.put("ß", "SS"); // ß
	}

	/**
	 * The MRZ record, not null.
	 */
	private final String mrz;
	/**
	 * The MRZ record separated into rows.
	 */
	private final String[] rows;
	/**
	 * MRZ record format.
	 */
	private final MrzFormat format;

	/**
	 * Creates new parser which parses given MRZ record.
	 *
	 * @param mrz the MRZ record, not null.
	 *
	 * @throws MrzParseException could not parse MRZ
	 */
	public MrzParser(final String mrz) throws MrzParseException {
		this.mrz = mrz;
		this.rows = mrz.split("\n");
		this.format = MrzFormat.get(mrz);
	}

	/**
	 * @return the MRZ record
	 */
	public String getMrz() {
		return mrz;
	}

	/**
	 * @return the MRZ rows
	 */
	public String[] getRows() {
		return rows;
	}

	/**
	 * @return the MRZ format
	 */
	public MrzFormat getFormat() {
		return format;
	}

	/**
	 * Parses the MRZ name in form of SURNAME &lt;&lt;FIRSTNAME&lt;.
	 *
	 * @author jllarraz@github
	 * @param range the range
	 * @return array of [surname, first_name], never null, always with a length of 2.
	 * @throws MrzParseException could not parse range
	 */
	public String[] parseName(final MrzRange range) throws MrzParseException {
		checkValidCharacters(range);
		String str = rawValue(range);
		while (str.endsWith("<")) {
			str = str.substring(0, str.length() - 1);
		}
		final String[] names = str.split("<<");
		String givenNames = "";
		String surname = parseString(new MrzRange(range.getColumn(), range.getColumn() + names[0].length(), range.getRow()));
		if (names.length == 1) {
			givenNames = parseString(new MrzRange(range.getColumn(), range.getColumn() + names[0].length(), range.getRow()));
			surname = "";
		} else if (names.length > 1) {
			surname = parseString(new MrzRange(range.getColumn(), range.getColumn() + names[0].length(), range.getRow()));
			givenNames = parseString(new MrzRange(range.getColumn() + names[0].length() + 2, range.getColumn() + str.length(), range.getRow()));
		}
		return new String[]{surname, givenNames};
	}

	/**
	 * Returns a raw MRZ value from given range. If multiple ranges are specified, the value is concatenated.
	 *
	 * @param range the ranges, not null.
	 * @return raw value, never null, may be empty.
	 */
	public String rawValue(final MrzRange... range) {
		final StringBuilder sb = new StringBuilder();
		for (MrzRange r : range) {
			sb.append(getRows()[r.getRow()].substring(r.getColumn(), r.getColumnTo()));
		}
		return sb.toString();
	}

	/**
	 * Checks that given range contains valid characters.
	 *
	 * @param range the range to check.
	 * @throws MrzParseException could not parse range
	 */
	public void checkValidCharacters(final MrzRange range) throws MrzParseException {
		final String str = rawValue(range);
		for (int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);
			if (c != FILLER && (c < '0' || c > '9') && (c < 'A' || c > 'Z')) {
				throw new MrzParseException("Invalid character in MRZ record: " + c, getMrz(), new MrzRange(range.getColumn() + i, range.getColumn() + i + 1, range.getRow()), getFormat());
			}
		}
	}

	/**
	 * Parses a string in given range. &lt;&lt; are replaced with ", ", &lt; is replaced by space.
	 *
	 * @param range the range
	 * @return parsed string.
	 * @throws MrzParseException could not parse range
	 */
	public String parseString(final MrzRange range) throws MrzParseException {
		checkValidCharacters(range);
		String str = rawValue(range);
		while (str.endsWith("<")) {
			str = str.substring(0, str.length() - 1);
		}
		return str.replace("" + FILLER + FILLER, ", ").replace(FILLER, ' ');
	}

	/**
	 * Verifies the check digit.
	 *
	 * @param col the 0-based column of the check digit.
	 * @param row the 0-based column of the check digit.
	 * @param strRange the range for which the check digit is computed.
	 * @param fieldName (optional) field name. Used only when validity check fails.
	 * @return true if check digit is valid, false if not
	 */
	public boolean checkDigit(final int col, final int row, final MrzRange strRange, final String fieldName) {
		return checkDigit(col, row, rawValue(strRange), fieldName);
	}

	/**
	 * Verifies the check digit.
	 *
	 * @param col the 0-based column of the check digit.
	 * @param row the 0-based column of the check digit.
	 * @param str the raw MRZ substring.
	 * @param fieldName (optional) field name. Used only when validity check fails.
	 * @return true if check digit is valid, false if not
	 */
	public boolean checkDigit(final int col, final int row, final String str, final String fieldName) {

		// If the check digit validation fails, this will contain the location.
		MrzRange invalidCheckdigit = null;

		final char digit = (char) (computeCheckDigit(str) + '0');
		char checkDigit = getRows()[row].charAt(col);
		if (checkDigit == FILLER) {
			checkDigit = '0';
		}
		if (digit != checkDigit) {
			invalidCheckdigit = new MrzRange(col, col + 1, row);
			LOG.info("Check digit verification failed for " + fieldName + ": expected " + digit + " but got " + checkDigit);
		}
		return invalidCheckdigit == null;
	}

	/**
	 * Parses MRZ date.
	 *
	 * @param range the range containing the date, in the YYMMDD format. The range must be 6 characters long.
	 * @return parsed date
	 * @throws IllegalArgumentException if the range is not 6 characters long.
	 */
	public MrzDate parseDate(final MrzRange range) {
		if (range.length() != 6) {
			throw new IllegalArgumentException("Parameter range: invalid value " + range + ": must be 6 characters long");
		}
		int year = parseYear(range);
		int month = parseMonth(range);
		int day = parseDay(range);
		return new MrzDate(year, month, day, rawValue(range));
	}

	private int parseYear(final MrzRange range) {
		int year;
		MrzRange r = new MrzRange(range.getColumn(), range.getColumn() + 2, range.getRow());
		try {
			year = Integer.parseInt(rawValue(r));
		} catch (NumberFormatException ex) {
			year = -1;
			LOG.debug("Failed to parse MRZ date year " + rawValue(range) + ": " + ex, getMrz(), r);
		}
		if (year < 0 || year > 99) {
			LOG.debug("Invalid year value " + year + ": must be 0..99");
		}
		return year;
	}

	private int parseMonth(final MrzRange range) {
		int month;
		MrzRange r = new MrzRange(range.getColumn() + 2, range.getColumn() + 4, range.getRow());
		try {
			month = Integer.parseInt(rawValue(r));
		} catch (NumberFormatException ex) {
			month = -1;
			LOG.debug("Failed to parse MRZ date month " + rawValue(range) + ": " + ex, getMrz(), r);
		}
		if (month < 1 || month > 12) {
			LOG.debug("Invalid month value " + month + ": must be 1..12");
		}
		return month;
	}

	private int parseDay(final MrzRange range) {
		int day;
		MrzRange r = new MrzRange(range.getColumn() + 4, range.getColumn() + 6, range.getRow());
		try {
			day = Integer.parseInt(rawValue(r));
		} catch (NumberFormatException ex) {
			day = -1;
			LOG.debug("Failed to parse MRZ date day " + rawValue(range) + ": " + ex, getMrz(), r);
		}
		if (day < 1 || day > 31) {
			LOG.debug("Invalid day value " + day + ": must be 1..31");
		}
		return day;
	}

	/**
	 * Parses the "sex" value from given column/row.
	 *
	 * @param col the 0-based column
	 * @param row the 0-based row
	 * @return sex, never null.
	 */
	public MrzSex parseSex(final int col, final int row) {
		return MrzSex.fromMrz(getRows()[row].charAt(col));
	}

	/**
	 * Checks if given character is valid in MRZ.
	 *
	 * @param c the character.
	 * @return true if the character is valid, false otherwise.
	 */
	private static boolean isValid(final char c) {
		return (c == FILLER) || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z');
	}

	private static int getCharacterValue(final char c) {
		if (c == FILLER) {
			return 0;
		}
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (c >= 'A' && c <= 'Z') {
			return c - 'A' + 10;
		}
		throw new IllegalArgumentException("Invalid character in MRZ record: " + c);
	}

	/**
	 * Computes MRZ check digit for given string of characters.
	 *
	 * @param str the string
	 * @return check digit in range of 0..9, inclusive. See
	 * <a href="http://www2.icao.int/en/MRTD/Downloads/Doc%209303/Doc%209303%20English/Doc%209303%20Part%203%20Vol%201.pdf">MRTD documentation</a>
	 * part 15 for details.
	 */
	public static int computeCheckDigit(final String str) {
		int result = 0;
		for (int i = 0; i < str.length(); i++) {
			result += getCharacterValue(str.charAt(i)) * MRZ_WEIGHTS[i % MRZ_WEIGHTS.length];
		}
		return result % 10;
	}

	/**
	 * Computes MRZ check digit for given string of characters.
	 *
	 * @param str the string
	 * @return check digit in range of 0..9, inclusive. See
	 * <a href="http://www2.icao.int/en/MRTD/Downloads/Doc%209303/Doc%209303%20English/Doc%209303%20Part%203%20Vol%201.pdf">MRTD documentation</a>
	 * part 15 for details.
	 */
	public static char computeCheckDigitChar(final String str) {
		return (char) ('0' + computeCheckDigit(str));
	}

	/**
	 * Factory method, which parses the MRZ and returns appropriate record class.
	 *
	 * @param mrz MRZ to parse.
	 * @return record class.
	 * @throws MrzParseException could not parse MRZ
	 */
	public static MrzRecord parse(final String mrz) throws MrzParseException {
		final MrzRecord result = MrzFormat.get(mrz).newRecord();
		result.fromMrz(mrz);
		return result;
	}

	/**
	 * Converts given string to a MRZ string: removes all accents, converts the string to upper-case and replaces all spaces and invalid characters
	 * with '&lt;'.
	 * <p>
	 * Several characters are expanded:
	 * </p>
	 * <table border="1">
	 * <caption>Expanded characters</caption>
	 * <tr><th>Character</th><th>Expand to</th></tr>
	 * <tr><td>Ä</td><td>AE</td></tr>
	 * <tr><td>Å</td><td>AA</td></tr>
	 * <tr><td>Æ</td><td>AE</td></tr>
	 * <tr><td>Ĳ</td><td>IJ</td></tr>
	 * <tr><td>IJ</td><td>IJ</td></tr>
	 * <tr><td>Ö</td><td>OE</td></tr>
	 * <tr><td>Ø</td><td>OE</td></tr>
	 * <tr><td>Ü</td><td>UE</td></tr>
	 * <tr><td>ß</td><td>SS</td></tr>
	 * </table>
	 * Examples:
	 * <ul>
	 * <li><code>toMrz("Sedím na konári", 20)</code> yields <code>"SEDIM&lt;NA&lt;KONARI&lt;&lt;&lt;&lt;&lt;"</code></li>
	 * <li><code>toMrz("Pat, Mat", 8)</code> yields <code>"PAT&lt;&lt;MAT"</code></li>
	 * <li><code>toMrz("foo/bar baz", 4)</code> yields <code>"FOO&lt;"</code></li>
	 * <li><code>toMrz("*$()&amp;/\", 8)</code> yields <code>"&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;"</code></li>
	 * </ul>
	 *
	 * @param input the string to convert. Passing null is the same as passing in an empty string.
	 * @param length required length of the string. If given string is longer, it is truncated. If given string is shorter than given length, '&lt;'
	 * characters are appended at the end. If -1, the string is neither truncated nor enlarged.
	 * @return MRZ-valid string.
	 */
	public static String toMrz(final String input, final int length) {
		String string = input == null ? "" : input;
		for (final Map.Entry<String, String> e : EXPAND_CHARACTERS.entrySet()) {
			string = string.replace(e.getKey(), e.getValue());
		}
		string = string.replace("’", "");
		string = string.replace("'", "");
		string = deaccent(string).toUpperCase();
		if (length >= 0 && string.length() > length) {
			string = string.substring(0, length);
		}
		final StringBuilder sb = new StringBuilder(string);
		for (int i = 0; i < sb.length(); i++) {
			if (!isValid(sb.charAt(i))) {
				sb.setCharAt(i, FILLER);
			}
		}
		while (sb.length() < length) {
			sb.append(FILLER);
		}
		return sb.toString();
	}

	private static boolean isBlank(final String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * Converts a surname and given names to a MRZ string, shortening them as per Doc 9303 Part 3 Vol 1 Section 6.7 of the MRZ specification when
	 * necessary.
	 *
	 * @param surname the surname, not blank.
	 * @param givenNames given names, not blank.
	 * @param length required length of the string. If given string is longer, it is shortened. If given string is shorter than given length, '&lt;'
	 * characters are appended at the end.
	 * @return name, properly converted to MRZ format of SURNAME&lt;&lt;GIVENNAMES&lt;..., with the exact length of given length.
	 */
	public static String nameToMrz(final String surname, final String givenNames, final int length) {
		if (isBlank(surname)) {
			throw new IllegalArgumentException("Parameter surname: invalid value " + surname + ": blank");
		}
		if (isBlank(givenNames)) {
			throw new IllegalArgumentException("Parameter givenNames: invalid value " + givenNames + ": blank");
		}
		if (length <= 0) {
			throw new IllegalArgumentException("Parameter length: invalid value " + length + ": not positive");
		}
		final String[] surnames = extractNames(surname);
		final String[] given = extractNames(givenNames);
		// Truncate
		truncateNames(surnames, given, length, surname, givenNames);
		return toMrz(toName(surnames, given), length);
	}

	private static String[] extractNames(final String name) {
		final String[] names = name.replace(", ", " ").trim().split("[ \n\t\f\r]+");
		for (int i = 0; i < names.length; i++) {
			names[i] = toMrz(names[i], -1);
		}
		return names;
	}

	private static void truncateNames(final String[] surnames, final String[] given, final int length, final String surname, final String givenNames) {
		int nameSize = getNameSize(surnames, given);
		String[] currentlyTruncating = given;
		int currentlyTruncatingIndex = given.length - 1;
		while (nameSize > length) {
			final String ct = currentlyTruncating[currentlyTruncatingIndex];
			final int ctsize = ct.length();
			if (nameSize - ctsize + 1 <= length) {
				currentlyTruncating[currentlyTruncatingIndex] = ct.substring(0, ctsize - (nameSize - length));
			} else {
				currentlyTruncating[currentlyTruncatingIndex] = ct.substring(0, 1);
				currentlyTruncatingIndex--;
				if (currentlyTruncatingIndex < 0) {
					if (currentlyTruncating == surnames) {
						throw new IllegalArgumentException("Cannot truncate name " + surname + " " + givenNames + ": length too small: " + length + "; truncated to " + toName(surnames, given));
					}
					currentlyTruncating = surnames;
					currentlyTruncatingIndex = currentlyTruncating.length - 1;
				}
			}
			nameSize = getNameSize(surnames, given);
		}
	}

	private static String toName(final String[] surnames, final String[] given) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : surnames) {
			if (first) {
				first = false;
			} else {
				sb.append(FILLER);
			}
			sb.append(s);
		}
		sb.append(FILLER);
		for (String s : given) {
			sb.append(FILLER);
			sb.append(s);
		}
		return sb.toString();
	}

	private static int getNameSize(final String[] surnames, final String[] given) {
		int result = 0;
		for (String s : surnames) {
			result += s.length() + 1;
		}
		for (String s : given) {
			result += s.length() + 1;
		}
		return result;
	}

	private static String deaccent(final String str) {
		String n = Normalizer.normalize(str, Normalizer.Form.NFD);
		return n.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
	}

}
