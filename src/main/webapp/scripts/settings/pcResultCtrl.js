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
angular.module('universeadm.settings.controllers')
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
        });


