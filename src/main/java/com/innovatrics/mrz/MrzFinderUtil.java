package com.innovatrics.mrz;

import com.innovatrics.mrz.types.MrzFormat;
import java.util.regex.Pattern;

/**
 * Utility to help find a MRZ.
 */
public final class MrzFinderUtil {

	// MRZ First Line - Type + delimiter or code + country code (then allow for line up to 30-44 length)
	private static final Pattern MRZFIRSTLINE = Pattern.compile("[P|VACI][A-Z0-9<]([A-Z]{3}|D<<)[A-Z0-9<]{25,39}");
	// MRZ Standard Characters Line (30 to 44 length)
	private static final Pattern MRZCHARS = Pattern.compile("[A-Z0-9<]{30,44}");

	/**
	 * Prevent instantiation.
	 */
	private MrzFinderUtil() {
		// Do nothing
	}

	/**
	 * Find a MRZ in an input string surrounded by other characters.
	 *
	 * @param input the string containing the MRZ
	 * @return the MRZ string
	 * @throws MrzNotFoundException MRZ not found
	 * @throws MrzParseException MRZ found but invalid
	 */
	public static String findMrz(final String input) throws MrzNotFoundException, MrzParseException {
		// Check input provided
		if (input == null) {
			throw new MrzNotFoundException();
		}
		// Try to extract MRZ
		String mrz = extractMrz(input);
		if (mrz.isEmpty()) {
			throw new MrzNotFoundException();
		}
		// Check MRZ is a valid format
		MrzFormat.get(mrz);
		return mrz;
	}

	private static String extractMrz(final String input) {
		// Split lines
		String[] lines = input.split("\n");
		StringBuilder mrz = new StringBuilder();
		boolean found = false;
		// Process lines until hit MRZ
		for (String line : lines) {
			String test = line.trim();
			// Only interested in lines that are not blank (this also allows for blank lines between MRZ lines)
			if (!test.isEmpty()) {
				if (found) {
					// Only extract continuous MRZ lines
					if (!MRZCHARS.matcher(test).matches()) {
						break;
					}
					// Append line
					mrz.append("\n");
					mrz.append(test);
				} else if (MRZFIRSTLINE.matcher(test).matches()) {
					found = true;
					mrz.append(test);
				}
			}
		}
		return mrz.toString();
	}

}
