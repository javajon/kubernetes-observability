package org.dijure.quotes.service;

import io.prometheus.client.hotspot.DefaultExports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter for collecting basic statistics at HTTP request/response level. This filter uses
 * a Prometheus client. It will automatically set up a default exporter which collects a
 * number of JVM metrics as well.
 * <p>
 * This does not set up a metrics endpoint to be used by Prometheus to get the metrics, it just
 * sets up the collecting of the metrics.
 * </p>
 */
public class HttpMetricsFilter implements Filter, MetricStatics
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpMetricsFilter.class);

    static
    {
        // Enables JVM statistics to be exported to Prometheus. Sets up default
        // JVM instrumentation provided by the Prometheus client.
        DefaultExports.initialize();
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        LOG.trace("Initializing HttpMetricsFilter.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        LOG.trace("Doing HttpMetricsFilter at {}", httpRequest.getRequestURI());

        // add the current time in ms to the request.
        httpRequest.setAttribute(REQ_PARAMS_TIMING, System.currentTimeMillis());

        chain.doFilter(request, response);

        // compute the duration of the request.
        long duration = System.currentTimeMillis() - (Long) request.getAttribute(REQ_PARAMS_TIMING);

        // determine values of the method and status labels.
        String[] labels = {httpRequest.getMethod(), Integer.toString(httpResponse.getStatus())};

        // save the metrics
        REQUEST_SIZE.labels(labels).observe(request.getContentLengthLong());
        RESPONSE_DURATION.labels(labels).observe(duration);
        REQUEST_COUNTER.labels(labels).inc();

        // capture exceptional situations, in conjunction with controller annotation @ExceptionHandler
        if (httpResponse.getStatus() != HttpStatus.OK.value())
        {
            // determine values of the method and status labels.
            ERROR_COUNTER.labels(labels).inc();
        }
    }

    @Override
    public void destroy()
    {
        LOG.trace("Destroying HttpMetricsFilter");
    }
}

