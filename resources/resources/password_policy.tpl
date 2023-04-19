{
  "Rules":[
    {
      "Rule":".{{"{"}}{{ .Env.Get "PWD_MIN_LENGTH" }},}",
      "Type":"regex",
      "Name": "length",
      "Variables": [
        {
          "Name": "length",
          "Value": "{{ .Env.Get "PWD_MIN_LENGTH" }}"
        }
      ]
    }{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_capital_letter" "false") "true" }},
    {
      "Rule":"[A-ZÄÖÜ]",
      "Type":"regex",
      "Name": "capital",
      "Variables": []
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_lower_case_letter" "false") "true" }},
    {
      "Rule":"[a-zäöüß]",
      "Type":"regex",
      "Name": "lowercase",
      "Variables": []
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_digit" "false") "true" }},
    {
      "Rule":"[0-9]",
      "Type":"regex",
      "Name": "numeric",
      "Variables": []
    }{{ end }}{{ if eq (.GlobalConfig.GetOrDefault "password-policy/must_contain_special_character" "false") "true" }},
    {
      "Rule":"[^a-zäöüßA-ZÄÖÜ0-9]",
      "Type":"regex",
      "Name": "special",
      "Variables": []
    }{{ end }}
  ]
}
