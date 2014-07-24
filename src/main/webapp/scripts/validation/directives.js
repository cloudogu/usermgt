/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
          var e2 = scope.$eval(attrs.scmmuPasswordMatch);
          
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