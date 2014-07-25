/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.template;

/**
 *
 * @author ssdorra
 */
public interface MapperFactory
{
  
  public <T> Mapper<T> createMapper(Class<T> type, String parentDN);
  
}
