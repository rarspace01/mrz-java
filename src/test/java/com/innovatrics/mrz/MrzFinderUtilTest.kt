package com.innovatrics.mrz

import org.junit.Assert
import org.junit.Test

/**
 * Unit tests for [MrzFinderUtil].
 */
class MrzFinderUtilTest {
    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidMrz() {
        Assert.assertEquals("Did not find valid MRZ", VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ))
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidMrzBlankStart() {
        Assert.assertEquals("Did not find valid MRZ", VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ_BLANK_START))
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidMrzBlankLines() {
        Assert.assertEquals("Did not find valid MRZ", VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ_BLANK_LINES))
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidGerMrz() {
        Assert.assertEquals("Did not find valid MRZ", VALID_GER_MRZ, MrzFinderUtil.findMrz(VALID_GER_MRZ))
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidWrappedMrz() {
        Assert.assertEquals("Did not find valid wrapped MRZ", VALID_MRZ, MrzFinderUtil.findMrz(WRAPPED_VALID))
    }

    @Test
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testValidWrappedGerMrz() {
        Assert.assertEquals("Did not find valid wrapped MRZ", VALID_GER_MRZ, MrzFinderUtil.findMrz(WRAPPED_VALID_GER_MRZ))
    }

    @Test(expected = MrzParseException::class)
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testInvalidMrz() {
        MrzFinderUtil.findMrz(INVALID_MRZ)
    }

    @Test(expected = MrzParseException::class)
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testInvalidWrappedMrz() {
        MrzFinderUtil.findMrz(WRAPPED_INVALID_MRZ)
    }

    @Test(expected = MrzNotFoundException::class)
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testNotFoundMrz() {
        MrzFinderUtil.findMrz(NO_MRZ)
    }

    @Test(expected = MrzNotFoundException::class)
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testNotFoundGerMrz() {
        MrzFinderUtil.findMrz(NO_GER_MRZ)
    }

    @Test(expected = MrzNotFoundException::class)
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testNullMrz() {
        MrzFinderUtil.findMrz(null)
    }

    @Test(expected = MrzNotFoundException::class)
    @Throws(MrzNotFoundException::class, MrzParseException::class)
    fun testEmptyMrz() {
        MrzFinderUtil.findMrz("")
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
