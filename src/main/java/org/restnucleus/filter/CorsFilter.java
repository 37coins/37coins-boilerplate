package org.restnucleus.filter;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class CorsFilter implements Filter{
    private List<String> cacheExceptions;
    private String allowOrigin;
    
    public CorsFilter(String allowOrigin) {
        this.allowOrigin = allowOrigin;
        cacheExceptions = null;
    }
    
    public CorsFilter(String allowOrigin, List<String> cacheExceptions) {
        this.allowOrigin = allowOrigin;
        this.cacheExceptions = cacheExceptions;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain filterChain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse rsp = (HttpServletResponse)response;
        if (req.getPathInfo()!=null){
            boolean found = false;
            if (null!=cacheExceptions)
                for (String ex : cacheExceptions)
                    if (req.getPathInfo().contains(ex))
                        found = true;
            if (!found){
                rsp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                rsp.setHeader("Pragma", "no-cache");
                rsp.setDateHeader("Expires", 0);
            }
        }
        if (req.getMethod().equals("OPTIONS")){
            rsp.setHeader("Access-Control-Allow-Origin", allowOrigin);
            rsp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
            rsp.setHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept, Authorization");
            rsp.setHeader("Access-Control-Max-Age", "1728000");
        }
        filterChain.doFilter(request, rsp);
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig filterConfig)throws ServletException{}
}