/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.util.services', [])
  .factory('pagingService', function() {
    
    return {
      
      pageRange: function(currentPage, maxSize, totalPages){
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
      }
      
    };
    
  });