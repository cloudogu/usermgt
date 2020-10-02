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


angular.module('universeadm.users.controllers', ['ui.bootstrap', 
  'universeadm.validation.directives', 'universeadm.users.services', 
  'universeadm.util.services', 'universeadm.groups.services'])
  .controller('usersController', function($scope, $location, $modal, userService, pagingService, users, page, query, config){

    function setUsers(users){
      if (!users.meta){
        users.meta = {totalEntries: 0, limit: 10};
      }
      $scope.users = users;
      $scope.pages = Math.ceil(users.meta.totalEntries / users.meta.limit);
      $scope.pageRange = pagingService.pageRange(page, 10, $scope.pages);
    }
    
    $scope.isSelf = function(user){
      return $scope.subject.principal === user.username;
    };
    
    $scope.search = function(query){
      $location.search({q: query});
      $location.path('/users/1');
    };
    
    $scope.remove = function(user){      
      var removeScope = $scope.$new();
      removeScope.user = user;
      
      var instance = $modal.open({
        templateUrl: 'views/user/remove.dialog.html',
        scope: removeScope
      });
      
      removeScope.remove = function(user){
        userService.remove(user).then(function(){
          return userService.search(query, users.meta.start, users.meta.limit);
        }).then(function(users){
          setUsers(users);
          instance.close();
        });        
      };
      
      removeScope.cancel = function(){
        instance.close();
      };

    };
    
    $scope.page = page;
    $scope.query = query;
    //$scope.config = config;
    $scope.nonSubmittedQuery = query;
    setUsers(users);
  })
  .controller('userEditController', function($scope, $location, $modal, groupService, userService, user){
    $scope.alerts = [];
    $scope.backEnabled = true;
    $scope.removeEnabled = true;

    
    $scope.create = false;
    if (user === null){
      $scope.create = true;
      user = {
        memberOf: []
      };
    } else {
      $scope.master = angular.copy(user);
      $scope.confirmPassword = user.password;
    }
    $scope.user = user;
    
    $scope.containsIllegalChar = function(username){
      return /^[a-zA-Z0-9-_@\.]+$/.test(username);
    };
    
    $scope.isUnchanged = function(user){
      return angular.equals(user, $scope.master);
    };
    
    $scope.isSelf = function(user){
      return $scope.subject.principal === user.username;
    };


    $scope.applyPasswordPolicy = function(){
      console.log($scope.config);
      var rules = [{Description: "Should start with Capital Letter", Rule: "^[A-Z].*"},
        {Description: "Should contain at least 6 characters", Rule: ".*(.*[a-z]){6}.*"},
        {Description: "Should contain at least one digit", Rule: ".*[0-9].*", Type: "regex"}];
      var violations = [];
      var configError = false;
      rules.forEach(function(rule){
        try{
          var regEx = new RegExp(rule.Regex);
          if (!regEx.test($scope.user.password)){
            violations.push(rule.Description);
          }
        } catch (e) {
          configError = true;
        }
      });

      if (configError){
        $scope.user.passwordPolicy = {status: "invalid", msg: "Password-Policy misconfigured"};
      }else{
      if (Array.isArray(violations) && violations.length){
        $scope.user.passwordPolicy = {status: "invalid", msg: violations.join('; ')};
      }else{
        $scope.user.passwordPolicy = {status: "fulfilled", msg: ''};
      }
    }};


    $scope.addGroup = function(group){
      if ( group ){
        var promise = null;
        if ($scope.create){
          promise = groupService.exists(group.newGroup);
        } else {
          promise = userService.addGroup(user, group.newGroup);
        }
        promise.then(function(){
          user.memberOf.push(group.newGroup);
          group.newGroup = null;
        }, function(e){
          // ?? do not clear, mark as dirty ?
          if (e.status === 400 || e.status === 404){
            $scope.alerts = [{
              type: 'danger',
              msg: 'group ' + group.newGroup + ' does not exists'
            }];
          } else if (e.status === 409) {
            $scope.alerts = [{
              type: 'info',
              msg: 'The user is allready a member of ' + group.newGroup
            }];  
          } else {
            $scope.alerts = [{
              type: 'danger',
              msg: 'The group could not be added'
            }];
          }
          group.newGroup = null;
        });
      }
    };
    
    $scope.removeGroup = function(group){
      if ($scope.create){
        user.memberOf.splice(user.memberOf.indexOf(group), 1);
      } else {
        userService.removeGroup(user, group).then(function(){
          user.memberOf.splice(user.memberOf.indexOf(group), 1);
        });
      }
    };
    
    $scope.searchGroups = function(value){
      return groupService.search(value, 0, 5);
    };
    
    $scope.remove = function(user){
      var removeScope = $scope.$new();
      removeScope.user = user;
      var instance = $modal.open({
        templateUrl: 'views/user/remove.dialog.html',
        scope: removeScope
      });
      removeScope.remove = function(user){              
        userService.remove(user).then(function(){
          instance.close();
          $location.path('/users');
        });
      };
      removeScope.cancel = function(){
        instance.close();
      };
    };
    
    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };
    
    $scope.save = function(user){
      var promise;
      if ($scope.create){
        promise = userService.create(user);
      } else {
        promise = userService.modify(user);
      }
      promise.then(function(){
        $location.path('/users');
      }, function(error){
        if ( error.status === 409 ){
          $scope.alerts = [{
            type: 'danger',
            msg: 'The user ' + user.username + ' already exists'
          }];
        } else {
          $scope.alerts = [{
            type: 'danger',
            msg: 'The user could not be saved'
          }];
        }
      });
    };
  });
