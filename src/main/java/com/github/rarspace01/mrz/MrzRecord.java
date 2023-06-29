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
package com.github.rarspace01.mrz;

import com.github.rarspace01.mrz.types.MrzDate;
import com.github.rarspace01.mrz.types.MrzDocumentCode;
import com.github.rarspace01.mrz.types.MrzFormat;
import com.github.rarspace01.mrz.types.MrzSex;

import java.io.Serializable;

/**
 * An abstract MRZ record, contains basic information present in all MRZ record types.
 *
 * @author Martin Vysny
 */
public abstract class MrzRecord implements Serializable {

    /**
     * Detected MRZ format.
     */
    private final MrzFormat format;

    /**
     * Name used for record type.
     */
    private final String recordName;

    /**
     * The document code.
     */
    private MrzDocumentCode code;
    /**
     * Document code, see {@link MrzDocumentCode} for details on allowed values.
     */
    private char code1;
    /**
     * For MRTD: Type, at discretion of states, but 1-2 should be IP for passport card, AC for crew member and IV is not allowed. For MRP: Type (for
     * countries that distinguish between different types of passports).
     */
    private char code2;

    /**
     * Issuing country.
     * <p>
     * An <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3">ISO 3166-1 alpha-3</a> country code of issuing country, with additional allowed
     * values (according to <a href="http://en.wikipedia.org/wiki/Machine-readable_passport">article on Wikipedia</a>):
     * <ul><li>D: Germany</li>
     * <li>GBD: British dependent territories citizen(note: the country code of the overseas territory is presently used to indicate issuing authority
     * and nationality of BOTC)</li>
     * <li>GBN: British National (Overseas)</li>
     * <li>GBO: British Overseas citizen</li>
     * <li>GBP: British protected person</li>
     * <li>GBS: British subject</li>
     * <li>UNA: specialized agency of the United Nations</li>
     * <li>UNK: resident of Kosovo to whom a travel document has been issued by the United Nations Interim Administration Mission in Kosovo
     * (UNMIK)</li>
     * <li>UNO: United Nations Organization</li>
     * <li>XOM: Sovereign Military Order of Malta</li>
     * <li>XXA: stateless person, as per the 1954 Convention Relating to the Status of Stateless Persons</li>
     * <li>XXB: refugee, as per the 1951 Convention Relating to the Status of Refugees</li>
     * <li>XXC: refugee, other than defined above</li>
     * <li>XXX: unspecified nationality</li></ul>
     */
    private String issuingCountry;
    /**
     * Document number, e.g. passport number.
     */
    private String documentNumber;
    /**
     * The surname in uppercase.
     */
    private String surname;
    /**
     * The given names in uppercase, separated by spaces.
     */
    private String givenNames;
    /**
     * Date of birth.
     */
    private MrzDate dateOfBirth;
    /**
     * Sex.
     */
    private MrzSex sex;
    /**
     * Expiration date of passport.
     */
    private MrzDate expirationDate;
    /**
     * An <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3">ISO 3166-1 alpha-3</a> country code of nationality. See {@link #issuingCountry}
     * for additional allowed values.
     */
    private String nationality;

    /**
     * Check digits, usually common in every document.
     */
    private boolean validDocumentNumber = true;
    private boolean validDateOfBirth = true;
    private boolean validExpirationDate = true;
    private boolean validComposite = true;

    /**
     * @param format     the record format
     * @param recordName the record name
     */
    protected MrzRecord(final MrzFormat format, final String recordName) {
        this.format = format;
        this.recordName = recordName;
    }

    /**
     * @return the format
     */
    public final MrzFormat getFormat() {
        return format;
    }

    /**
     * @return the record name
     */
    public final String getRecordName() {
        return recordName;
    }

    /**
     * Parses the MRZ record.
     *
     * @param mrz the mrz record, not null, separated by \n
     * @throws MrzParseException could not parse MRZ
     */
    public void fromMrz(final String mrz) throws MrzParseException {
        if (getFormat() != MrzFormat.get(mrz)) {
            System.out.println("Invalid format: " + MrzFormat.get(mrz) + " " + mrz);
            return;
        }
        setCode(MrzDocumentCode.parse(mrz));
        setCode1(mrz.charAt(0));
        setCode2(mrz.charAt(1));
        setIssuingCountry(new MrzParser(mrz).parseString(new MrzRange(2, 5, 0)));
    }

