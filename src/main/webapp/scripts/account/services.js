/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.account.services', ['restangular'])
  .factory('accountService', function(Restangular){
    return {
      get: function(){
        return Restangular.one('account').get();
      },
      modify: function(account){
        return account.put();
      }
    };
  });
