/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.groups.controllers', ['ui.bootstrap', 
  'universeadm.validation.directives', 'universeadm.groups.services',
  'universeadm.users.services'
])
  .controller('groupsController', function($scope, groups){
    $scope.groups = groups;
  })
  .controller('groupEditController', function($scope, userService, group){
    $scope.group = group;
    
    $scope.searchUsers = function(value){
      return userService.search(value, 0, 5);
    };
  });