package org.dijure.biographies.service;

import io.prometheus.client.Counter;
import io.prometheus.client.Summary;

public interface MetricStatics
{
    /**
     * labels that will be added to all the metrics.
     */
    String[] METRIC_LABELS = {"method", "status", "biographies"};

    /**
     * Name of parameter that will be added to the request to allow computation of the duration of the request.
     */
    String REQ_PARAMS_TIMING = "timing";

    /**
     * Counter to count the number of request (R of RED).
     */
    Counter REQUEST_COUNTER = counter("demo_http_requests_total", "Total HTTP Request");

    /**
     * Counter to count the number of request. (E of RED).
     */
    Counter ERROR_COUNTER = counter("demo_http_errors_total", "Total HTTP Errors");

    /**
     * Summary to get the amount of time taken to fulfill request (D of RED).
     */
    Summary RESPONSE_DURATION = summary("http_response_time_milliseconds", "Duration of " +
            "request in milliseconds");

    /**
     * Summary to get the size of the request.
     */
    Summary REQUEST_SIZE = summary("http_request_size_bytes", "Size of request in bytes");

    static Counter counter(String name, String help)
    {
        return Counter.build(name, help).labelNames(METRIC_LABELS).register();
    }

    static Summary summary(String name, String help)
    {
        return Summary.build(name, help).labelNames(METRIC_LABELS).register();
    }
}