    /**
     * Helper method to set the full name. Changes both {@link #surname} and {@link #givenNames}.
     *
     * @param name expected array of length 2, in the form of [surname, first_name]. Must not be null.
     */
    protected final void setName(final String[] name) {
        setSurname(name[0]);
        setGivenNames(name[1]);
    }

    /**
     * Serializes this record to a valid MRZ record.
     *
     * @return a valid MRZ record, not null, separated by \n
     */
    public abstract String toMrz();

    /**
     * @return the document code
     */
    public MrzDocumentCode getCode() {
        return code;
    }

    /**
     * @param code the document code
     */
    public void setCode(final MrzDocumentCode code) {
        this.code = code;
    }

    /**
     * @return the document code1
     */
    public char getCode1() {
        return code1;
    }

    /**
     * @param code1 the document code1
     */
    public void setCode1(final char code1) {
        this.code1 = code1;
    }

    /**
     * @return the document code2
     */
    public char getCode2() {
        return code2;
    }

    /**
     * @param code2 the document code2
     */
    public void setCode2(final char code2) {
        this.code2 = code2;
    }

    /**
     * @return the issuing country
     */
    public String getIssuingCountry() {
        return issuingCountry;
    }

    /**
     * @param issuingCountry the issuing country
     */
    public void setIssuingCountry(final String issuingCountry) {
        this.issuingCountry = issuingCountry;
    }

    /**
     * @return the document number
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @param documentNumber the document number
     */
    public void setDocumentNumber(final String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname the surname
     */
    public void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * @return the given names
     */
    public String getGivenNames() {
        return givenNames;
    }

    /**
     * @param givenNames the given names
     */
    public void setGivenNames(final String givenNames) {
        this.givenNames = givenNames;
    }

    /**
     * @return the date of birth
     */
    public MrzDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth the date of birth
     */
    public void setDateOfBirth(final MrzDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the sex
     */
    public MrzSex getSex() {
        return sex;
    }

    /**
     * @param sex the sex
     */
    public void setSex(final MrzSex sex) {
        this.sex = sex;
    }

    /**
     * @return the expiration date
     */
    public MrzDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * @param expirationDate the expiration date
     */
    public void setExpirationDate(final MrzDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * @return the nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * @param nationality the nationality
     */
    public void setNationality(final String nationality) {
        this.nationality = nationality;
    }

    /**
     * @return true if valid document number
     */
    public boolean isValidDocumentNumber() {
        return validDocumentNumber;
    }

    /**
     * @param validDocumentNumber true if valid document number
     */
    protected void setValidDocumentNumber(final boolean validDocumentNumber) {
        this.validDocumentNumber = validDocumentNumber;
    }

    /**
     * @return true if valid date of birth
     */
    public boolean isValidDateOfBirth() {
        return validDateOfBirth;
    }

    /**
     * @param validDateOfBirth true if valid date of birth
     */
    protected void setValidDateOfBirth(final boolean validDateOfBirth) {
        this.validDateOfBirth = validDateOfBirth;
    }

    /**
     * @return true if valid expiration date
     */
    public boolean isValidExpirationDate() {
        return validExpirationDate;
    }

    /**
     * @param validExpirationDate true if valid expiration date to set
     */
    protected void setValidExpirationDate(final boolean validExpirationDate) {
        this.validExpirationDate = validExpirationDate;
    }

    /**
     * @return true if valid composite
     */
    public boolean isValidComposite() {
        return validComposite;
    }

    /**
     * @param validComposite true if valid composite
     */
    protected void setValidComposite(final boolean validComposite) {
        this.validComposite = validComposite;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getRecordName());
        sb.append("{");
        buildToString(sb);
        sb.append("}");
        return sb.toString();
    }

    /**
     * @param sb the string builder to hold the record details
     */
    protected void buildToString(final StringBuilder sb) {
        sb.append("code=").append(getCode());
        sb.append('[').append(getCode1()).append(getCode2()).append(']');
        sb.append(", issuingCountry=").append(getIssuingCountry());
        sb.append(", documentNumber=").append(getDocumentNumber());
        sb.append(", surname=").append(getSurname());
        sb.append(", givenNames=").append(getGivenNames());
        sb.append(", dateOfBirth=").append(getDateOfBirth());
        sb.append(", sex=").append(getSex());
        sb.append(", expirationDate=").append(getExpirationDate());
        sb.append(", nationality=").append(getNationality());
    }

}
