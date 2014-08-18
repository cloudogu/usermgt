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
      }, function() {

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
          }, function() {

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