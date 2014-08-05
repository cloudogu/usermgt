/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ssdorra
 */
public final class Caches
{

  private static final long MAX_TIME_SMALL = 1l;
  
  private static final long MAX_SIZE_SMALL = 5l;
  
  private static final long MAX_SIZE_DISABLED = 0l;
  
  private Caches()
  {
  }
  
  public static <K,V> Cache<K, V> createSmallCache()
  {
    return CacheBuilder.newBuilder()
                       .maximumSize(MAX_SIZE_SMALL)
                       .expireAfterWrite(MAX_TIME_SMALL, TimeUnit.HOURS)
                       .build();
  }
  
  public static <K,V> Cache<K, V> createDisabledCache()
  {
    return CacheBuilder.newBuilder().maximumSize(MAX_SIZE_DISABLED).build();
  }
  
}
