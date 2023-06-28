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

import com.innovatrics.mrz.MrzFinderUtil;
import com.innovatrics.mrz.MrzNotFoundException;
import com.innovatrics.mrz.MrzParseException;
import com.innovatrics.mrz.MrzParser;
import com.innovatrics.mrz.types.MrzDate;
import com.innovatrics.mrz.types.MrzDocumentCode;
import com.innovatrics.mrz.types.MrzSex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link MrtdTd2}.
 *
 * @author Martin Vysny
 */
public class MrtdTd2Test {

    private static final String PARSE = "I<UTOSTEVENSON<<PETER<<<<<<<<<<<<<<<\nD231458907UTO3407127M9507122<<<<<<<2";
    private static final String TOMRZ = "I<UTOSTEVENSON<<PETER<<<<<<<<<<<<<<<\nD231458907UTO3407127M9507122<<<<<<<2\n";
    private static final String WRAPPED = "xx\n\nyyy\n" + PARSE + "\nZZZZ";

    @Test
    public void testTd2Parsing() throws MrzParseException {
        final MrtdTd2 r = (MrtdTd2) MrzParser.parse(PARSE);
        assertEquals(MrzDocumentCode.TYPE_I, r.getCode());
        assertEquals('I', r.getCode1());
        assertEquals('<', r.getCode2());
        assertEquals("UTO", r.getIssuingCountry());
        assertEquals("UTO", r.getNationality());
        assertEquals("", r.getOptional());
        assertEquals("D23145890", r.getDocumentNumber());
        assertEquals(new MrzDate(95, 7, 12), r.getExpirationDate());
        assertEquals(new MrzDate(34, 7, 12), r.getDateOfBirth());
        assertEquals(MrzSex.MALE, r.getSex());
        assertEquals("STEVENSON", r.getSurname());
        assertEquals("PETER", r.getGivenNames());
    }

    @Test
    public void testToMrz() {
        final MrtdTd2 r = new MrtdTd2();
        r.setCode1('I');
        r.setCode2('<');
        r.setIssuingCountry("UTO");
        r.setNationality("UTO");
        r.setOptional("");
        r.setDocumentNumber("D23145890");
        r.setExpirationDate(new MrzDate(95, 7, 12));
        r.setDateOfBirth(new MrzDate(34, 7, 12));
        r.setSex(MrzSex.MALE);
        r.setSurname("STEVENSON");
        r.setGivenNames("PETER");
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
