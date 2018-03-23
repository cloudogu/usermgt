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

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.junit.Test;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class ValidatorTest
{

  private final Validator validator = new HibernateValidator(Validation.buildDefaultValidatorFactory());
  
  @Test
  public void testValidObject()
  {
    validator.validate(new VObject("test"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testNotValid()
  {
    validator.validate(new VObject(""), "not valid");
  }

  @Test
  public void testRDNValidMinus(){
    validator.validate(new VObject("ab-ab"), "not valid");
  }
  
  @Test
  public void testRDNValidUnderscore(){
    validator.validate(new VObject("ab_ab"), "not valid");
  }
  
  
  @Test
  public void testRDNValidChars(){
    validator.validate(new VObject("aB_ab09041985"), "not valid");
  }
  
  @Test
  public void testRDNValid()
  {
    validator.validate(new RDNObject("trillian.mcmillan.ford.prefect.arthur.dent@hitchhiker.com"), "not valid");
    validator.validate(new RDNObject("dent"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidEqual()
  {
    validator.validate(new RDNObject("cn=dent"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidComma()
  {
    validator.validate(new RDNObject("dent,ou"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidMaxLength()
  {
    StringBuilder builder = new StringBuilder("d");
    for (int i=0; i<32; i++) {
      builder.append("dent");
    }
    System.out.println(builder.toString().length());
    validator.validate(new RDNObject(builder.toString()), "not valid");
  }

  @Test
  public void testRDNMaxLength()
  {
    validator.validate(new RDNObject("dentdentdentdentdentdentdentdent"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidMinLength()
  {
    validator.validate(new RDNObject("a"), "not valid");
  }

  @Test
  public void testRDNMinLength()
  {
    validator.validate(new RDNObject("as"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidEmpty()
  {
    validator.validate(new RDNObject(""), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidSlash()
  {
    validator.validate(new RDNObject("as/as"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidPercent()
  {
    validator.validate(new RDNObject("as%as"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidStar()
  {
    validator.validate(new RDNObject("as*as"), "not valid");
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void testRDNInvalidNull()
  {
    validator.validate(new RDNObject(null), "not valid");
  }
  
  private static class RDNObject {
    
    @RDN
    private final String rdn;

    public RDNObject(String rdn)
    {
      this.rdn = rdn;
    }
  }
  
  private static class VObject {
    
    @NotNull
    @Size(min=2)
    private final String name;

    public VObject(String name)
    {
      this.name = name;
    }
    
  }
  
}
