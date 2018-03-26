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


angular.module('universeadm', ['angular-loading-bar', 'ngAnimate', 'restangular',
  'ui.router', 'universeadm.navigation', 'universeadm.account.config', 
  'universeadm.users.config', 'universeadm.groups.config'])
  .config(function(RestangularProvider, $stateProvider, $urlRouterProvider, $logProvider){
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
      });

    // redirect start page to account
    $urlRouterProvider.when('', '/account');
    $urlRouterProvider.when('/', '/account');

    // diplay error 404 for unmatched routes
    $urlRouterProvider.otherwise(function($injector){
      $injector.get('$state').go('error404');
    });
    
    // configure logging
    $logProvider.debugEnabled(false);
  })
  .run(function($rootScope, $state, $log, $http){
    $http.get(_contextPath + '/api/subject').then(function(res){
      $log.info('subject for principal ' + res.data.principal + ' logged in');
      $rootScope.subject = res.data;
      $rootScope.$broadcast('universeadmSubjectReceived', res.data);
    }, function(e){
      $log.error(e);
    });
    
    $rootScope.$on('$stateChangeError', function(event, toState, toParams, fromState, fromParams, error){
      if ( error.status === 404 ){
        event.preventDefault();
        $log.warn('could not find page, redirect to error page');
        $state.go('error404');
      } else if (error.status === 302 || error.status === 0){
        location.href = _contextPath;
      } else {
        $log.warn('http error ' + error.status);
        $state.go('error500');
      }
    });

    $rootScope.$on('$stateNotFound', function(){
      $log.debug('$stateNotFound, go to 404 page');
      $state.go('error404');
    });
  })
  .controller('navigationController', function($scope, $location, $log, navigation){    
    function setNavigation(subject){
      $scope.navItems = _.filter(navigation.items, function(item){
        return ! item.requireAdminPrivileges || subject.admin;
      });
    }
    
    var subject = $scope.subject;
    if (!subject){
      $scope.$on('universeadmSubjectReceived', function(event, subject){
        setNavigation(subject);
      });
    } else {
      setNavigation(subject);
    }

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
