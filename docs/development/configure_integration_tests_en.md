# Configuration for integration tests

The integration tests expect a certain configuration to run successfully. Specifically certain values be set in the
etcd. These are as follows:

```
etcdctl set /config/_global/password-policy/must_contain_capital_letter true
etcdctl set /config/_global/password-policy/must_contain_lower_case_letter true
etcdctl set /config/_global/password-policy/must_contain_digit true
etcdctl set /config/_global/password-policy/must_contain_special_character true
etcdctl set /config/_global/password-policy/min_length 14
```

In order for the set values to be taken into account, the Dogu must be restarted once.

The values configure the password rules that are checked in the integration tests.