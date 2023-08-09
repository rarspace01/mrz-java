/**
 * Java parser for the MRZ records, as specified by the ICAO organization.
 * Copyright (C) 2011 Innovatrics s.r.o.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.github.rarspace01.mrz.types

import com.github.rarspace01.mrz.MrzParseException
import com.github.rarspace01.mrz.MrzRange

/**
 * Lists all supported MRZ record types (a.k.a. document codes).
 *
 * @author Martin Vysny
 */
enum class MrzDocumentCode {
    /**
     * A passport, P or IP. ... maybe Travel Document that is very similar to the passport.
     */
    PASSPORT,

    /**
     * General I type (besides IP).
     */
    TYPE_I,

    /**
     * General A type (besides AC).
     */
    TYPE_A,

    /**
     * Crew member (AC).
     */
    CREW_MEMBER,

    /**
     * General type C.
     */
    TYPE_C,

    /**
     * Type V (Visa).
     */
    TYPE_V,

    /**
     *
     */
    MIGRANT;

    companion object {
        /**
         * @author Zsombor turning to switch statement due to lots of types
         *
         * @param mrz the mrz string
         * @return the mrz document code
         * @throws MrzParseException could not parse MRZ
         */
        @JvmStatic
		@Throws(MrzParseException::class)
        fun parse(mrz: String): MrzDocumentCode? {
            val code = mrz.substring(0, 2)
            when (code) {
                "IV" -> throw MrzParseException("IV document code is not allowed", mrz, MrzRange(0, 2, 0), null) // TODO why?
                "AC" -> return CREW_MEMBER
                "ME" -> return MIGRANT
                "TD" -> return MIGRANT // travel document
                "PT" -> return MIGRANT
                "IP" -> return PASSPORT
                else -> {}
            }
            return when (code[0]) {
                'T', 'P' -> PASSPORT
                'A' -> TYPE_A
                'C' -> TYPE_C
                'V' -> TYPE_V
                'I' -> TYPE_I // identity card or residence permit
                'R' -> MIGRANT // swedish '51 Convention Travel Document
                else -> {println("Unsupported document code: $code $mrz"); null
                }
            }
        }
    }
}
