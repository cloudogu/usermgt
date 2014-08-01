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
          },
          query: function(){
            return null;
          },
          page: function(){
            return 1;
          }
        }
      })
      .state('groupsPage', {
        url: '/groups/{page:[0-9]+}?q',
        controller: 'groupsController',
        templateUrl: 'views/group/groups.html',
        resolve: {
          page: function($stateParams){
            return parseInt($stateParams.page);
          },
          query: function($stateParams){
            return $stateParams.q;
          },
          groups: function(groupService, page, query){
            return groupService.search(query, (page-1) * 20, 20);
          }
        }
      })
      .state('groupsEdit', {
        url: '/groups/:name',
        controller: 'groupEditController',
        templateUrl: 'views/group/edit.html',
        resolve: {
          group: function(groupService, $stateParams){
            var name = $stateParams.name;
            return name !== null && name.length > 0 ? groupService.get(name) : null;
          }
        }
      });
  });