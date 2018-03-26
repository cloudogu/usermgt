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

'use strict';

/* jasmine specs for filters go here */

describe('filter', function() {
  beforeEach(module('universeadm.util.filters'));


  describe('simpleSort', function() {
    it('should sort the string array', inject(function(simpleSortFilter){
      var array = ['c', 'a', 'd', 'b'];
      expect(simpleSortFilter(array)).toEqual(['a', 'b', 'c', 'd']);
    }));
    
    it('should sort the array of numbers', inject(function(simpleSortFilter){
      var array = [4, 2, 5, 1, 6, 3];
      expect(simpleSortFilter(array)).toEqual([1, 2, 3, 4, 5, 6]);
    }));
  });
  
  describe('byteSize', function(){
    
    it('should format the numbers', inject(function(byteSizeFilter){
      expect(byteSizeFilter(712)).toEqual('712');
      expect(byteSizeFilter(1256)).toEqual('1.2 kB');
      expect(byteSizeFilter(17632003)).toEqual('16.8 MB');
    }));
    
    it('should return invalid for non numbers', inject(function(byteSizeFilter){
      expect(byteSizeFilter('asd')).toEqual('-');
      expect(byteSizeFilter({})).toEqual('-');
    }));
    
  });
});