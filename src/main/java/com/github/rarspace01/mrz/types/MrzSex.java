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
package com.github.rarspace01.mrz.types;

/**
 * MRZ sex.
 *
 * @author Martin Vysny
 */
public enum MrzSex {

	/**
	 * Male MRZ sex type.
	 */
	MALE('M'),
	/**
	 * Female MRZ sex type.
	 */
	FEMALE('F'),
	/**
	 * Unspecified MRZ sex type.
	 */
	UNSPECIFIED('X');

	/**
	 * The MRZ character.
	 */
	private final char mrz;

	private MrzSex(final char mrz) {
		this.mrz = mrz;
	}

	/**
	 * @return the MRZ character
	 */
	public final char getMrz() {
		return mrz;
	}

	/**
	 * @param sex the sex char
	 * @return the matching MrzSex type
	 */
	public static MrzSex fromMrz(final char sex) {
		switch (sex) {
			case 'M':
				return MALE;
			case 'F':
				return FEMALE;
			case '<':
			case 'X':
				return UNSPECIFIED;
			default:
				return null;
		}
	}

}
