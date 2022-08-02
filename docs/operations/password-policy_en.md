# Passwort Policy

Certain rules for passwords can be defined in the etcd of the CES. These rules must be observed in User Management when
set passwords in the User Management.

## Configuration of password rules in etcd

Specifically, it can be configured whether a password must contain certain characters and what the minimum length of a
password must be.

With the value `true` the respective rule can be activated for the following entries.

* `/config/_global/password-policy/must_contain_capital_letter` - specifies whether the password must contain at least
  one capital letter.
* `/config/_global/password-policy/must_contain_lower_case_letter` - specifies whether the password must contain at
  least one lowercase letter.
* `/config/_global/password-policy/must_contain_digit` - specifies if the password must contain at least one digit
* `/config/_global/password-policy/must_contain_special_character` - indicates whether the password must contain at
  least one

For uppercase letters this includes the umlauts `Ä`, `Ö` and `Ü`, for lowercase letters it includes the umlauts `ä`, `ö`
and `ü` and the `ß`. Special characters are all characters that are neither uppercase letters, lowercase letters nor
numbers.

The minimum length of the password can be configured via the entry `/config/_global/password-policy/min_length`. A
numeric integer value must be entered here. If no value is entered or a non-integer value is set, the minimum length is
1 .

The values are used by the CAS after a restart.

It should be noted that these values cannot be configured via `cesapp edit-config usermgt`, as they are global values.
These values are valid for the entire CES and are therefore not Dogu-specific.