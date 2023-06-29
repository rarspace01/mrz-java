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
package com.github.rarspace01.mrz.records;

import com.github.rarspace01.mrz.MrzParseException;
import com.github.rarspace01.mrz.MrzParser;
import com.github.rarspace01.mrz.MrzRange;
import com.github.rarspace01.mrz.MrzRecordOptional;
import com.github.rarspace01.mrz.types.MrzFormat;

/**
 * MRTD TD1 format: A three line long, 30 characters per line format.
 *
 * @author Martin Vysny
 */
public class MrtdTd1 extends MrzRecordOptional {

	private static final long serialVersionUID = 1L;

	/**
	 * Optional (for U.S. passport holders, 21-29 may be corresponding passport number).
	 */
	private String optional2;

	/**
	 * Construct a MrtdTd1 Record.
	 */
	public MrtdTd1() {
		super(MrzFormat.MRTD_TD1, "MRTD-TD1");
	}

	@Override
	public void fromMrz(final String mrz) throws MrzParseException {
		super.fromMrz(mrz);
		final MrzParser parser = new MrzParser(mrz);
		setDocumentNumber(parser.parseString(new MrzRange(5, 14, 0)));
		setValidDocumentNumber(parser.checkDigit(14, 0, new MrzRange(5, 14, 0), "document number"));
		setOptional(parser.parseString(new MrzRange(15, 30, 0)));
		setDateOfBirth(parser.parseDate(new MrzRange(0, 6, 1)));
		setValidDateOfBirth(parser.checkDigit(6, 1, new MrzRange(0, 6, 1), "date of birth") && getDateOfBirth().isDateValid());
		setSex(parser.parseSex(7, 1));
		setExpirationDate(parser.parseDate(new MrzRange(8, 14, 1)));
		setValidExpirationDate(parser.checkDigit(14, 1, new MrzRange(8, 14, 1), "expiration date") && getExpirationDate().isDateValid());
		setNationality(parser.parseString(new MrzRange(15, 18, 1)));
		setOptional2(parser.parseString(new MrzRange(18, 29, 1)));
		setValidComposite(parser.checkDigit(29, 1, parser.rawValue(new MrzRange(5, 30, 0), new MrzRange(0, 7, 1), new MrzRange(8, 15, 1), new MrzRange(18, 29, 1)), "mrz"));
		setName(parser.parseName(new MrzRange(0, 30, 2)));
	}

	@Override
	public String toMrz() {
		// first line
		final StringBuilder sb = new StringBuilder();
		sb.append(getCode1());
		sb.append(getCode2());
		sb.append(MrzParser.toMrz(getIssuingCountry(), 3));
		final String dno = MrzParser.toMrz(getDocumentNumber(), 9) + MrzParser.computeCheckDigitChar(MrzParser.toMrz(getDocumentNumber(), 9)) + MrzParser.toMrz(getOptional(), 15);
		sb.append(dno);
		sb.append('\n');
		// second line
		final String dob = getDateOfBirth().toMrz() + MrzParser.computeCheckDigitChar(getDateOfBirth().toMrz());
		sb.append(dob);
		sb.append(getSex().getMrz());
		final String ed = getExpirationDate().toMrz() + MrzParser.computeCheckDigitChar(getExpirationDate().toMrz());
		sb.append(ed);
		sb.append(MrzParser.toMrz(getNationality(), 3));
		sb.append(MrzParser.toMrz(getOptional2(), 11));
		sb.append(MrzParser.computeCheckDigitChar(dno + dob + ed + MrzParser.toMrz(getOptional2(), 11)));
		sb.append('\n');
		// third line
		sb.append(MrzParser.nameToMrz(getSurname(), getGivenNames(), 30));
		sb.append('\n');
		return sb.toString();
	}

	/**
	 * Optional (for U.S. passport holders, 21-29 may be corresponding passport number).
	 *
	 * @return the optional2 data
	 */
	public String getOptional2() {
		return optional2;
	}

	/**
	 * Optional (for U.S. passport holders, 21-29 may be corresponding passport number).
	 *
	 * @param optional2 the optional2 data
	 */
	public void setOptional2(final String optional2) {
		this.optional2 = optional2;
	}

	@Override
	protected void buildToString(final StringBuilder sb) {
		super.buildToString(sb);
		sb.append(", optional2=").append(getOptional2());
	}

}
