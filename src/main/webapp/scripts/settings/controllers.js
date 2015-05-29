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


angular.module('universeadm.settings.controllers', [
  'universeadm.validation.directives', 'universeadm.settings.services'])
        .controller('settingsController', function ($scope, $http, settingsService, settings) {
          $scope.master = angular.copy(settings);
          $scope.settings = settings;
          $scope.invalidCredentials = false;
          $http.get('/universeadm/api/update/versionCheck', {}).
                  success(function (data, status, headers, config) {
                    $scope.availableVersion = data.version;

                  }).
                  error(function (data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                  });
          $scope.updateScheduled = false;
          $scope.updateInProgress = false;
          $scope.updatePrecheck = false;
          $scope.buttonDisabled = true;




          $scope.checkUpdateStatus = function () {

            $http.post('/universeadm/api/update/check', {}).success(function (data, status, headers, config) {
              if (data.status == "scheduled") {
                $scope.updateScheduled = true;
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status = "precheck") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = true;
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status = "in progress") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = false;
                $scope.updateInProgress = true;
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status = "preCheckResult") {
                $scope.updatePrecheck = false;

                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status = "updateFormAvailable") {
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status = "done") {
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status = "no update") {
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else {
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }

            }).
                    error(function (data, status, headers, config) {
                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                    });
          }


          $scope.isUnchanged = function (settings) {
            return angular.equals(settings, $scope.master);
          };

          $scope.update = function (settings) {
            settingsService.update(settings).then(function () {
              $scope.master = angular.copy(settings);
              $scope.settings = settings;
              $scope.invalidCredentials = false;
            }, function (e) {
              if (e.status === 400) {
                $scope.invalidCredentials = true;
              }
            });
          };

          $scope.startUpdate = function () {
            $http.post('/universeadm/api/update/start', {}).
                    success(function (data, status, headers, config) {

                    }).
                    error(function (data, status, headers, config) {
                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                    });
          };

        });

angular.module('universeadm.settings.controllers')
        .controller('ModalCtrl', function ($scope, $modal, $log) {

          $scope.animationsEnabled = true;

          $scope.openPCResult = function (size) {

            var modalInstance = $modal.open({
              animation: $scope.animationsEnabled,
              templateUrl: 'views/settings/preCheckResult.dialog.html',
              controller: 'PCResultCtrl',
              size: size,
              resolve: {
                items: function () {
                  return $scope.items;
                }
              }
            });



            modalInstance.result.then(function (selectedItem) {
              $scope.selected = selectedItem;
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
          };

          $scope.openUserInput = function (size) {

            var modalInstance = $modal.open({
              animation: $scope.animationsEnabled,
              templateUrl: 'views/settings/userInput.dialog.html',
              controller: 'UserInputCtrl',
              size: size,
              resolve: {
                items: function () {
                  return $scope.items;
                }
              }
            });

            modalInstance.result.then(function (selectedItem) {
              $scope.selected = selectedItem;
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
          };

          $scope.toggleAnimation = function () {
            $scope.animationsEnabled = !$scope.animationsEnabled;
          };

        });

angular.module('universeadm.settings.controllers')
        .controller('PCResultCtrl', function ($scope, $http, $modalInstance, items) {

          $http.get('/universeadm/api/update/preCheckResult', {}).
                  success(function (data, status, headers, config) {
                    $scope.items = data;
                  }).
                  error(function (data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                  });
          $scope.ok = function () {
            $modalInstance.close();
          };

          $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
          };

        });

angular.module('universeadm.settings.controllers')
        .controller('UserInputCtrl', function ($scope, $http, $modalInstance, $log, items) {

          $scope.objectKeys = function (obj) {
            return Object.keys(obj);
          }

          $http.get('/universeadm/api/update/userInput', {}).
                  success(function (data, status, headers, config) {
                    $scope.items = data;
                  }).
                  error(function (data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                  });
          $scope.ok = function () {
            $modalInstance.close();
          };

          $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
          };

        });