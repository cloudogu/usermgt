/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

/**
 *
 * @author ssdorra
 * @param <T>
 */
public interface EntityEvent<T>
{

  public EventType getType();

  public T getOldEntity();
  
  public T getEntity();
}
