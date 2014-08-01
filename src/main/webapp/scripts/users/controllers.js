/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.users.controllers', ['ui.bootstrap', 'universeadm.validation.directives', 'universeadm.users.services', 'universeadm.util.services'])
  .controller('usersController', function($scope, $location, $modal, userService, pagingService, users, page, query){
    
    function setUsers(users){
      if (!users.meta){
        users.meta = {totalEntries: 0, limit: 10};
      }
      $scope.users = users;
      $scope.pages = Math.ceil(users.meta.totalEntries / users.meta.limit);
      $scope.pageRange = pagingService.pageRange(page, 10, $scope.pages);
    };
    
    $scope.search = function(query){
      $location.search({q: query});
      $location.path('/users/1');
    };
    
    $scope.remove = function(user){      
      var removeScope = $scope.$new();
      removeScope.user = user;
      
      var instance = $modal.open({
        templateUrl: 'views/user/remove.dialog.html',
        scope: removeScope
      });
      
      removeScope.remove = function(user){
        userService.remove(user).then(function(){
          return userService.search(query, users.meta.start, users.meta.limit);
        }).then(function(users){
          setUsers(users);
          instance.close();
        });        
      };
      
      removeScope.cancel = function(){
        instance.close();
      };

    };
    
    $scope.page = page;
    $scope.query = query;
    $scope.nonSubmittedQuery = query;
    setUsers(users);
  })
  .controller('userEditController', function($scope, $location, $modal, userService, user){
    $scope.alerts = [];
    $scope.backEnabled = true;
    $scope.removeEnabled = true;
    
    $scope.create = false;
    if (user === null){
      $scope.create = true;
      user = {};
    } else {
      $scope.master = angular.copy(user);
      $scope.confirmPassword = user.password;
    }
    $scope.user = user;
    
    $scope.isUnchanged = function(user){
      return angular.equals(user, $scope.master);
    };
    
    $scope.remove = function(user){
      var removeScope = $scope.$new();
      removeScope.user = user;
      var instance = $modal.open({
        templateUrl: 'views/user/remove.dialog.html',
        scope: removeScope
      });
      removeScope.remove = function(user){              
        userService.remove(user).then(function(){
          instance.close();
          $location.path('/users');
        });
      };
      removeScope.cancel = function(){
        instance.close();
      };
    };
    
    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };
    
    $scope.save = function(user){
      var promise;
      if ($scope.create){
        promise = userService.create(user);
      } else {
        promise = userService.modify(user);
      }
      promise.then(function(){
        $location.path('/users');
      }, function(error){
        if ( error.status === 409 ){
          $scope.alerts = [{
            type: 'danger',
            msg: 'The user ' + user.username + ' already exists'
          }];
        } else {
          $scope.alerts = [{
            type: 'danger',
            msg: 'The user could not be saved'
          }];
        }
      });
    };
  });