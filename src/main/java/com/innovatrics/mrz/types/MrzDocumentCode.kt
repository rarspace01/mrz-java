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
package com.innovatrics.mrz.types;

import com.innovatrics.mrz.MrzParseException;
import com.innovatrics.mrz.MrzRange;

/**
 * Lists all supported MRZ record types (a.k.a. document codes).
 *
 * @author Martin Vysny
 */
public enum MrzDocumentCode {

	/**
	 * A passport, P or IP. ... maybe Travel Document that is very similar to the passport.
	 */
	PASSPORT,
	/**
	 * General I type (besides IP).
	 */
	TYPE_I,
	/**
	 * General A type (besides AC).
	 */
	TYPE_A,
	/**
	 * Crew member (AC).
	 */
	CREW_MEMBER,
	/**
	 * General type C.
	 */
	TYPE_C,
	/**
	 * Type V (Visa).
	 */
	TYPE_V,
	/**
	 *
	 */
	MIGRANT;

	/**
	 * @author Zsombor turning to switch statement due to lots of types
	 *
	 * @param mrz the mrz string
	 * @return the mrz document code
	 * @throws MrzParseException could not parse MRZ
	 */
	public static MrzDocumentCode parse(final String mrz) throws MrzParseException {
		final String code = mrz.substring(0, 2);

		// 2-letter checks
		switch (code) {
			case "IV":
				throw new MrzParseException("IV document code is not allowed", mrz, new MrzRange(0, 2, 0), null); // TODO why?
			case "AC":
				return CREW_MEMBER;
			case "ME":
				return MIGRANT;
			case "TD":
				return MIGRANT; // travel document
			case "IP":
				return PASSPORT;
			default:
			// Do nothing
		}

		// 1-letter checks
		switch (code.charAt(0)) {
			case 'T':   // usually Travel Document
			case 'P':
				return PASSPORT;
			case 'A':
				return TYPE_A;
			case 'C':
				return TYPE_C;
			case 'V':
				return TYPE_V;
			case 'I':
				return TYPE_I; // identity card or residence permit
			case 'R':
				return MIGRANT;  // swedish '51 Convention Travel Document
			default:
				throw new MrzParseException("Unsupported document code: " + code, mrz, new MrzRange(0, 2, 0), null);
		}

	}
}
