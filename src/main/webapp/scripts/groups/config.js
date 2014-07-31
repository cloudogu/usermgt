/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('universeadm.groups.config', ['ui.router',
  'universeadm.navigation', 'universeadm.groups.controllers'])
  .config(function($stateProvider, navigationProvider){
    // registar navigation
    navigationProvider.add({
      url: '/groups',
      label: 'Groups',
      requireAdminPrivileges: true
    });
        // configure routes
    $stateProvider
      .state('groups', {
        url: '/groups',
        controller: 'groupsController',
        templateUrl: 'views/group/groups.html',
        resolve: {
          groups: function(groupService){
            return groupService.getAll(0, 20);
          }
        }
      });
  });