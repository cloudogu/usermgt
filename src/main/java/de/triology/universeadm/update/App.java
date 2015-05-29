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
package de.triology.universeadm.update;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author mbehlendorf
 */
public class App {
  
  
  public static void main(String[] args) throws IOException{
    
    ObjectMapper mapper = new ObjectMapper();
    Person person = new Person("sorbot", "haxor");
    String json = mapper.writeValueAsString(person);
    System.out.println(json);
    Person personFromJson = mapper.readValue(json, Person.class);
    System.out.println(personFromJson.getGivenname());
    System.out.println(personFromJson.getSurname());
  }
  
  public static class Person {
    
    private String givenname;
    private String surname;

    Person() {
    }
    
    public Person(String givenname, String surname) {
      this.givenname = givenname;
      this.surname = surname;
    }

    public String getGivenname() {
      return givenname;
    }

    public String getSurname() {
      return surname;
    }
    
  }
  
}
