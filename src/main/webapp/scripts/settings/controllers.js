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
        .controller('settingsController', function ($scope, $http, $log, $modal, settingsService, settings, updateService, update) {
          $scope.master = angular.copy(settings);
          $scope.user = angular.copy($scope.master);
          $scope.settings = settings;
          $scope.availableVersion = update.version;
          $scope.invalidCredentials = false;
          $scope.updateScheduled = false;
          $scope.updateInProgress = false;
          $scope.updatePrecheck = false;
          $scope.buttonDisabled = false;

          $scope.checkUpdateStatus = function () {

            $http.post('/universeadm/api/update/check', {}).success(function (data, status, headers, config) {
              if (data.status == "scheduled") {
                $scope.updateScheduled = true;
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status == "preCheck") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = true;
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status == "in progress") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = false;
                $scope.updateInProgress = true;
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status == "preCheckResult") {
                $scope.updatePrecheck = false;
                $scope.openPCResult(data.syscheck);
              }
              else if (data.status == "updateFormAvailable") {
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status == "done") {
                window.setTimeout($scope.checkUpdateStatus, 1000);
              }
              else if (data.status == "no update") {
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
          };

          $scope.checkUpdateCheckStatus = function () {
            updateService.updateCheck().then(function (e) {
              $scope.updateAvailable = e.updateAvailable;
              $scope.validCreds = e.validCreds;
            },
                    function () {
                    });
          };

          $scope.checkUpdateCheckStatus();

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
            updateService.start();
          };

          $scope.animationsEnabled = true;

          // Modals:
          $scope.openUpdateStartConfirmation = function (size) {

            var modalInstance = $modal.open({
              animation: $scope.animationsEnabled,
              templateUrl: 'views/settings/updateStartConfirmation.dialog.html',
              controller: 'updateStartConfirmationCtrl',
              size: size,
              backdrop: 'static',
              resolve: {
              }
            });

            modalInstance.result.then(function () {
              $scope.startUpdate();
              $scope.checkUpdateStatus();
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
          };

          $scope.openPCResult = function (data, size) {

            var modalInstance = $modal.open({
              animation: $scope.animationsEnabled,
              templateUrl: 'views/settings/preCheckResult.dialog.html',
              controller: 'pcResultCtrl',
              size: size,
              backdrop: 'static',
              resolve: {
                items: function () {
                  return $scope.items = data;
                }
              }
            });

            modalInstance.result.then(function () {
              window.setTimeout($scope.checkUpdateStatus, 1000);
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
          };

          $scope.openUserInput = function (size) {

            var modalInstance = $modal.open({
              animation: $scope.animationsEnabled,
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

            modalInstance.result.then(function (selectedItem) {
              $scope.selected = selectedItem;
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
          };

          $scope.toggleAnimation = function () {
            $scope.animationsEnabled = !$scope.animationsEnabled;
          };
        })

        .controller('pcResultCtrl', function ($scope, $http, $log, $timeout, $modalInstance, items) {
          $scope.items = items;

          $scope.preCheckGlobalState = function (data) {
            $scope.globalState = 'ok';
            angular.forEach(data, function (d) {
              if (d.state == 'error' || (d.state == 'warning' && $scope.globalState != 'error')) {
                $scope.globalState = d.state;
              }
            })
          };

          $scope.continueWithUpdateAfterDelay = function (seconds) {
            $scope.updateCountdown = seconds;
            if (seconds > 0) {
              $timeout(function () {
                $scope.continueWithUpdateAfterDelay($scope.updateCountdown - 1);
              }, 1000);
            } else {
              $scope.action('ok');
              $modalInstance.close();
            }

          }

          $scope.updateResult = function (data) {
            $scope.preCheckGlobalState(data);
            if ($scope.globalState == 'ok') {
              $scope.pcsuccess = true;
              $scope.pcwarning = false;
              $scope.pcerror = false;

              $scope.btnIgnoreDisabled = true;
              $scope.btnRecheckDisabled = true;
              $scope.btnCancelDisabled = true;
              $scope.continueWithUpdateAfterDelay(10);
            }
            else if ($scope.globalState == 'warning') {
              $scope.pcsuccess = false;
              $scope.pcwarning = true;
              $scope.pcerror = false;
            }
            else {
              $scope.pcsuccess = false;
              $scope.pcwarning = false;
              $scope.pcerror = true;
            }
          }

          $scope.updateResult($scope.items);
          $scope.ignore = function () {
            $scope.action('ignore');
            $modalInstance.close();
          };

          $scope.recheck = function () {
            $scope.action('recheck');
            $modalInstance.close();
          };

          $scope.cancel = function () {
            $scope.action('abort');
            $modalInstance.close();
          };
          $scope.action = function (action) {
            $http.post('/universeadm/api/update/preCheckAction', action).
                    success(function (data, status, headers, config) {
                    }).
                    error(function (data, status, headers, config) {
                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                    });

          };


        })


        .controller('userInputCtrl', function ($scope, $http, $modalInstance, $log, items) {
          $scope.inputName="test";
          $scope.objectKeys = function (obj) {
            if (!obj) {
              return [];
            }
            return Object.keys(obj);
          };

          $http.get('/universeadm/api/update/userInput', {}).
                  success(function (data, status, headers, config) {
                    $scope.inputName= $scope.objectKeys(data)[0];
                    $scope.fieldsName=$scope.objectKeys(data[$scope.inputName])[0];
                    $scope.items = data[$scope.inputName][$scope.fieldsName];
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

        })

        .controller('updateStartConfirmationCtrl', function ($scope, $http, $modalInstance, $log) {

          $scope.yes = function () {
            $modalInstance.close();
          };

          $scope.no = function () {
            $modalInstance.dismiss();
          };

        });