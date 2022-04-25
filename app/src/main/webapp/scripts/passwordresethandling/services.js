angular.module('universeadm.passwordresethandling.services', ['restangular'])
    .factory('passwordResetHandlingService', function(Restangular){
        return {
            getPasswordResetDefaultValue: function (){
               // return Restangular.one('account/gui_config').withHttpConfig({ cache: true}).get();
                return true;
            }
        };
    });