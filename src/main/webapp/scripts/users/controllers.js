/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.users.controllers', ['ui.bootstrap', 'universeadm.validation.directives', 'universeadm.users.services'])
  .controller('usersController', function($scope, $location, $modal, userService, users, page, query){
    
    function pageRange(currentPage, maxSize, totalPages){
      var ret = [];
      
      var startPage = Math.max(currentPage - Math.floor(maxSize/2), 1);
      var endPage   = startPage + maxSize - 1;
      if (endPage > totalPages) {
        endPage = totalPages;
        startPage = endPage - maxSize + 1;
      }
      if (startPage <= 0){
        startPage = 1;
      }
      for (var i = startPage; i <= endPage; i++) {
        ret.push(i);
      }
      return ret;
    };
    
    function setUsers(users){
      $scope.users = users;
      $scope.pages = Math.ceil(users.meta.totalEntries / users.meta.limit);
      $scope.pageRange = pageRange(page, 10, $scope.pages);
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