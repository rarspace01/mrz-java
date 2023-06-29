package com.github.rarspace01.mrz;

import com.github.rarspace01.mrz.types.MrzFormat;

/**
 * MrzRecord with Optional field.
 */
public abstract class MrzRecordOptional extends MrzRecord {

	/**
	 * For use of the issuing State or organization.
	 */
	private String optional;

	/**
	 * @param format the record format
	 * @param recordName the record name
	 */
	protected MrzRecordOptional(final MrzFormat format, final String recordName) {
		super(format, recordName);
	}

	@Override
	protected void buildToString(final StringBuilder sb) {
		super.buildToString(sb);
		sb.append(", optional=").append(getOptional());
	}

	/**
	 * @return the issuing State or Organization
	 */
	public String getOptional() {
		return optional;
	}

	/**
	 * @param optional the issuing State or Organization
	 */
	public void setOptional(final String optional) {
		this.optional = optional;
	}

}
