/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.groups.controllers', ['ui.bootstrap', 'universeadm.validation.directives', 'universeadm.groups.services'])
  .controller('groupsController', function($scope, groups){
    $scope.groups = groups;
  });