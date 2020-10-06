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
      exists: function(username){
        return users.one(username).head();
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
      },
      getPasswordPolicy: function (){
        return Restangular.one('account/passwordpolicy').get();
      },
    };
  });
