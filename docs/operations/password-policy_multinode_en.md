# Password Policy

Certain rules for passwords can be defined in the global configuration of the CES. These rules must be observed in User Management when
setting passwords in User Management.

## Configuration of password rules in the global configuration

Specifically, it can be configured whether a password must contain certain characters and what the minimum length of a
password must be.

With the value `true` the respective rule can be activated for the following entries.

* `password-policy/must_contain_capital_letter` - specifies whether the password must contain at least
  one capital letter.
* `password-policy/must_contain_lower_case_letter` - specifies whether the password must contain at
  least one lowercase letter.
* `password-policy/must_contain_digit` - specifies if the password must contain at least one digit
* `password-policy/must_contain_special_character` - indicates whether the password must contain at
  least one special character

For uppercase letters this includes the umlauts `Ä`, `Ö` and `Ü`, for lowercase letters it includes the umlauts `ä`, `ö`
and `ü` and the `ß`. Special characters are all characters that are neither uppercase letters, lowercase letters nor
numbers.

The minimum length of the password can be configured via the entry `password-policy/min_length`.
A numeric integer value must be entered here. If no value is entered or a non-integer value is set, the minimum length is
1.

The values are used by the CAS after a restart.

The values can be configured via `kubectl edit configmap -n ecosystem global-config`.
