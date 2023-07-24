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

package de.triology.universeadm.user;

import com.google.common.collect.Lists;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class Users {
    private Users() {
    }

    public static User createDent() {
        return new User(
                "dent", "Arthur Dent", "Arthur", "Dent",
                "arthur.dent@hitchhiker.com", "hitchhiker123", true,
                Lists.newArrayList("Hitchhiker")
        );
    }

    public static User createDent2() {
        return new User(
                "dent2", "Arthur Dent", "Arthur", "Dent",
                "arthur.dent@hitchhiker.com", "hitchhiker123", false,
                Lists.newArrayList("Hitchhiker")
        );
    }

    public static User createTrillian() {
        return new User(
                "trillian", "Tricia McMillan", "Tricia", "McMillan",
                "tricia.mcmillan@hitchhiker.com", "hitchhiker123", false,
                Lists.newArrayList("Hitchhiker")
        );
    }
}
