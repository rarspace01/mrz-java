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

import java.io.Serializable;

/**
 * Represents a text selection range.
 *
 * @author Martin Vysny
 */
public class MrzRange implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 0-based index of first character in the range.
	 */
	private final int column;
	/**
	 * 0-based index of a character after last character in the range.
	 */
	private final int columnTo;
	/**
	 * 0-based row.
	 */
	private final int row;

	/**
	 * Creates new MRZ range object.
	 *
	 * @param column 0-based index of first character in the range.
	 * @param columnTo 0-based index of a character after last character in the range.
	 * @param row 0-based row.
	 */
	public MrzRange(final int column, final int columnTo, final int row) {
		if (column > columnTo) {
			throw new IllegalArgumentException("Parameter column: invalid value " + column + ": must be less than " + columnTo);
		}
		this.column = column;
		this.columnTo = columnTo;
		this.row = row;
	}

	/**
	 * @return the 0-based index of first character in the range
	 */
	public final int getColumn() {
		return column;
	}

	/**
	 * @return the 0-based index of a character after last character in the range
	 */
	public final int getColumnTo() {
		return columnTo;
	}

	/**
	 * @return the 0-based row
	 */
	public final int getRow() {
		return row;
	}

	/**
	 * Returns length of this range.
	 *
	 * @return number of characters, which this range covers.
	 */
	public final int length() {
		return getColumnTo() - getColumn();
	}

	@Override
	public String toString() {
		return getColumn() + "-" + getColumnTo() + "," + getRow();
	}

}
