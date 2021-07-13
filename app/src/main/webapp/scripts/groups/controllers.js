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


angular.module('universeadm.groups.controllers', ['ui.bootstrap', 
  'universeadm.validation.directives', 'universeadm.groups.services',
  'universeadm.users.services', 'universeadm.util.filters', 'universeadm.util.services'
])
  .controller('groupsController', function($scope, $location, $modal, groupService, pagingService, groups, page, query){
    
    function setGroups(groups){
      if (!groups.meta){
        groups.meta = {totalEntries: 0, limit: 10};
      }
      $scope.groups = groups;
      $scope.pages = Math.ceil(groups.meta.totalEntries / groups.meta.limit);
      $scope.pageRange = pagingService.pageRange(page, 10, $scope.pages);
    }

    $scope.editGroup = function(group){
      var redirectURL='#/group/'+group;
      window.location.replace(redirectURL);
    };

    $scope.search = function(query){
      $location.search({q: query});
      $location.path('/groups/1');
    };
    
    $scope.remove = function(group){      
      var removeScope = $scope.$new();
      removeScope.group = group;
      
      var instance = $modal.open({
        templateUrl: 'views/group/remove.dialog.html',
        scope: removeScope
      });
      
      removeScope.remove = function(group){
        groupService.remove(group).then(function(){
          return groupService.search(query, groups.meta.start, groups.meta.limit);
        }).then(function(groups){
          setGroups(groups);
          instance.close();
        });
      };
      
      removeScope.cancel = function(){
        instance.close();
      };

    };

    $scope.page = page;
    $scope.query = query;
    $scope.nonSubmittedQuery = query;
    setGroups(groups);
  })
  .controller('groupEditController', function($scope, $location, $modal, groupService, userService, group){
    $scope.alerts = [];
    $scope.backEnabled = true;
    $scope.removeEnabled = true;
    
    $scope.addMember = function(member){
      if ( member ){
        var promise = null;
        if ($scope.create){
          promise = userService.exists(member.newMember);
        } else {
          promise = groupService.addMember(group, member.newMember);
        }
        promise.then(function(){
          group.members.push(member.newMember);
          member.newMember = null;
        }, function(e){
          // ?? do not clear, mark as dirty ?
          if (e.status === 400 || e.status === 404){
            $scope.alerts = [{
              type: 'danger',
              msg: 'user ' + member.newMember + ' does not exists'
            }];
          } else if (e.status === 409) {
            $scope.alerts = [{
              type: 'info',
              msg: 'The user ' + member.newMember + ' is allready a member'
            }];              
          } else {
            $scope.alerts = [{
              type: 'danger',
              msg: 'The member could not be added'
            }];
          }
          member.newMember = null;
        });
      }
    };
    
    $scope.remove = function(group){
      var removeScope = $scope.$new();
      removeScope.group = group;
      
      var instance = $modal.open({
        templateUrl: 'views/group/remove.dialog.html',
        scope: removeScope
      });
      
      removeScope.remove = function(group){
        groupService.remove(group).then(function(){
          instance.close();
          $location.path('/groups');
        });
      };
      
      removeScope.cancel = function(){
        instance.close();
      };
    };
    
    $scope.removeMember = function(member){
      if ($scope.create){
        group.members.splice(group.members.indexOf(member), 1);
      } else {
        groupService.removeMember(group, member).then(function(){
          group.members.splice(group.members.indexOf(member), 1);
        });
      }
    };

    $scope.editMember = function(member){
      var redirectURL='#/user/'+member;
      console.log(redirectURL);
      window.location.replace(redirectURL);
    };
    
    $scope.searchUsers = function(value){
      return userService.search(value, 0, 5);
    };
    
    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };
    
    $scope.isUnchanged = function(group){
      return angular.equals(group, $scope.master);
    };
    
    $scope.save = function(group){
      var promise;
      if ($scope.create){
        promise = groupService.create(group);
      } else {
        promise = groupService.modify(group);
      }
      promise.then(function(){
        $location.path('/groups');
      }, function(error){
        if ( error.status === 409 ){
          $scope.alerts = [{
            type: 'danger',
            msg: 'The group ' + group.name + ' already exists'
          }];
        } else {
          $scope.alerts = [{
            type: 'danger',
            msg: 'The group could not be saved'
          }];
        }
      });
    };
    
    $scope.create = false;
    if (group === null){
      $scope.create = true;
      group = {};
      group.members = [];
    }
    $scope.master = angular.copy(group);
    $scope.group = group;
  });