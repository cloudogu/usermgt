/*
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.scm-manager.com
 */

package de.triology.universeadm.validation;

import com.google.common.base.Strings;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class RDNValidator implements ConstraintValidator<RDN, String> {
    public static final String ERROR_TOO_SHORT = "ERROR_LENGTH_TOO_SMALL";
    public static final String ERROR_TOO_LONG = "ERROR_LENGTH_TOO_HIGH";
    public static final String ERROR_INVALID_CHARACTERS = "ERROR_INVALID_CHARACTERS";

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9-_@\\.]{2,128}");

    @Override
    public void initialize(RDN rdn) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (value.length() < 2) {
            context.buildConstraintViolationWithTemplate(ERROR_TOO_SHORT)
                .addConstraintViolation();
            return false;
        } else if (value.length() > 128){
            context.buildConstraintViolationWithTemplate(ERROR_TOO_LONG)
                .addConstraintViolation();
            return false;
        } else if (!PATTERN.matcher(Strings.nullToEmpty(value)).matches()){
            context.buildConstraintViolationWithTemplate(ERROR_INVALID_CHARACTERS)
                .addConstraintViolation();
            return false;
        }

        return true;
    }

}
