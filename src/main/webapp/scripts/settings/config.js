/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('universeadm.settings.config', ['ui.router',
  'universeadm.navigation',  'universeadm.settings.services', 
  'universeadm.settings.controllers'])
  .config(function($stateProvider, navigationProvider){
    // registar navigation
    navigationProvider.add({
      url: '/settings',
      label: 'Settings',
      requireAdminPrivileges: true
    });
    
    // configure routes
    $stateProvider
      .state('settings', {
        url: '/settings',
        templateUrl: 'views/settings/edit.html',
        controller: 'settingsController',
        resolve: {
          settings: function(settingsService){
            return settingsService.get();
          }
        }
      });
  });