/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.validation;

import com.google.common.base.Strings;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author ssdorra
 */
public class RDNValidator implements ConstraintValidator<RDN, String>
{

  @Override
  public void initialize(RDN rdn)
  {

  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context)
  {
    return !Strings.isNullOrEmpty(value) && !value.contains(",") && !value.contains("=");
  }

}
