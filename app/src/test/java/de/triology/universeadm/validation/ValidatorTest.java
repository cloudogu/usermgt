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
    validator.validate(new RDNObject("trillian.mcmillan.ford.prefect.arthur.dent@hitchhiker24.com"), "not valid");
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
