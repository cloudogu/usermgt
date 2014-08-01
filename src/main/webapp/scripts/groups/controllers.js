/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
  .controller('groupEditController', function($scope, $location, groupService, userService, group){
    $scope.alerts = [];
    $scope.backEnabled = true;
    $scope.removeEnabled = true;
    
    $scope.addMember = function(member){
      if ( member ){
        if (group.members.indexOf(member.newMemeber) < 0){
          if ($scope.create){
            group.members.push(member.newMember);
            member.newMember = null;
          } else {
            groupService.addMember(group, member.newMember).then(function(){
              group.members.push(member.newMember);
              member.newMember = null;
            });
          }
        } else {
          member.newMember = null;
        }
      }
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
    
    $scope.searchUsers = function(value){
      return userService.search(value, 0, 5);
    };
    
    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };
    
    $scope.save = function(user){
      var promise;
      if ($scope.create){
        promise = groupService.create(user);
      } else {
        promise = groupService.modify(user);
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
    $scope.group = group;
  });