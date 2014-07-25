/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.account.controllers', ['universeadm.validation.directives', 'universeadm.account.services'])
  .controller('accountController', function($scope, accountService, account){
    
    function setAccount(account){
      $scope.user = account;
      $scope.master = angular.copy(account);
      $scope.confirmPassword = account.password;
    }
    
    setAccount(account);
    
    $scope.isUnchanged = function(account){
      return angular.equals(account, $scope.master);
    };
    
    $scope.save = function(account){
      accountService.modify(account).then(function(){
        setAccount(account);
        $scope.form.$setPristine();
      }, function(e){
        
      });
    };
  });