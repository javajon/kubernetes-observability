package org.dijure.biographies.service;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CidFilter implements Filter
{
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        if (req instanceof HttpServletRequest)
        {
            String requestCid = ((HttpServletRequest) req).getHeader("CID");
            MDC.put("CID", requestCid);
        }

        try
        {
            // Call filter(s) upstream for the real processing of the request
            chain.doFilter(req, res);
        } finally
        {
            // Important to always clean the cid from the MDC, this Thread goes
            // to the pool but it's log lines would still contain the cid.
            MDC.remove("CID");
        }
    }

    @Override
    public void destroy()
    {
        // nothing
    }

    @Override
    public void init(FilterConfig fc) throws ServletException
    {
        // nothing
    }
}