/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.config', ['ui.router', 'universeadm.users.controllers'])
  .config(function($stateProvider){
        // configure routes
    $stateProvider
      .state('users', {
        url: '/users',
        controller: 'usersController',
        templateUrl: 'views/user/users.html',
        resolve: {
          users: function(userService){
            return userService.getAll(0, 20);
          },
          query: function(){
            return null;
          },
          page: function(){
            return 1;
          }
        }
      })
      .state('usersPage', {
        url: '/users/{page:[0-9]+}?q',
        controller: 'usersController',
        templateUrl: 'views/user/users.html',
        resolve: {
          page: function($stateParams){
            return parseInt($stateParams.page);
          },
          query: function($stateParams){
            return $stateParams.q;
          },
          users: function(userService, page, query){
            return userService.search(query, (page-1) * 20, 20);
          }
        }
      })
      .state('usersEdit', {
        url: '/users/:username',
        controller: 'userEditController',
        templateUrl: 'views/user/edit.html',
        resolve: {
          user: function(userService, $stateParams){
            var username = $stateParams.username;
            return username !== null && username.length > 0 ? userService.get(username) : null;
          }
        }
      });
  });