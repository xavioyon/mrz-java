/**
 * Java parser for the MRZ records, as specified by the ICAO organization.
 * Copyright (C) 2011 Innovatrics s.r.o.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.innovatrics.mrz.records;

import com.innovatrics.mrz.MrzParser;
import com.innovatrics.mrz.MrzRange;
import com.innovatrics.mrz.MrzRecord;
import com.innovatrics.mrz.types.MrzDocumentCode;
import com.innovatrics.mrz.types.MrzFormat;

/**
 * Format used for French ID Cards
 * @author Pierrick Martin
 */
public class FrenchIdCard extends MrzRecord {

    private static final long serialVersionUID = 1L;

    public FrenchIdCard() {
        super(MrzFormat.FRENCH_ID);
        code = MrzDocumentCode.TypeI;
        code1 = 'I';
        code2 = 'D';
    }
    /**
     * For use of the issuing State or 
    organization. Unused character positions 
    shall be completed with filler characters (&lt;)
    repeated up to position 35 as required. 
     */
    public String optional;

    @Override
    public void fromMrz(String mrz) {
        super.fromMrz(mrz);
        final MrzParser p = new MrzParser(mrz);
        //Special because surname and firstname not on the same line
        String[] name = new String[]{"", ""};
        name[0] = p.parseString(new MrzRange(5, 25, 0));
        name[1] = p.parseString(new MrzRange(13, 27, 1));
        setName(name);
        nationality = p.parseString(new MrzRange(2, 5, 0));
        optional = p.parseString(new MrzRange(25, 36, 0));
        documentNumber = p.parseString(new MrzRange(0, 12, 1));
        p.checkDigit(12, 1, new MrzRange(0, 12, 1), "document number");
        dateOfBirth = p.parseDate(new MrzRange(27, 33, 1));
        p.checkDigit(33, 1, new MrzRange(27, 33, 1), "date of birth");
        sex = p.parseSex(34, 1);
        // TODO last character is probably a checksum of multiple fields
        // TODO expirationDate is missing
    }

    @Override
    public String toString() {
        return "FrenchCard{" + super.toString() + ", optional=" + optional + '}';
    }

    @Override
    public String toMrz() {
        final StringBuilder sb = new StringBuilder("IDFRA");
        // first row
        sb.append(MrzParser.toMrz(surname, 20));
        sb.append(MrzParser.toMrz(optional, 11));
        sb.append('\n');
        // second row
        sb.append(MrzParser.toMrz(documentNumber, 12));
        sb.append(MrzParser.computeCheckDigitChar(MrzParser.toMrz(documentNumber, 12)));
        sb.append(MrzParser.toMrz(givenNames, 14));
        sb.append(dateOfBirth.toMrz());
        sb.append(MrzParser.computeCheckDigitChar(dateOfBirth.toMrz()));
        sb.append(sex.mrz);
        // TODO last character is probably a checksum of multiple fields
        sb.append('<');
        sb.append('\n');
        return sb.toString();
    }
}
