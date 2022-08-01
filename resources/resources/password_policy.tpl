{
  "Rules":[
    {
      "Description":"Das Passwort muss mindestens {{ .Env.Get "PWD_MIN_LENGTH" }} Zeichen enthalten",
      "Rule":".{{"{"}}{{ .Env.Get "PWD_MIN_LENGTH" }},}",
      "Type":"regex"
    }{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_capital_letter" "false") "true" }},
    {
      "Description":"Das Passwort muss mindestens 1 Großbuchstaben enthalten",
      "Rule":"[A-ZÄÖÜ]",
      "Type":"regex"
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_lower_case_letter" "false") "true" }},
    {
      "Description":"Das Passwort muss mindestens 1 Kleinbuchstaben enthalten",
      "Rule":"[a-zäöüß]",
      "Type":"regex"
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_digit" "false") "true" }},
    {
      "Description":"Das Passwort muss mindestens 1 Zahl enthalten",
      "Rule":"[0-9]",
      "Type":"regex"
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_special_character" "false") "true" }},
    {
      "Description":"Das Passwort muss mindestens 1 Sonderzeichen enthalten",
      "Rule":"[^a-zäöüßA-ZÄÖÜ0-9]",
      "Type":"regex"
    }{{ end }}
  ]
}