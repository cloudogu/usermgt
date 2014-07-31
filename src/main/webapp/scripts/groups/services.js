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
      get: function(name){
        return groups.one(name).get();
      },
      modify: function(group){
        return group.put();
      },
      remove: function(group){
        return groups.one(group.name).remove();
      },
      create: function(group){
        return groups.post(group);
      }
    };
  });
