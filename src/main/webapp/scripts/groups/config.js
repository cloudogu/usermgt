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