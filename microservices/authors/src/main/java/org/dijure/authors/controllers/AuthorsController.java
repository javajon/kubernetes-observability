package org.dijure.authors.controllers;

import org.dijure.authors.model.Author;
import org.dijure.authors.service.AuthorsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class AuthorsController
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthorsController.class);

    @Autowired
    private AuthorsService authorsService;

    @RequestMapping("/author/random")
    @ResponseBody
    public Author getAuthorRandom()
    {
        LOG.info("Request random author");
        latencyRequest();
        return authorsService.getRandomAuthor();
    }

    @RequestMapping("/author/list")
    @ResponseBody
    public List<Author> getAuthors()
    {
        LOG.info("Request author listing");
        latencyRequest();
        return authorsService.getAuthors();
    }

    @RequestMapping("/forceError")
    @ResponseBody
    public String forceError()
    {
        LOG.info("Force an error");
        throw new RuntimeException("An error has been forced for demonstration.");
    }

    @RequestMapping("/latency")
    @ResponseBody
    public String getLatency()
    {
        String latencyText = "LATENCY = " + getLatencyValue();
        LOG.info(latencyText);
        return latencyText;
    }

    @RequestMapping("/")
    @ResponseBody
    public String getProbed()
    {
        LOG.info("Probed");
        return "Probed.";
    }

    @ExceptionHandler(Exception.class)
    public String exception(Exception e)
    {
        return "An API exception has been encountered: " + e.getMessage();
    }

    private int getLatencyValue()
    {
        return Integer.parseInt(System.getProperty("LATENCY", "0"));
    }

    private void latencyRequest()
    {
        int latency = getLatencyValue();
        if (latency > 0)
        {
            try { TimeUnit.MILLISECONDS.sleep(latency); }
            catch(InterruptedException ex) { LOG.error(ex.getMessage(), ex); }
        }
    }
}
