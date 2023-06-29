package com.github.rarspace01.mrz;

/**
 * Could not find MRZ Exception.
 */
public class MrzNotFoundException extends Exception {

	/**
	 * Default constructor.
	 */
	public MrzNotFoundException() {
		super("Could not find a MRZ");
	}
}
