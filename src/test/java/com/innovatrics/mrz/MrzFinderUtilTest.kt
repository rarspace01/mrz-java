package com.innovatrics.mrz;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link MrzFinderUtil}.
 */
public class MrzFinderUtilTest {

	private static final String VALID_MRZ = "I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n123456<AA5SVK8110251M1801020749313<<<<<<<<70";
	private static final String VALID_MRZ_BLANK_START = "  I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n  123456<AA5SVK8110251M1801020749313<<<<<<<<70";
	private static final String VALID_MRZ_BLANK_LINES = "I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n  \n123456<AA5SVK8110251M1801020749313<<<<<<<<70";
	private static final String VALID_GER_MRZ = "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<\nC01X00T478D<<6408125F2702283<<<<<<<<<<<<<<<4";
	private static final String INVALID_MRZ = "I<SVKNOVAK<<JAN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n123456";
	private static final String WRAPPED_VALID = "xx\n\nyyy\n" + VALID_MRZ + "\nZZZZ";
  private static final String WRAPPED_VALID_GER_MRZ = "xx\n\nyyy\n" + VALID_GER_MRZ + "\nZZZZ";
	private static final String WRAPPED_INVALID_MRZ = "XX\nAZ09<\nYYY\n" + INVALID_MRZ + "\nAZ09<\nZZZZ";
	private static final String NO_GER_MRZ = "P<DE<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<\nC01X00T478D<<6408125F2702283<<<<<<<<<<<<<<<4";
	private static final String NO_MRZ = "AZ09<\n\nBBB\n\nAZ09<\nCCCCC";

	@Test
	public void testValidMrz() throws MrzNotFoundException, MrzParseException {
		Assert.assertEquals("Did not find valid MRZ", VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ));
	}

	@Test
	public void testValidMrzBlankStart() throws MrzNotFoundException, MrzParseException {
		Assert.assertEquals("Did not find valid MRZ", VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ_BLANK_START));
	}

	@Test
	public void testValidMrzBlankLines() throws MrzNotFoundException, MrzParseException {
		Assert.assertEquals("Did not find valid MRZ", VALID_MRZ, MrzFinderUtil.findMrz(VALID_MRZ_BLANK_LINES));
	}

	@Test
	public void testValidGerMrz() throws MrzNotFoundException, MrzParseException {
	  Assert.assertEquals("Did not find valid MRZ", VALID_GER_MRZ, MrzFinderUtil.findMrz(VALID_GER_MRZ));
	}

	@Test
	public void testValidWrappedMrz() throws MrzNotFoundException, MrzParseException {
		Assert.assertEquals("Did not find valid wrapped MRZ", VALID_MRZ, MrzFinderUtil.findMrz(WRAPPED_VALID));
	}

	@Test
	public void testValidWrappedGerMrz() throws MrzNotFoundException, MrzParseException {
	  Assert.assertEquals("Did not find valid wrapped MRZ", VALID_GER_MRZ, MrzFinderUtil.findMrz(WRAPPED_VALID_GER_MRZ));
	}

	@Test(expected = MrzParseException.class)
	public void testInvalidMrz() throws MrzNotFoundException, MrzParseException {
		MrzFinderUtil.findMrz(INVALID_MRZ);
	}

	@Test(expected = MrzParseException.class)
	public void testInvalidWrappedMrz() throws MrzNotFoundException, MrzParseException {
		MrzFinderUtil.findMrz(WRAPPED_INVALID_MRZ);
	}

	@Test(expected = MrzNotFoundException.class)
	public void testNotFoundMrz() throws MrzNotFoundException, MrzParseException {
		MrzFinderUtil.findMrz(NO_MRZ);
	}

  @Test(expected = MrzNotFoundException.class)
  public void testNotFoundGerMrz() throws MrzNotFoundException, MrzParseException {
    MrzFinderUtil.findMrz(NO_GER_MRZ);
  }

	@Test(expected = MrzNotFoundException.class)
	public void testNullMrz() throws MrzNotFoundException, MrzParseException {
		MrzFinderUtil.findMrz(null);
	}

	@Test(expected = MrzNotFoundException.class)
	public void testEmptyMrz() throws MrzNotFoundException, MrzParseException {
		MrzFinderUtil.findMrz("");
	}

}
