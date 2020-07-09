/**
 * Java parser for the MRZ records, as specified by the ICAO organization.
 * Copyright (C) 2011 Innovatrics s.r.o.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.innovatrics.mrz.records;

import com.innovatrics.mrz.MrzParser;
import com.innovatrics.mrz.MrzRange;
import com.innovatrics.mrz.MrzRecord;
import com.innovatrics.mrz.types.MrzDocumentCode;
import com.innovatrics.mrz.types.MrzFormat;

/**
 * MRV type-A format: A two lines long, 44 characters per line format.
 *
 * @author Jeremy Le Berre
 */
public class MrvA extends MrzRecord {

	private static final long serialVersionUID = 1L;

	/**
	 * Optional data at the discretion of the issuing State.
	 */
	private String optional;

	/**
	 * Construct a MrvA Record.
	 */
	public MrvA() {
		super(MrzFormat.MRV_VISA_A);
		setCode(MrzDocumentCode.TypeV);
		setCode1('V');
		setCode2('<');
	}

	@Override
	public void fromMrz(final String mrz) {
		super.fromMrz(mrz);
		final MrzParser parser = new MrzParser(mrz);
		setName(parser.parseName(new MrzRange(5, 44, 0)));
		setDocumentNumber(parser.parseString(new MrzRange(0, 9, 1)));
		setValidDocumentNumber(parser.checkDigit(9, 1, new MrzRange(0, 9, 1), "passport number"));
		setNationality(parser.parseString(new MrzRange(10, 13, 1)));
		setDateOfBirth(parser.parseDate(new MrzRange(13, 19, 1)));
		setValidDateOfBirth(parser.checkDigit(19, 1, new MrzRange(13, 19, 1), "date of birth") && getDateOfBirth().isDateValid());
		setSex(parser.parseSex(20, 1));
		setExpirationDate(parser.parseDate(new MrzRange(21, 27, 1)));
		setValidExpirationDate(parser.checkDigit(27, 1, new MrzRange(21, 27, 1), "expiration date") && getExpirationDate().isDateValid());
		setOptional(parser.parseString(new MrzRange(28, 44, 1)));
		// TODO validComposite missing? (final MRZ check digit)
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("MRV-A");
		sb.append('{');
		sb.append(super.toString());
		sb.append(", optional=").append(getOptional());
		sb.append('}');
		return sb.toString();
	}

	@Override
	public String toMrz() {
		// first line
		final StringBuilder sb = new StringBuilder("V<");
		sb.append(MrzParser.toMrz(getIssuingCountry(), 3));
		sb.append(MrzParser.nameToMrz(getSurname(), getGivenNames(), 39));
		sb.append('\n');
		// second line
		sb.append(MrzParser.toMrz(getDocumentNumber(), 9));
		sb.append(MrzParser.computeCheckDigitChar(MrzParser.toMrz(getDocumentNumber(), 9)));
		sb.append(MrzParser.toMrz(getNationality(), 3));
		sb.append(getDateOfBirth().toMrz());
		sb.append(MrzParser.computeCheckDigitChar(getDateOfBirth().toMrz()));
		sb.append(getSex().getMrz());
		sb.append(getExpirationDate().toMrz());
		sb.append(MrzParser.computeCheckDigitChar(getExpirationDate().toMrz()));
		sb.append(MrzParser.toMrz(getOptional(), 16));
		sb.append('\n');
		return sb.toString();
	}

	/**
	 * Optional data at the discretion of the issuing State.
	 *
	 * @return the optional data
	 */
	public String getOptional() {
		return optional;
	}

	/**
	 * Optional data at the discretion of the issuing State.
	 *
	 * @param optional the optional data
	 */
	public void setOptional(final String optional) {
		this.optional = optional;
	}

}
