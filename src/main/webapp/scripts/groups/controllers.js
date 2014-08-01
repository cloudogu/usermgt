/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.groups.controllers', ['ui.bootstrap', 
  'universeadm.validation.directives', 'universeadm.groups.services',
  'universeadm.users.services', 'universeadm.util.filters'
])
  .controller('groupsController', function($scope, groups){
    $scope.groups = groups;
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