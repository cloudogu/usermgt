/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.settings.services', ['restangular'])
  .factory('settingsService', function(Restangular){
    var settings = Restangular.one('settings');
    return {
      get: function(){
        return settings.get();
      },
      update: function(settings){
        return settings.post();
      }
    };
  });