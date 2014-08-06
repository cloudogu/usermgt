/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.validation;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.junit.Test;

/**
 *
 * @author ssdorra
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
  public void testRDNValid()
  {
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
  public void testRDNInvalidEmpty()
  {
    validator.validate(new RDNObject(""), "not valid");
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
