package com.github.rarspace01.mrz

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for [MrzFinderUtil].
 */
class MrzFinderUtilTest {
    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidMrz() {
        assertEquals(VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ), "Did not find valid MRZ")
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidMrzBlankStart() {
        assertEquals(VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ_BLANK_START), "Did not find valid MRZ")
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidMrzBlankLines() {
        assertEquals(VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ_BLANK_LINES), "Did not find valid MRZ")
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidGerMrz() {
        assertEquals(VALID_GER_MRZ, MrzFinderUtil.findMrz(VALID_GER_MRZ), "Did not find valid MRZ")
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidWrappedMrz() {
        assertEquals(VALID_MRZ, MrzFinderUtil.findMrz(WRAPPED_VALID), "Did not find valid wrapped MRZ")
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidWrappedGerMrz() {
        assertEquals(VALID_GER_MRZ, MrzFinderUtil.findMrz(WRAPPED_VALID_GER_MRZ), "Did not find valid wrapped MRZ")
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testInvalidMrz() {
        assertThrows<MrzParseException> {
            MrzFinderUtil.findMrz(INVALID_MRZ)
        }
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testInvalidWrappedMrz() {
        assertThrows<MrzParseException> {
            MrzFinderUtil.findMrz(WRAPPED_INVALID_MRZ)
        }
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testNotFoundMrz() {
        assertThrows<MrzNotFoundException> {
            MrzFinderUtil.findMrz(NO_MRZ)
        }
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testNotFoundGerMrz() {
        assertThrows<MrzNotFoundException> {
            MrzFinderUtil.findMrz(NO_GER_MRZ)
        }
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testNullMrz() {
        assertThrows<MrzNotFoundException> {
            MrzFinderUtil.findMrz(null)
        }
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testEmptyMrz() {
        assertThrows<MrzNotFoundException> {
            MrzFinderUtil.findMrz("")
        }
    }

    companion object {
        private const val VALID_MRZ = "I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n123456<AA5SVK8110251M1801020749313<<<<<<<<70"
        private const val VALID_MRZ_BLANK_START = "  I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n  123456<AA5SVK8110251M1801020749313<<<<<<<<70"
        private const val VALID_MRZ_BLANK_LINES = "I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n  \n123456<AA5SVK8110251M1801020749313<<<<<<<<70"
        private const val VALID_GER_MRZ = "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<\nC01X00T478D<<6408125F2702283<<<<<<<<<<<<<<<4"
        private const val INVALID_MRZ = "I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n123456"
        private const val WRAPPED_VALID = """xx

yyy
$VALID_MRZ
ZZZZ"""
        private const val WRAPPED_VALID_GER_MRZ = """xx

yyy
$VALID_GER_MRZ
ZZZZ"""
        private const val WRAPPED_INVALID_MRZ = """XX
AZ09<
YYY
$INVALID_MRZ
AZ09<
ZZZZ"""
        private const val NO_GER_MRZ = "P<DE<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<\nC01X00T478D<<6408125F2702283<<<<<<<<<<<<<<<4"
        private const val NO_MRZ = "AZ09<\n\nBBB\n\nAZ09<\nCCCCC"
    }
}
