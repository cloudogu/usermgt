'use strict';
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


angular.module('universeadm.users.config', ['ui.router', 'universeadm.navigation', 'universeadm.users.controllers', 'ui.router.title'])
  .config(function($stateProvider, navigationProvider){
    
    // register navigation item
    navigationProvider.add({
      url: '/users',
      label: 'Users',
      requireAdminPrivileges: true
    });
    
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
          },
          $title: function() { return 'Users | User Management'; }
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
          },
          $title: function() { return 'Users | User Management'; }
        }
      })
      .state('usersEdit', {
        url: '/user/:username',
        controller: 'userEditController',
        templateUrl: 'views/user/edit.html',
        resolve: {
          user: function(userService, $stateParams){
            var username = $stateParams.username;
            return username !== null && username.length > 0 ? userService.get(username) : null;
          },
          $title: function($stateParams) {
            var username = $stateParams.username;
            var title;
            if (username !== null && username.length > 0) {
              title = 'Edit ' + username + ' | User Management';
            } else {
              title = 'Create User | User Management';
            }
            return title;
          }
        }
      });
  });
