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
 * Tests {@link MrvA}.
 *
 * @author Martin Vysny
 */
public class MrvATest {

    private static final String PARSE = "V<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<<<<<<<<<\nL898902C<3UTO6908061F9406236ZE184226B<<<<<<<\n";
    private static final String TOMRZ = "V<FRANOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\nABCDE12346FRA8110251M1801020123456<<<<<<<<<<\n";
    private static final String WRAPPED = "xx\n\nyyy\n" + PARSE + "\nZZZZ";

    @Test
    public void testMrvVisaACardParsing() throws MrzParseException {
        final MrvA r = (MrvA) MrzParser.parse(PARSE);
        assertEquals(MrzDocumentCode.TYPE_V, r.getCode());
        assertEquals('V', r.getCode1());
        assertEquals('<', r.getCode2());
        assertEquals("UTO", r.getIssuingCountry());
        assertEquals("UTO", r.getNationality());
        assertEquals("L898902C", r.getDocumentNumber());
        assertEquals(new MrzDate(94, 6, 23), r.getExpirationDate());
        assertEquals("ZE184226B", r.getOptional());
        assertEquals(new MrzDate(69, 8, 6), r.getDateOfBirth());
        assertEquals(MrzSex.FEMALE, r.getSex());
        assertEquals("ERIKSSON", r.getSurname());
        assertEquals("ANNA MARIA", r.getGivenNames());
    }

    @Test
    public void testMrvVisaAMrz() {
        final MrvA r = new MrvA();
        r.setIssuingCountry("FRA");
        r.setNationality("FRA");
        r.setOptional("123456");
        r.setDocumentNumber("ABCDE1234512");
        r.setExpirationDate(new MrzDate(18, 1, 2));
        r.setDateOfBirth(new MrzDate(81, 10, 25));
        r.setSex(MrzSex.MALE);
        r.setSurname("NOVAK");
        r.setGivenNames("JAN");
        assertEquals(TOMRZ, r.toMrz());
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
