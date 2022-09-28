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


angular.module('universeadm.groups.services', ['restangular'])
    .factory('groupService', function (Restangular) {
        var groups = Restangular.all('groups');
        var alerts = [];
        return {
            getAll: function (start, limit) {
                return groups.getList({start: start, limit: limit});
            },
            getUndeletableGroups: function () {
                return groups.one('undeletable').get();
            },
            search: function (query, start, limit) {
                return groups.getList({query: query, start: start, limit: limit});
            },
            exists: function (name) {
                return groups.one(name).head();
            },
            get: function (name) {
                return groups.one(name).get();
            },
            modify: function (group) {
                return group.put();
            },
            remove: function (group) {
                return groups.one(group.name).remove();
            },
            create: function (group) {
                return groups.post(group);
            },
            addMember: function (group, member) {
                return group.one('members/' + member).post();
            },
            removeMember: function (group, member) {
                return group.one('members/' + member).remove();
            },
            addGroupAlert: function (type, message) {
                alerts.push({type: type, msg: message});
            },
            getGroupAlert: function () {
                return alerts.pop();
            }
        };
    });
