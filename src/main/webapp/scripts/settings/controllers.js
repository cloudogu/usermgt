+/* 
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
        .controller('settingsController', function ($scope, $log, $modal,$interval, settingsService, settings, updateService, update) {
          $scope.master = angular.copy(settings);
          $scope.user = angular.copy($scope.master);
          $scope.settings = settings;
          $scope.availableVersion = update.newVersion;
          $scope.version = update.version;
          $scope.animationsEnabled = true;
          
          $scope.test={ "java": {"title": "Java Version", "expected": "1.7.0_60", "found": "1.7.0_60", "state": "ok", "message": "Found a supported version"},
"tomcat": {"title": "Apache Tomcat", "expected": "7.0.26-1ubuntu1.2", "found": "7.0.26-1ubuntu1.2", "state": "false", "message": "Found a supported version"},


"apache": {"title": "Apache Webserver", "expected": "2.2.22", "found": "2.2.22", "state": "ok", "message": "Found a supported version"},
"sonarQube": {"title": "SonarQube", "expected": "3.7.4", "found": "3.7.4", "state": "ok", "message": "Found the right version"},
"bugzilla": {"title": "Bugzilla", "expected": "4.4.6", "found": "4.4.6", "state": "ok", "message": "Found the right version"},
"SCMManager": {"title": "SCM-Manager", "expected": "1.43", "found": "1.43", "state": "ok", "message": "Found the right version"},
"wordpress":{"title": "WordPress", "expected": "3.9", "found": "3.9.6", "state": "ok", "message": "Found a supported version"},
"sonatypeNexus": {"title": "Sonatype Nexus", "expected": "2.10.0-02", "found": "2.10.0-02", "state": "ok", "message": "Found the right version"},
"MYSQL":{"title": "MySQLServer", "expected": "5.5.37", "found": "5.5.40", "state": "ok", "message": "Found a supported version"},
"jenkins": {"title": "Jenkins", "expected": "1.565.3", "found": "1.565.3", "state": "ok", "message": "Found the right version"},
"diskspace": {"title": "Harddrive (MByte)", "expected": 159, "found": 13522, "state": "ok", "message": "Enough hard drive space available"},
"Resource-Servers": {"title": "Resource-Servers", "expected": "connected", "found": "connected", "state": "ok", "message": "OK"},
"PHP": {"title": "PHP Version", "expected": "5.3.10-1ubuntu3.13", "found": "5.3.10-1ubuntu3.15", "state": "ok", "message": "Found a supported version"}};

          $scope.detectStatus= function(data){
              if (data.status == "scheduled") {
                $scope.updateScheduled = true;
              }
              else if (data.status == "preCheck") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = true;
              }
              else if (data.status == "in progress") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = false;
                $scope.updateInProgress = true;
              }
              else if (data.status == "preCheckResult") {
                $scope.updatePrecheck = false;
                $interval.cancel(intervalPromise); 
                $scope.openPCResult(data.syscheck);
              }
              else if (data.status == "updateFormAvailable") {
                $interval.cancel(intervalPromise); 
                $scope.openUserInput();
              }
              else if (data.status == "done") {
                $interval.cancel(intervalPromise); 
                $scope.checkUpdateCheckStatus();
                $scope.updateInProgress = false;
                updateService.versionCheck().then(function(data){
                  $scope.availableVersion = data.newVersion;
                  $scope.version = data.version;
                });
                if (data.result == "successful") { 
                  $scope.updateSuccessful = true;
                }
                else if (data.result == "failed") {
                  $scope.updateFailed = true;
                }
                else if (data.result == "unknown") {
                }
                else {
                  $scope.updateFailedUnexpected = true;
                }
              }
              else if (data.status == "no update") {
                $scope.noUpdate = true;
                $interval.cancel(intervalPromise); 
                $scope.checkUpdateCheckStatus();
              }
              else if (data.status == "wrong credentials") {
                $scope.wrongCreds = true;
                $interval.cancel(intervalPromise); 
                $scope.checkUpdateCheckStatus();
              }
            };

          $scope.checkUpdateStatus = function () {

            updateService.check().then(function(d){$scope.detectStatus(d);},
                    function () {
                      $log.error("Cant check update status");
                    }
            );
          }
          
          var intervalPromise =$interval(function(){$scope.checkUpdateStatus();},2000);
          $scope.$on('$destroy', function () { $interval.cancel(intervalPromise); });

          $scope.checkUpdateCheckStatus = function () {
            updateService.updateCheck().then(function (e) {
              $scope.updateAvailable = e.updateAvailable;
              $scope.validCreds = e.validCreds;
            },
                    function () {
                      $log.error("Cant check if update is possible.");
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
              var intervalPromise =$interval(function(){$scope.checkUpdateStatus();},2000);
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
              // time to recognize and delete preCheckDone
              window.setTimeout($scope.checkUpdateStatus, 10000);
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
              window.setTimeout($scope.checkUpdateStatus, 15000);
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
          };

          $scope.toggleAnimation = function () {
            $scope.animationsEnabled = !$scope.animationsEnabled;
          };
        })


        .controller('pcResultCtrl', function ($scope, $log, $timeout, $modalInstance, updateService, items) {
          $scope.items = items;


          $scope.preCheckGlobalState = function (data) {
            $scope.globalState = 'ok';
            angular.forEach(data, function (d) {
              if (d.state == 'error' || (d.state == 'warning' && $scope.globalState != 'error')) {
                $scope.globalState = d.state;
              }
            });
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
          };
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
            updateService.preCheckAction(action).then(function (e) {
            }, function () {
              $log.error("Error at preCheckAction");
            });
          };
        })


        .controller('userInputCtrl', function ($scope, $modalInstance, $log, updateService) {

          updateService.userInput().then(function (data) {
            $scope.inputName = $scope.objectKeys(data)[0];
            $scope.fieldsName = $scope.objectKeys(data[$scope.inputName])[0];
            $scope.items = data[$scope.inputName][$scope.fieldsName];
            angular.forEach($scope.items, function (value, key) {
              if (value.minLength == null) {
                value.minLength = 0;
              }
              if (value.maxLength == null) {
                value.maxLength = 1024;
              }
            });
          }, function () {
            $log.error("Cant get userInput");
          });
          $scope.input = {};

          $scope.objectKeys = function (obj) {
            if (!obj) {
              return [];
            }
            return Object.keys(obj);
          };

          $scope.ok = function () {
            $modalInstance.close();
          };

          $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
          };

          $scope.send = function (input) {
            var promise = updateService.sendUserInput(input);
            promise.then(function () {
              $modalInstance.close();
            }, function () {
            });
          };

        })


        .controller('updateStartConfirmationCtrl', function ($scope, $modalInstance) {

          $scope.yes = function () {
            $modalInstance.close();
          };

          $scope.no = function () {
            $modalInstance.dismiss();
          };

        });