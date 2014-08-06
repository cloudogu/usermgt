/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.settings.controllers', [ 
  'universeadm.validation.directives', 'universeadm.settings.services'])
  .controller('settingsController', function($scope, settingsService, settings){
    
    $scope.master = angular.copy(settings);
    $scope.settings = settings;
    
    $scope.isUnchanged = function(settings){
      return angular.equals(settings, $scope.master);
    };
    
    $scope.update = function(settings){
      settingsService.update(settings).then(function(){
        $scope.master = angular.copy(settings);
        $scope.settings = settings;
      });
    };
    
  });