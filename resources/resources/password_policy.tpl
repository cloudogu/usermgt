{
  "Rules":[
    {
      "Description":"The password must contain at least {{ .Env.Get "PWD_MIN_LENGTH" }} characters",
      "Rule":".{{"{"}}{{ .Env.Get "PWD_MIN_LENGTH" }},}",
      "Type":"regex"
    }{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_capital_letter" "false") "true" }},
    {
      "Description":"The password must contain at least 1 capital letter",
      "Rule":"[A-ZÄÖÜ]",
      "Type":"regex"
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_lower_case_letter" "false") "true" }},
    {
      "Description":"The password must contain at least 1 lower case letter",
      "Rule":"[a-zäöüß]",
      "Type":"regex"
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_digit" "false") "true" }},
    {
      "Description":"The password must contain at least 1 number",
      "Rule":"[0-9]",
      "Type":"regex"
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_special_character" "false") "true" }},
    {
      "Description":"The password must contain at least 1 special character",
      "Rule":"[^a-zäöüßA-ZÄÖÜ0-9]",
      "Type":"regex"
    }{{ end }}
  ]
}