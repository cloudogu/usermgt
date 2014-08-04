/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('universeadm.users.services', ['restangular'])
  .factory('userService', function(Restangular){
    var users = Restangular.all('users');
    return {
      getAll: function(start, limit){
        return users.getList({start: start, limit: limit});
      },
      search: function(query, start, limit){
        return users.getList({query: query, start: start, limit: limit});
      },
      get: function(username){
        return users.one(username).get();
      },
      modify: function(user){
        return user.put();
      },
      remove: function(user){
        return users.one(user.username).remove();
      },
      create: function(user){
        return users.post(user);
      },
      addGroup: function(user, group){
        return user.one('groups/' + group).post();
      },
      removeGroup: function(user, group){
        return user.one('groups/' + group).remove();
      }
    };
  });
