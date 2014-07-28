/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author ssdorra
 */
@Singleton
public class LDAPConnectionStrategyBindFilter implements Filter
{
  
  private final LDAPConnectionStrategy strategy;

  @Inject
  public LDAPConnectionStrategyBindFilter(LDAPConnectionStrategy strategy)
  {
    this.strategy = strategy;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    strategy.bind();
    try {
      chain.doFilter(request, response);
    } finally {
      strategy.release();
    }
  }

  @Override
  public void destroy()
  {
    
  }
  
}
