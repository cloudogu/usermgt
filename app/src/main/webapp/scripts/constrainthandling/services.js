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


angular.module('universeadm.constrainthandling.services', [])
  .factory('constraintHandlingService', function(){

    var setConstraintValidation = function(constraint, $scope) {
      var currentField;

      switch(constraint){
        case 'UNIQUE_EMAIL':
          currentField = $scope.form.email;
          break;
        case 'UNIQUE_USERNAME':
          currentField = $scope.form.username;
          break;
      }

      if (currentField !== undefined && currentField !== null) {
        currentField.previousUniqueValue = currentField.$viewValue;
        setTimeout(function(){
          currentField.executeValidatorCheck();
        }, 1);
      }
    };

    return {
      createErrorMessage: function(constraints) {
        var msg = '';

        for (var i = 0; i < constraints.length; i++) {
          var c = constraints[i];
          switch(c){
            case 'UNIQUE_EMAIL':
              msg += 'A user with that e-mail already exists. ';
              break;
            case 'UNIQUE_USERNAME':
              msg += 'A user with that username already exists. ';
              break;
            default:
              msg += 'An unexpected error occured. ';
              break;
          }
        }

        return msg;
      },
      setConstraintErrors: function(constraints, $scope) {
        for (var i = 0; i < constraints.length; i++) {
          var c = constraints[i];
          setConstraintValidation(c, $scope);
        }
      }
    };
  });
