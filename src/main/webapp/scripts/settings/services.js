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


angular.module('universeadm.settings.services', ['restangular'])
        .factory('settingsService', function (Restangular) {
          var settings = Restangular.one('settings');
          return {
            get: function () {
              return settings.get();
            },
            update: function (settings) {
              return settings.post();
            }
          };
        })
        .factory('updateService', function (Restangular) {
          var update = Restangular.one('update');
          return {
            versionCheck: function () {
              return update.one('versionCheck').get();
            },
            updateCheck: function () {
              return update.post('updateCheck');
            },
            check: function () {
              return update.post('check');
            },
            start: function () {
              return update.post('start');
            },
            sendUserInput: function (input) {
              return update.post('sendUserInput', input);
            },
            preCheckAction: function (input) {
              return update.post('preCheckAction', input);
            },
            userInput: function () {
              return update.one('userInput').get();
            },
            preCheckResult: function () {
              return update.one('preCheckResult').get();
            }

          };
        })
        .factory('modalService', function ($modal, $log) {
          return{
            pcResult: function (data, size) {
              var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'views/settings/preCheckResult.dialog.html',
                controller: 'pcResultCtrl',
                size: size,
                backdrop: 'static',
                resolve: {
                  items: function () {
                    return data;
                  }
                }
              });
              modalInstance.result.then(function () {
                return 0;
              }, function () {
                return 1;
                $log.info('Modal dismissed at: ' + new Date());
              });
           
            },
          userInput: function (size) {
            var modalInstance = $modal.open({
              animation: true,
              templateUrl: 'views/settings/userInput.dialog.html',
              controller: 'userInputCtrl',
              size: size,
              backdrop: 'static',
              resolve: {
                items: function () {
                  return $scope.items;
                }
              }            
            });
            modalInstance.result.then(function () {
              return 0;
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
              return 1;
            });             
           }            
          }
        });