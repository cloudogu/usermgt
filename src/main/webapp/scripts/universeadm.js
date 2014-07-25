/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm', ['angular-loading-bar', 'ngAnimate', 
  'restangular', 'ui.router', 'universeadm.account.config', 
  'universeadm.users.config'])
  .config(function(RestangularProvider, $stateProvider, $urlRouterProvider){
    // configure restangular
    RestangularProvider.setBaseUrl(_contextPath + '/api');
    RestangularProvider.addResponseInterceptor(function(response, operation) {
      if (operation === 'getList') {
        if ( response.entries ){
          var resp = response.entries;
          resp.meta = {
            start: response.start,
            limit: response.limit,
            totalEntries: response.totalEntries
          };
          return resp;
        }
      }
      return response;
    });

    $stateProvider
      .state('error404', {
        url: '/error/404',
        templateUrl: 'views/error/404.html'
      })
      .state('error500', {
        url: '/error/500',
        templateUrl: 'views/error/500.html'
      });;

    // redirect unmatched to /users
    $urlRouterProvider.otherwise("/account");
  })
  .run(function($rootScope, $state, $log, $http){
    $http.get(_contextPath + '/api/subject').then(function(res){
      $log.info('subject for principal ' + res.data.principal + ' logged in');
      $rootScope.subject = res.data;
    }, function(e){
      $log.error(e);
    });
    
    $rootScope.$on("$stateChangeError", function(event, toState, toParams, fromState, fromParams, error){
      if ( error.status === 404 ){
        event.preventDefault();
        $log.warn('could not find page, redirect to error page');
        $state.go('error404');
      } else {
        $log.warn('http error ' + error.status);
        $state.go('error500');
      }
    });

    $rootScope.$on("$stateNotFound", function(event){
      $state.go('error404');
    });
  })
  .controller('navigationController', function($scope, $location){
    $scope.navCollapsed = true;

    $scope.toggleNav = function(){
      $scope.navCollapsed = !$scope.navCollapsed;
    };

    $scope.$on('$routeChangeStart', function() {
      $scope.navCollapsed = true;
    });
    
    $scope.navClass = function(page) {
      var currentRoute = $location.path();
      return page === currentRoute || new RegExp(page).test(currentRoute) ? 'active' : '';
    };

  });
