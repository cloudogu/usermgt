/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */


angular.module('universeadm.passwordpolicy.services', ['restangular'])
  .factory('passwordPolicyService', function(Restangular){
    return {
      applyPasswordPolicy: function ($scope){
        Restangular.one('account/passwordpolicy').withHttpConfig({ cache: true}).get().then(function(policy){
          var passwordPolicyError = 'Invalid password policy configured - please contact the administrator';
          var rules = policy.Rules;
          var violations = [];
          var invalidRules = [];
          try {
            rules.forEach(function (rule) {
              try {
                var regEx = new RegExp(rule.Rule);
                if (!regEx.test($scope.user.password)) {
                  violations.push(rule);
                }
              } catch (e) {
                rule.Description = rule.Description + ': ' + passwordPolicyError;
                invalidRules.push(rule);
              }
            });
          } catch (e){
            invalidRules.push({Description: passwordPolicyError, Rule: '', Type: ''});
          }
          if (Array.isArray(invalidRules) && invalidRules.length) {
            $scope.passwordPolicy = {status: 'invalid', violations: invalidRules, satisfactions: []};
            $scope.form.password.$setValidity('password-policy',false);
          } else{
            if (Array.isArray(violations) && violations.length) {
              var statisfactions = rules.filter(function(e) { return violations.indexOf(e) < 0 });
              $scope.passwordPolicy = {status: 'invalid', violations: violations, satisfactions: statisfactions};
              $scope.form.password.$setValidity('password-policy',false);
            } else {
              $scope.passwordPolicy = {status: 'fulfilled', violations: [], satisfactions: rules};
              $scope.form.password.$setValidity('password-policy',true);
            }
          }
        }, function(){
          var invalidRules = [];
          invalidRules.push({Description: 'Could not reach password policy endpoint - please try again later or contact the administrator', Rule: '', Type: ''});
          $scope.passwordPolicy = {status: 'invalid', violations: invalidRules, satisfactions: []};
          $scope.form.password.$setValidity('password-policy',false);
        });
      }
    };
  });
