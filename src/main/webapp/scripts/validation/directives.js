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


angular.module('universeadm.validation.directives', [])
  .directive('uadmValidate', function() {
    return {
      restrict: 'A',
      require: '^form',
      link: function(scope, el, attrs, formCtrl) {
        // find the text box element, which has the 'name' attribute
        var inputEl = el[0].querySelector("[name]");
        // convert the native text box element to an angular element
        var inputNgEl = angular.element(inputEl);
        // get the name on the text box so we know the property to check
        // on the form controller
        var inputName = inputNgEl.attr('name');

        // only apply the has-error class after the user leaves the text box
        inputNgEl.bind('blur', function() {
          el.toggleClass('has-error', formCtrl[inputName].$invalid);
          el.toggleClass('has-success', formCtrl[inputName].$valid);
        });
      }
    };
  })
  .directive('uadmPasswordMatch', function() {
    return {
      restrict: 'A',
      scope: true,
      require: 'ngModel',
      link: function(scope, elem, attrs, control) {
        var checker = function() {
          //get the value of the first password
          var e1 = scope.$eval(attrs.ngModel);

          //get the value of the other password  
          var e2 = scope.$eval(attrs.uadmPasswordMatch);
          
          return e1 === e2;
        };
        scope.$watch(checker, function(n) {

          //set the form control to valid if both 
          //passwords are the same, else invalid
          control.$setValidity("passwordMatch", n);
        });
      }
    };
  })
  .directive('uadmError', function(){
    return {
      restrict: 'AE',
      scope: {
        error: '=',
        item: '='
      },
      transclude : false,
      replace: true,
      templateUrl: 'views/directives/error.html',
      controller: function($scope){
        var error = $scope.error;
        var item = $scope.item;
        if (!item){
          item = 'object';
        }
        if (error){
          // validation error ??
          if ( error.status === 400 ) {
            if ( error.data.message ){
              $scope.message = error.data.message;
            }
            if ( error.data.violations ){
              $scope.violations = error.data.violations;
            }
          } else if (error.status === 409){
            $scope.message = 'The ' + item + ' already exists';
          } else {
            $scope.message = 'Server returned http status code ' + error.status;
          }
        }
      },
      link: function($scope, $element, $attr){
        //console.log($scope.error);
      }
    };
  });