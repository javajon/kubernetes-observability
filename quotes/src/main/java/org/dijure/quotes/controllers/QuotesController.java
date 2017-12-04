package org.dijure.quotes.controllers;

import org.dijure.quotes.service.QuotesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
        String authorName = lookupAuthorNameRandom();
        LOG.info("Full quote: Author name {}", authorName);

        String authorBio = lookupAuthorBio(authorName);
        LOG.info("Full quote: Author bio {}", authorBio);

        List<String> quotes = quotesService.getQuotes(authorName);

        List<String> fullQuote = new ArrayList<>(3);
        fullQuote.add(authorName);
        fullQuote.add(authorBio);
        if (!quotes.isEmpty())
        {
            String quote = quotes.get(new Random().nextInt(quotes.size()));
            fullQuote.add(quote);
            LOG.info("Full quote: Author quote {}", quote);
        }

        return fullQuote;
    }

    private String lookupAuthorNameRandom()
    {
        RestTemplate restTemplate = new RestTemplate();
        try
        {
            return parseFullName(restTemplate.getForObject("http://authors.quotes.svc.cluster.local:9001/author/random", String.class));
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
     * TODO: its brute force now, use json parser later
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
            String url = "http://biographies.quotes.svc.cluster.local:9001/bio/author/{firstName}/{lastName}";
            LOG.info("Calling: {} with: {} {}", url, nameParts[0], nameParts[1]);
            return restTemplate.getForObject(url, String.class, nameParts[0], nameParts[1]);
        } catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }


    @RequestMapping("/quote/author/{firstName}/{lastName}")
    @ResponseBody
    public List<String> getAuthorQuote(@PathVariable String firstName, @PathVariable String lastName)
    {
        List<String> quotes = quotesService.getQuotes(firstName, lastName);
        return quotes;
    }

    @RequestMapping("/quote/random")
    @ResponseBody
    public List<String> getQuoteRandomAuthor()
    {
        List<String> randomQuotes = quotesService.getRandomQuote();

        return randomQuotes;
    }

    @RequestMapping("/quote/list")
    @ResponseBody
    public Map<String, List<String>> getAllAuthorQuotes()
    {
        return quotesService.getQuotes();
    }
}
