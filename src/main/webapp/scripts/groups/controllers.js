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
  .controller('groupEditController', function($scope, groupService, userService, group){
    $scope.group = group;
    
    $scope.addMember = function(member){
      if ( member ){
        groupService.addMember(group, member.newMember).then(function(){
          if (!group.members){
            group.members = [];
          }
          group.members.push(member.newMember);
          member.newMember = null;
        });
      }
    };
    
    $scope.removeMember = function(member){
      groupService.removeMember(group, member).then(function(){
        group.members.splice(group.members.indexOf(member), 1);
      });
    };
    
    $scope.searchUsers = function(value){
      return userService.search(value, 0, 5);
    };
  });