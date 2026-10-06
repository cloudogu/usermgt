package de.triology.universeadm;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CasSessionValidationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        boolean hasAuth = false;
        if (session != null) {
            hasAuth = session.getAttribute(
                "org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY"
            ) != null;
        }

        if (request.isRequestedSessionIdValid() && session != null && !hasAuth) {

            session.invalidate();

            response.sendRedirect(request.getRequestURI());
            return;
        }

        // remove ;jsessionid if session is not valid
        if (request.isRequestedSessionIdFromURL() && !request.isRequestedSessionIdValid()) {

            String uri = request.getRequestURI();

            // ;jsessionid remove
            String cleanUri = uri.replaceAll(";jsessionid=[^/?]*", "");

            // restructure query params
            String query = request.getQueryString();
            if (query != null) {
                cleanUri += "?" + query;
            }

            // redirect to clean URL
            response.sendRedirect(cleanUri);
            return;
        }

        // follow chain
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
