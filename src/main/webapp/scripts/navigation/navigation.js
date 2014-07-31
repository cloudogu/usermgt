/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.navigation', [])
  .provider('navigation', function(){
    
    var items = [];
    
    this.add = function(item){
      items.push(item);
    };
    
    this.$get = function(){
      return {
        items: items
      };
    };
    
  });