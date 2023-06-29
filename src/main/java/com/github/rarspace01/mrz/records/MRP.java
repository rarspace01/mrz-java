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
import com.github.rarspace01.mrz.MrzRecord;
import com.github.rarspace01.mrz.types.MrzFormat;

/**
 * MRP Passport format: A two line long, 44 characters per line format.
 *
 * @author Martin Vysny
 */
public class MRP extends MrzRecord {

	private static final long serialVersionUID = 1L;

	private String personalNumber;

	private boolean validPersonalNumber;

	/**
	 * Construct MRP Record.
	 */
	public MRP() {
		super(MrzFormat.PASSPORT, "MRP");
	}

	@Override
	public void fromMrz(final String mrz) throws MrzParseException {
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
		setPersonalNumber(parser.parseString(new MrzRange(28, 42, 1)));
		setValidPersonalNumber(parser.checkDigit(42, 1, new MrzRange(28, 42, 1), "personal number"));
		setValidComposite(parser.checkDigit(43, 1, parser.rawValue(new MrzRange(0, 10, 1), new MrzRange(13, 20, 1), new MrzRange(21, 43, 1)), "mrz"));
	}

	@Override
	public String toMrz() {
		// first line
		final StringBuilder sb = new StringBuilder();
		sb.append(getCode1());
		sb.append(getCode2());
		sb.append(MrzParser.toMrz(getIssuingCountry(), 3));
		sb.append(MrzParser.nameToMrz(getSurname(), getGivenNames(), 39));
		sb.append('\n');
		// second line
		final String docNum = MrzParser.toMrz(getDocumentNumber(), 9) + MrzParser.computeCheckDigitChar(MrzParser.toMrz(getDocumentNumber(), 9));
		sb.append(docNum);
		sb.append(MrzParser.toMrz(getNationality(), 3));
		final String dob = getDateOfBirth().toMrz() + MrzParser.computeCheckDigitChar(getDateOfBirth().toMrz());
		sb.append(dob);
		sb.append(getSex().getMrz());
		final String edpn = getExpirationDate().toMrz() + MrzParser.computeCheckDigitChar(getExpirationDate().toMrz()) + MrzParser.toMrz(getPersonalNumber(), 14) + MrzParser.computeCheckDigitChar(MrzParser.toMrz(getPersonalNumber(), 14));
		sb.append(edpn);
		sb.append(MrzParser.computeCheckDigitChar(docNum + dob + edpn));
		sb.append('\n');
		return sb.toString();
	}

	/**
	 * @return the personal number
	 */
	public String getPersonalNumber() {
		return personalNumber;
	}

	/**
	 * @param personalNumber the personal number
	 */
	public void setPersonalNumber(final String personalNumber) {
		this.personalNumber = personalNumber;
	}

	/**
	 * @return true if valid personal number
	 */
	public boolean isValidPersonalNumber() {
		return validPersonalNumber;
	}

	/**
	 * @param validPersonalNumber true if valid personal number
	 */
	protected void setValidPersonalNumber(final boolean validPersonalNumber) {
		this.validPersonalNumber = validPersonalNumber;
	}

	@Override
	protected void buildToString(final StringBuilder sb) {
		super.buildToString(sb);
		sb.append(", personalNumber=").append(getPersonalNumber());
	}

}
