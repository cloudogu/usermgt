/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.account.controllers', ['universeadm.validation.directives', 
  'universeadm.account.services', 'universeadm.groups.services'])
  .controller('accountController', function($scope, accountService, groupService, account) {

    function setAccount(account) {
      $scope.user = account;
      $scope.master = angular.copy(account);
      $scope.confirmPassword = account.password;
    }

    setAccount(account);

    $scope.isUnchanged = function(account) {
      return angular.equals(account, $scope.master);
    };

    $scope.save = function(account) {
      accountService.modify(account).then(function() {
        setAccount(account);
        $scope.form.$setPristine();
      }, function(e) {

      });
    };
    
    $scope.searchGroups = function(value){
      return groupService.search(value, 0, 5);
    };

    $scope.addGroup = function(group) {
      if (group) {
        if (account.memberOf.indexOf(group.newGroup) < 0) {
          account.memberOf.push(group.newGroup);
          accountService.modify(account).then(function() {
            group.newGroup = null;
          }, function(e) {

          });
        } else {
          group.newGroup = null;
        }
      }
    };

    $scope.removeGroup = function(group) {
      account.memberOf.splice(account.memberOf.indexOf(group), 1);
      accountService.modify(account);
    };
  });