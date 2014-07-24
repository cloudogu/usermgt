/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import java.util.List;

/**
 *
 * @author ssdorra
 */
public final class Paginations
{
  private Paginations(){
    
  }
  
  public static <T> PagedResultList<T> createPaging(List<T> list, int start, int limit){
    PagedResultList<T> result = null;
    int size = list.size();
    if ( start < size ){
      int end = start + limit;
      if ( end > size ){
        end = size;
      }
      result = new PagedResultList<>(list.subList(start, end), start, limit, size); 
    }
    return result;
  }
}
