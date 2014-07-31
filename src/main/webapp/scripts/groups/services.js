/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('universeadm.groups.services', ['restangular'])
  .factory('groupService', function(Restangular){
    var groups = Restangular.all('groups');
    return {
      getAll: function(start, limit){
        return groups.getList({start: start, limit: limit});
      },
      search: function(query, start, limit){
        return groups.getList({query: query, start: start, limit: limit});
      },
      get: function(username){
        return groups.one(username).get();
      },
      modify: function(user){
        return user.put();
      },
      remove: function(user){
        return groups.one(user.username).remove();
      },
      create: function(user){
        return groups.post(user);
      }
    };
  });
