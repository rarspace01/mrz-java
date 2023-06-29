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

import java.io.Serializable;

/**
 * Holds a MRZ date type.
 *
 * @author Martin Vysny
 */
public class MrzDate implements Serializable, Comparable<MrzDate> {

	private static final long serialVersionUID = 1L;

	/**
	 * Year, 00-99.
	 * <p>
	 * Note: I am unable to find a specification of conversion of this value to a full year value.
	 * </p>
	 */
	private final int year;
	/**
	 * Month, 1-12.
	 */
	private final int month;
	/**
	 * Day, 1-31.
	 */
	private final int day;

	private final String mrz;

	/**
	 * Is the date valid or not.
	 */
	private final boolean dateValid;

	/**
	 * @param year the year 00-99
	 * @param month the month 1-12
	 * @param day the day 1-31
	 */
	public MrzDate(final int year, final int month, final int day) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.dateValid = check();
		this.mrz = null;
	}

	/**
	 * @param year the year 00-99
	 * @param month the month 1-12
	 * @param day the day 1-31
	 * @param raw the raw MRZ value
	 */
	public MrzDate(final int year, final int month, final int day, final String raw) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.dateValid = check();
		this.mrz = raw;
	}

	/**
	 * @return the year
	 */
	public final int getYear() {
		return year;
	}

	/**
	 * @return the month
	 */
	public final int getMonth() {
		return month;
	}

	/**
	 * @return the day
	 */
	public final int getDay() {
		return day;
	}

	/**
	 * @return the raw MRZ
	 */
	public final String getMrz() {
		return mrz;
	}

	/**
	 * Returns the date validity.
	 *
	 * @return true if the parsed date is valid, false otherwise
	 */
	public final boolean isDateValid() {
		return dateValid;
	}

	/**
	 * @return the date in MRZ format
	 */
	public String toMrz() {
		if (getMrz() != null) {
			return getMrz();
		} else {
			return String.format("%02d%02d%02d", getYear(), getMonth(), getDay());
		}
	}

	private boolean check() {
		if (getYear() < 0 || getYear() > 99) {
			System.out.println("Parameter year: invalid value " + getYear() + ": must be 0..99");
			return false;
		}
		if (getMonth() < 1 || getMonth() > 12) {
			System.out.println("Parameter month: invalid value " + getMonth() + ": must be 1..12");
			return false;
		}
		if (getDay() < 1 || getDay() > 31) {
			System.out.println("Parameter day: invalid value " + getDay() + ": must be 1..31");
			return false;
		}

		return true;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MrzDate other = (MrzDate) obj;
		if (this.getYear() != other.getYear()) {
			return false;
		}
		if (this.getMonth() != other.getMonth()) {
			return false;
		}
		return this.getDay() == other.getDay();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 11 * hash + this.getYear();
		hash = 11 * hash + this.getMonth();
		hash = 11 * hash + this.getDay();
		return hash;
	}

	@Override
	public int compareTo(final MrzDate o) {
		return Integer.valueOf(getYear() * 10000 + getMonth() * 100 + getDay()).compareTo(o.getYear() * 10000 + o.getMonth() * 100 + o.getDay());
	}

	@Override
	public String toString() {
		return "{" + getDay() + "/" + getMonth() + "/" + getYear() + '}';
	}

}
