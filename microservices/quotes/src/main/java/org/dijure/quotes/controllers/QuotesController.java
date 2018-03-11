package org.dijure.quotes.controllers;

import org.dijure.quotes.service.QuotesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
public class QuotesController
{
    private static final Logger LOG = LoggerFactory.getLogger(QuotesController.class);

    @Autowired
    private QuotesService quotesService;


    @RequestMapping("/quote/full")
    @ResponseBody
    public List<String> getFullQuote()
    {
        LOG.info("Request quote full quote from all services.");
        List<String> fullQuote = new ArrayList<>(3);

        String authorName = lookupAuthorNameRandom();
        fullQuote.add(authorName);
        String authorBio = lookupAuthorBio(authorName);
        fullQuote.add(getString(fullQuote, authorName, authorBio));

        return fullQuote;
    }


    @RequestMapping("/quote/author/{firstName}/{lastName}")
    @ResponseBody
    public List<String> getAuthorQuote(@PathVariable String firstName, @PathVariable String lastName)
    {
        LOG.info("Request quote from author {} {}", firstName, lastName);
        return quotesService.getQuotes(firstName, lastName);
    }

    @RequestMapping("/quote/random")
    @ResponseBody
    public List<String> getQuoteRandomAuthor()
    {
        LOG.info("Request random quote");
        return quotesService.getRandomQuote();
    }

    @RequestMapping("/quote/list")
    @ResponseBody
    public Map<String, List<String>> getAllAuthorQuotes()
    {
        LOG.info("Request full list");
        return quotesService.getQuotes();
    }

    @RequestMapping("/forceError")
    @ResponseBody
    public String forceError()
    {
        LOG.info("Error being forced");
        throw new RuntimeException("An error has been forced for demonstration.");
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

    private String lookupAuthorNameRandom()
    {
        RestTemplate restTemplate = new RestTemplate();
        try
        {
            return parseFullName(restTemplate.getForObject("http://ms-authors.quotes.svc.cluster.local:80/author/random", String.class));
        } catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    /**
     * Parse out the full name from a string like this,
     * <p>
     * ["{\"first\":\"Steven\",\"last\":\"Wright\"}","todo"]
     * <p>
     * TODO: it's brute force now, consider json parser later
     */
    private String parseFullName(String rawNameData)
    {
        String[] parts = rawNameData.split(":");

        String first = parts[1].split("\"")[1];
        first = first.substring(0, first.length());

        String last = parts[2].split("\"")[1];
        last = last.substring(0, last.length());

        return first + ' ' + last;
    }

    private String lookupAuthorBio(String authorFullName)
    {
        String[] nameParts = authorFullName.split(" ");
        RestTemplate restTemplate = new RestTemplate();
        try
        {
            String url = "http://ms-biographies.quotes.svc.cluster.local:80/bio/author/{firstName}/{lastName}";
            LOG.info("Calling: {} with: {} {}", url, nameParts[0], nameParts[1]);
            return restTemplate.getForObject(url, String.class, nameParts[0], nameParts[1]);
        } catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    private String getString(List<String> fullQuote, String authorName, String authorBio)
    {
        String quote = "";
        List<String> quotes = quotesService.getQuotes(authorName);
        fullQuote.add(authorBio);
        if (!quotes.isEmpty())
        {
            quote = quotes.get(new Random().nextInt(quotes.size()));
            LOG.info("Full quote: Author quote {}", quote);
        }
        return quote;
    }
}
