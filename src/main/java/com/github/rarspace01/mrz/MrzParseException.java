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
package com.github.rarspace01.mrz;

import com.github.rarspace01.mrz.types.MrzFormat;

/**
 * Thrown when a MRZ parse fails.
 *
 * @author Martin Vysny
 */
public class MrzParseException extends Exception {

	private static final long serialVersionUID = 1L;
	/**
	 * The MRZ string being parsed.
	 */
	private final String mrz;
	/**
	 * Range containing problematic characters.
	 */
	private final MrzRange range;
	/**
	 * Expected MRZ format.
	 */
	private final MrzFormat format;

	/**
	 *
	 * @param message the message
	 * @param mrz the MRZ string being parsed
	 * @param range the Range containing problematic characters
	 * @param format the expected MRZ format
	 */
	public MrzParseException(final String message, final String mrz, final MrzRange range, final MrzFormat format) {
		super("Failed to parse MRZ " + format + " " + mrz + " at " + range + ": " + message);
		this.mrz = mrz;
		this.format = format;
		this.range = range;
	}

	/**
	 * @return the MRZ string being parsed
	 */
	public final String getMrz() {
		return mrz;
	}

	/**
	 * @return the Range containing problematic characters
	 */
	public final MrzRange getRange() {
		return range;
	}

	/**
	 * @return the expected MRZ format
	 */
	public final MrzFormat getFormat() {
		return format;
	}

}
