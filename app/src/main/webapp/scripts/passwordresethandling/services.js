'use strict';

angular.module('universeadm.passwordresethandling.services', ['restangular'])
    .factory('passwordResetHandlingService', function(Restangular){
        return {
            getPasswordResetDefaultValue: function (){
                return new Promise(function (resolve) {
                    Restangular.one('account/gui_config').withHttpConfig({ cache: true}).get().then(function (result) {
                        resolve(true);
                        // If result is undefined because of a failure in backend, make sure not to throw an error here
                        if (!!result){
                            resolve(result.pwdResetPreselected);
                        } else {
                            resolve(false);
                        }
                    });
                });
            }
        };
    });