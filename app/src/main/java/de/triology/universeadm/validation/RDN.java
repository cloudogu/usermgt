package de.triology.universeadm.validation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Constraint(validatedBy = RDNValidator.class)
@Documented
public @interface RDN
{

  String message() default "{value} is not a valid rdn";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
