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

import com.github.rarspace01.mrz.MrzFinderUtil;
import com.github.rarspace01.mrz.MrzNotFoundException;
import com.github.rarspace01.mrz.MrzParseException;
import com.github.rarspace01.mrz.MrzParser;
import com.github.rarspace01.mrz.types.MrzDate;
import com.github.rarspace01.mrz.types.MrzDocumentCode;
import com.github.rarspace01.mrz.types.MrzSex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link FrenchIdCard}.
 *
 * @author Maximilian Moser
 */
public class PTDTest {

    private static final String PARSE = "PTD<<ALJWEER<<AHMAD<<<<<<<<<<<<<<<<<<<<<<<<<\nZ06RF5CX25SYR0101011M24092162101<<<<<<<<<<44";
    private static final String TOMRZ = "PTD<<ALJWEER<<AHMAD<<<<<<<<<<<<<<<<<<<<<<<<<\nZ06RF5CX25SYR0101011M24092162101<<<<<<<<<<44";
    private static final String WRAPPED = "xx\n\nyyy\n" + PARSE + "\nZZZZ";

    @Test
    public void testParsing() throws MrzParseException {
        final PTD r = (PTD) MrzParser.parse(PARSE);
        assertEquals(MrzDocumentCode.MIGRANT, r.getCode());
//        assertEquals("PTD", r.getCode1());
//        assertEquals('<', r.getCode2());
        assertEquals("D", r.getIssuingCountry());
        assertEquals("SYR", r.getNationality());
        assertEquals("2101", r.getPersonalNumber());
        assertEquals("Z06RF5CX2", r.getDocumentNumber());
        assertEquals(new MrzDate(24, 9, 21), r.getExpirationDate());
        assertEquals(new MrzDate(1, 1, 1), r.getDateOfBirth());
        assertEquals(MrzSex.MALE, r.getSex());
        assertEquals("ALJWEER", r.getSurname());
        assertEquals("AHMAD", r.getGivenNames());
    }

    @Test
    public void testToMrz() {
        final MRP r = new MRP();
        r.setCode1('P');
        r.setCode2('T');
        r.setIssuingCountry("D");
        r.setNationality("SYR");
        r.setPersonalNumber("2101");
        r.setDocumentNumber("Z06RF5CX2");
        r.setExpirationDate(new MrzDate(24, 9, 21));
        r.setDateOfBirth(new MrzDate(1, 1, 1));
        r.setSex(MrzSex.MALE);
        r.setSurname("ALJWEER");
        r.setGivenNames("AHMAD");
        assertEquals(TOMRZ.replace("\n", ""), r.toMrz().replace("\n", ""));
    }

    @Test
    public void testFindMrz() throws MrzNotFoundException, MrzParseException {
        assertEquals(PARSE.trim(), MrzFinderUtil.findMrz(PARSE), "Did not find MRZ");
    }

    @Test
    public void testFindMrzWrapped() throws MrzNotFoundException, MrzParseException {
        assertEquals(PARSE.trim(), MrzFinderUtil.findMrz(WRAPPED), "Did not find wrapped MRZ");
    }

}
