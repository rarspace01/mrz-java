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
package com.innovatrics.mrz.records;

import com.innovatrics.mrz.MrzParseException;
import com.innovatrics.mrz.MrzParser;
import com.innovatrics.mrz.MrzRange;
import com.innovatrics.mrz.MrzRecordOptional;
import com.innovatrics.mrz.types.MrzFormat;

/**
 * MRTD td2 format: A two line long, 36 characters per line format.
 *
 * @author Martin Vysny
 */
public class MrtdTd2 extends MrzRecordOptional {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a MrtdTd2 Record.
	 */
	public MrtdTd2() {
		super(MrzFormat.MRTD_TD2, "MRTD-TD2");
	}

	@Override
	public void fromMrz(final String mrz) throws MrzParseException {
		super.fromMrz(mrz);
		final MrzParser parser = new MrzParser(mrz);
		setName(parser.parseName(new MrzRange(5, 36, 0)));
		setDocumentNumber(parser.parseString(new MrzRange(0, 9, 1)));
		setValidDocumentNumber(parser.checkDigit(9, 1, new MrzRange(0, 9, 1), "document number"));
		setNationality(parser.parseString(new MrzRange(10, 13, 1)));
		setDateOfBirth(parser.parseDate(new MrzRange(13, 19, 1)));
		setValidDateOfBirth(parser.checkDigit(19, 1, new MrzRange(13, 19, 1), "date of birth") && getDateOfBirth().isDateValid());
		setSex(parser.parseSex(20, 1));
		setExpirationDate(parser.parseDate(new MrzRange(21, 27, 1)));
		setValidExpirationDate(parser.checkDigit(27, 1, new MrzRange(21, 27, 1), "expiration date") && getExpirationDate().isDateValid());
		setOptional(parser.parseString(new MrzRange(28, 35, 1)));
		setValidComposite(parser.checkDigit(35, 1, parser.rawValue(new MrzRange(0, 10, 1), new MrzRange(13, 20, 1), new MrzRange(21, 35, 1)), "mrz"));
	}

	@Override
	public String toMrz() {
		// first line
		final StringBuilder sb = new StringBuilder();
		sb.append(getCode1());
		sb.append(getCode2());
		sb.append(MrzParser.toMrz(getIssuingCountry(), 3));
		sb.append(MrzParser.nameToMrz(getSurname(), getGivenNames(), 31));
		sb.append('\n');
		// second line
		final String dn = MrzParser.toMrz(getDocumentNumber(), 9) + MrzParser.computeCheckDigitChar(MrzParser.toMrz(getDocumentNumber(), 9));
		sb.append(dn);
		sb.append(MrzParser.toMrz(getNationality(), 3));
		final String dob = getDateOfBirth().toMrz() + MrzParser.computeCheckDigitChar(getDateOfBirth().toMrz());
		sb.append(dob);
		sb.append(getSex().getMrz());
		final String ed = getExpirationDate().toMrz() + MrzParser.computeCheckDigitChar(getExpirationDate().toMrz());
		sb.append(ed);
		sb.append(MrzParser.toMrz(getOptional(), 7));
		sb.append(MrzParser.computeCheckDigitChar(dn + dob + ed + MrzParser.toMrz(getOptional(), 7)));
		sb.append('\n');
		return sb.toString();
	}

}
