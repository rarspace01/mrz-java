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
package com.innovatrics.mrz;

import com.innovatrics.mrz.types.MrzDate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the parser.
 *
 * @author Martin Vysny
 */
public class MrzParserTest {

	/**
	 * Test of computeCheckDigit method, of class MrzRecord.
	 */
	@Test
	public void testComputeCheckDigit() {
		Assert.assertEquals(3, MrzParser.computeCheckDigit("520727"));
		Assert.assertEquals(2, MrzParser.computeCheckDigit("D231458907<<<<<<<<<<<<<<<34071279507122<<<<<<<<<<"));
		Assert.assertEquals('3', MrzParser.computeCheckDigitChar("520727"));
		Assert.assertEquals('2', MrzParser.computeCheckDigitChar("D231458907<<<<<<<<<<<<<<<34071279507122<<<<<<<<<<"));
	}

	@Test
	public void testValidCheckDigit() throws MrzParseException {

		String CzechPassport = "P<CZESPECIMEN<<VZOR<<<<<<<<<<<<<<<<<<<<<<<<<\n99003853<1CZE1101018M1207046110101111<<<<<94";
		Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidDateOfBirth());
		Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidExpirationDate());
		Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidDocumentNumber());
		Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidComposite());

		String GermanPassport = "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<\nC01X01R741D<<6408125F2010315<<<<<<<<<<<<<<<9";
		Assert.assertEquals(true, MrzParser.parse(GermanPassport).isValidDateOfBirth());
		Assert.assertEquals(true, MrzParser.parse(GermanPassport).isValidExpirationDate());
		Assert.assertEquals(true, MrzParser.parse(GermanPassport).isValidDocumentNumber());
		Assert.assertEquals(false, MrzParser.parse(GermanPassport).isValidComposite()); // yes, this specimen has intentationally wrong check digit
	}

	@Test
	public void testDateParsing() throws MrzParseException {
		Assert.assertEquals(new MrzDate(34, 7, 12), new MrzParser("CIUTOD231458907A123X5328434D23\n3407127M9507122UTO<<<<<<<<<<<6\nSTEVENSON<<PETER<<<<<<<<<<<<<<\n").parseDate(new MrzRange(0, 6, 1)));
		Assert.assertEquals(new MrzDate(95, 12, 1), new MrzParser("CIUTOD231458907A123X5328434D23\n3407127M9512012UTO<<<<<<<<<<<6\nSTEVENSON<<PETER<<<<<<<<<<<<<<\n").parseDate(new MrzRange(8, 14, 1)));
	}

	@Test
	public void testToMrz() {
		// \u010d = č
		Assert.assertEquals("CACACA<<<<<", MrzParser.toMrz("\u010da\u010da\u010da", 11));
		Assert.assertEquals("HERBERT<<FRANK<<<", MrzParser.toMrz("Herbert  Frank", 17));
		Assert.assertEquals("PAT<<MAT", MrzParser.toMrz("Pat, Mat", 8));
		Assert.assertEquals("FOO<", MrzParser.toMrz("foo bar baz", 4));
		Assert.assertEquals("<<<<<<<<", MrzParser.toMrz("*$()&/\\", 8));
		Assert.assertEquals("AEAEIJIJ", MrzParser.toMrz("\u00C4\u00E4\u0132\u0133", 8));
		Assert.assertEquals("OEOE", MrzParser.toMrz("\u00D6\u00F6", 4));
		Assert.assertEquals("DART", MrzParser.toMrz("D’Artagnan", 4));
		Assert.assertEquals("DART", MrzParser.toMrz("D'Artagnan", 4));
	}

	@Test
	public void testNameToMrz() {
		Assert.assertEquals("HERBERT<<FRANK<<<", MrzParser.nameToMrz("Herbert", "Frank", 17));
		Assert.assertEquals("ERIKSSON<<ANNA<MARIA<<<", MrzParser.nameToMrz("Eriksson", "Anna, Maria", 23));
		// test name truncating
		Assert.assertEquals("PAPANDROPOULOUS<<JONATHOON<ALEC", MrzParser.nameToMrz("Papandropoulous", "Jonathoon Alec", 31));
		Assert.assertEquals("NILAVADHANANANDA<<CHAYAPA<DEJ<K", MrzParser.nameToMrz("Nilavadhanananda", "Chayapa Dejthamrong Krasuang", 31));
		Assert.assertEquals("NILAVADHANANANDA<<ARNPOL<PETC<C", MrzParser.nameToMrz("NILAVADHANANANDA", "ARNPOL PETCH CHARONGUANG", 31));
		Assert.assertEquals("BENNELONG<WOOLOOMOOLOO<W<W<<D<P", MrzParser.nameToMrz("BENNELONG WOOLOOMOOLOO WARRANDYTE WARNAMBOOL", "DINGO POTOROO", 31));
	}

	@Test
	public void testValidDates() throws MrzParseException {
		String validBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809117F2007162<<<<<<<<<<<<<<08";
		MrzRecord record = MrzParser.parse(validBirthDateMrz);
		Assert.assertEquals(true, record.getDateOfBirth().isDateValid());
		Assert.assertEquals(true, record.getExpirationDate().isDateValid());
		Assert.assertEquals(true, record.isValidDateOfBirth());
		Assert.assertEquals(true, record.isValidExpirationDate());
	}

	@Test
	public void testMrzInvalidBirthDate() throws MrzParseException {
		String invalidBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809417F2007162<<<<<<<<<<<<<<08";
		MrzRecord record = MrzParser.parse(invalidBirthDateMrz);
		Assert.assertEquals(false, record.getDateOfBirth().isDateValid());
		Assert.assertEquals(false, record.isValidDateOfBirth());
	}

	@Test
	public void testMrzInvalidExpiryDate() throws MrzParseException {
		String invalidExpiryDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809117F2007462<<<<<<<<<<<<<<08";
		MrzRecord record = MrzParser.parse(invalidExpiryDateMrz);
		Assert.assertEquals(false, record.getExpirationDate().isDateValid());
		Assert.assertEquals(false, record.isValidExpirationDate());
	}

	@Test
	public void testUnparseableDates() throws MrzParseException {
		String unparseableDatesMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBRBB09117F2ZZ7162<<<<<<<<<<<<<<08";
		MrzRecord record = MrzParser.parse(unparseableDatesMrz);
		Assert.assertNotNull(record.getDateOfBirth());
		Assert.assertEquals(-1, record.getDateOfBirth().getYear());
		Assert.assertEquals(9, record.getDateOfBirth().getMonth());
		Assert.assertEquals(11, record.getDateOfBirth().getDay());
		Assert.assertEquals(false, record.getDateOfBirth().isDateValid());
		Assert.assertEquals(false, record.isValidDateOfBirth());

		Assert.assertNotNull(record.getExpirationDate());
		Assert.assertEquals(-1, record.getExpirationDate().getYear());
		Assert.assertEquals(-1, record.getExpirationDate().getMonth());
		Assert.assertEquals(16, record.getExpirationDate().getDay());
		Assert.assertEquals(false, record.getExpirationDate().isDateValid());
		Assert.assertEquals(false, record.isValidExpirationDate());
	}

	@Test
	public void testRawDate() throws MrzParseException {
		String validBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809117F2007162<<<<<<<<<<<<<<08";
		MrzRecord record = MrzParser.parse(validBirthDateMrz);
		Assert.assertEquals("880911", record.getDateOfBirth().toMrz());

		String invalidBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809417F2007162<<<<<<<<<<<<<<08";
		record = MrzParser.parse(invalidBirthDateMrz);
		Assert.assertEquals("880941", record.getDateOfBirth().toMrz());

		String unparseableDatesMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBRBB09117F2007162<<<<<<<<<<<<<<08";
		record = MrzParser.parse(unparseableDatesMrz);
		Assert.assertEquals("BB0911", record.getDateOfBirth().toMrz());
	}

	@Test
	public void testWithBrokenRZM() {

	}

}
