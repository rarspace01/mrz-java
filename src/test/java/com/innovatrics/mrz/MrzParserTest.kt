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
package com.innovatrics.mrz

import com.innovatrics.mrz.types.MrzDate
import org.junit.Assert
import org.junit.Test

/**
 * Tests the parser.
 *
 * @author Martin Vysny
 */
class MrzParserTest {
    /**
     * Test of computeCheckDigit method, of class MrzRecord.
     */
    @Test
    fun testComputeCheckDigit() {
        Assert.assertEquals(3, MrzParser.computeCheckDigit("520727").toLong())
        Assert.assertEquals(2, MrzParser.computeCheckDigit("D231458907<<<<<<<<<<<<<<<34071279507122<<<<<<<<<<").toLong())
        Assert.assertEquals('3'.code.toLong(), MrzParser.computeCheckDigitChar("520727").code.toLong())
        Assert.assertEquals('2'.code.toLong(), MrzParser.computeCheckDigitChar("D231458907<<<<<<<<<<<<<<<34071279507122<<<<<<<<<<").code.toLong())
    }

    @Test
    @Throws(MrzParseException::class)
    fun testValidCheckDigit() {
        val CzechPassport = "P<CZESPECIMEN<<VZOR<<<<<<<<<<<<<<<<<<<<<<<<<\n99003853<1CZE1101018M1207046110101111<<<<<94"
        Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidDateOfBirth)
        Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidExpirationDate)
        Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidDocumentNumber)
        Assert.assertEquals(true, MrzParser.parse(CzechPassport).isValidComposite)
        val GermanPassport = "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<\nC01X01R741D<<6408125F2010315<<<<<<<<<<<<<<<9"
        Assert.assertEquals(true, MrzParser.parse(GermanPassport).isValidDateOfBirth)
        Assert.assertEquals(true, MrzParser.parse(GermanPassport).isValidExpirationDate)
        Assert.assertEquals(true, MrzParser.parse(GermanPassport).isValidDocumentNumber)
        Assert.assertEquals(false, MrzParser.parse(GermanPassport).isValidComposite) // yes, this specimen has intentationally wrong check digit
    }

    @Test
    @Throws(MrzParseException::class)
    fun testDateParsing() {
        Assert.assertEquals(
            MrzDate(34, 7, 12),
            MrzParser("CIUTOD231458907A123X5328434D23\n3407127M9507122UTO<<<<<<<<<<<6\nSTEVENSON<<PETER<<<<<<<<<<<<<<\n").parseDate(MrzRange(0, 6, 1))
        )
        Assert.assertEquals(
            MrzDate(95, 12, 1),
            MrzParser("CIUTOD231458907A123X5328434D23\n3407127M9512012UTO<<<<<<<<<<<6\nSTEVENSON<<PETER<<<<<<<<<<<<<<\n").parseDate(MrzRange(8, 14, 1))
        )
    }

    @Test
    fun testToMrz() {
        // \u010d = č
        Assert.assertEquals("CACACA<<<<<", MrzParser.toMrz("\u010da\u010da\u010da", 11))
        Assert.assertEquals("HERBERT<<FRANK<<<", MrzParser.toMrz("Herbert  Frank", 17))
        Assert.assertEquals("PAT<<MAT", MrzParser.toMrz("Pat, Mat", 8))
        Assert.assertEquals("FOO<", MrzParser.toMrz("foo bar baz", 4))
        Assert.assertEquals("<<<<<<<<", MrzParser.toMrz("*$()&/\\", 8))
        Assert.assertEquals("AEAEIJIJ", MrzParser.toMrz("\u00C4\u00E4\u0132\u0133", 8))
        Assert.assertEquals("OEOE", MrzParser.toMrz("\u00D6\u00F6", 4))
        Assert.assertEquals("DART", MrzParser.toMrz("D’Artagnan", 4))
        Assert.assertEquals("DART", MrzParser.toMrz("D'Artagnan", 4))
    }

    @Test
    fun testNameToMrz() {
        Assert.assertEquals("HERBERT<<FRANK<<<", MrzParser.nameToMrz("Herbert", "Frank", 17))
        Assert.assertEquals("ERIKSSON<<ANNA<MARIA<<<", MrzParser.nameToMrz("Eriksson", "Anna, Maria", 23))
        // test name truncating
        Assert.assertEquals("PAPANDROPOULOUS<<JONATHOON<ALEC", MrzParser.nameToMrz("Papandropoulous", "Jonathoon Alec", 31))
        Assert.assertEquals("NILAVADHANANANDA<<CHAYAPA<DEJ<K", MrzParser.nameToMrz("Nilavadhanananda", "Chayapa Dejthamrong Krasuang", 31))
        Assert.assertEquals("NILAVADHANANANDA<<ARNPOL<PETC<C", MrzParser.nameToMrz("NILAVADHANANANDA", "ARNPOL PETCH CHARONGUANG", 31))
        Assert.assertEquals("BENNELONG<WOOLOOMOOLOO<W<W<<D<P", MrzParser.nameToMrz("BENNELONG WOOLOOMOOLOO WARRANDYTE WARNAMBOOL", "DINGO POTOROO", 31))
    }

    @Test
    @Throws(MrzParseException::class)
    fun testValidDates() {
        val validBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809117F2007162<<<<<<<<<<<<<<08"
        val record = MrzParser.parse(validBirthDateMrz)
        Assert.assertEquals(true, record.dateOfBirth.isDateValid)
        Assert.assertEquals(true, record.expirationDate.isDateValid)
        Assert.assertEquals(true, record.isValidDateOfBirth)
        Assert.assertEquals(true, record.isValidExpirationDate)
    }

    @Test
    @Throws(MrzParseException::class)
    fun testMrzInvalidBirthDate() {
        val invalidBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809417F2007162<<<<<<<<<<<<<<08"
        val record = MrzParser.parse(invalidBirthDateMrz)
        Assert.assertEquals(false, record.dateOfBirth.isDateValid)
        Assert.assertEquals(false, record.isValidDateOfBirth)
    }

    @Test
    @Throws(MrzParseException::class)
    fun testMrzInvalidExpiryDate() {
        val invalidExpiryDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809117F2007462<<<<<<<<<<<<<<08"
        val record = MrzParser.parse(invalidExpiryDateMrz)
        Assert.assertEquals(false, record.expirationDate.isDateValid)
        Assert.assertEquals(false, record.isValidExpirationDate)
    }

    @Test
    @Throws(MrzParseException::class)
    fun testUnparseableDates() {
        val unparseableDatesMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBRBB09117F2ZZ7162<<<<<<<<<<<<<<08"
        val record = MrzParser.parse(unparseableDatesMrz)
        Assert.assertNotNull(record.dateOfBirth)
        Assert.assertEquals(-1, record.dateOfBirth.year.toLong())
        Assert.assertEquals(9, record.dateOfBirth.month.toLong())
        Assert.assertEquals(11, record.dateOfBirth.day.toLong())
        Assert.assertEquals(false, record.dateOfBirth.isDateValid)
        Assert.assertEquals(false, record.isValidDateOfBirth)
        Assert.assertNotNull(record.expirationDate)
        Assert.assertEquals(-1, record.expirationDate.year.toLong())
        Assert.assertEquals(-1, record.expirationDate.month.toLong())
        Assert.assertEquals(16, record.expirationDate.day.toLong())
        Assert.assertEquals(false, record.expirationDate.isDateValid)
        Assert.assertEquals(false, record.isValidExpirationDate)
    }

    @Test
    @Throws(MrzParseException::class)
    fun testRawDate() {
        val validBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809117F2007162<<<<<<<<<<<<<<08"
        var record = MrzParser.parse(validBirthDateMrz)
        Assert.assertEquals("880911", record.dateOfBirth.toMrz())
        val invalidBirthDateMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBR8809417F2007162<<<<<<<<<<<<<<08"
        record = MrzParser.parse(invalidBirthDateMrz)
        Assert.assertEquals("880941", record.dateOfBirth.toMrz())
        val unparseableDatesMrz = "P<GBRUK<SPECIMEN<<ANGELA<ZOE<<<<<<<<<<<<<<<<\n9250764733GBRBB09117F2007162<<<<<<<<<<<<<<08"
        record = MrzParser.parse(unparseableDatesMrz)
        Assert.assertEquals("BB0911", record.dateOfBirth.toMrz())
    }

    @Test
    fun testWithBrokenRZM() {
    }
}
