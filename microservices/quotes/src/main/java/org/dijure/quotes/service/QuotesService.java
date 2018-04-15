package org.dijure.quotes.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
public class QuotesService
{
    private static final Logger LOG = LoggerFactory.getLogger(QuotesService.class);

    /**
     * In the event of an unknown author.
     */
    private static final List<String> UNKNOWN_QUOTE = Collections.emptyList();

    /**
     * The quotes associated with an author name.
     */
    private final Map<String, List<String>> quotes = new HashMap<>(20);

    QuotesService() throws IOException
    {
        populateData();
    }

    /**
     * Get the author's quotes.
     *
     * @param firstName Author's name
     * @param lastName  Author's name
     * @return The author's quotes
     */
    public List<String> getQuotes(String firstName, String lastName)
    {
        return getQuotes(firstName + ' ' + lastName);
    }

    /**
     * Get the author's quotes.
     * @param fullName
     * @return The author's quotes
     */
    public List<String> getQuotes(String fullName)
    {
        if (quotes.containsKey(fullName))
        {
            return quotes.get(fullName);
        }

        return UNKNOWN_QUOTE;
    }

    /**
     * Obtain the full collection of quotes.
     *
     * @return
     */
    public Map<String, List<String>> getQuotes()
    {
        return quotes;
    }

    /**
     * Obtain a quote set from a random author
     *
     * @return
     */
    public List<String> getRandomQuote()
    {
        List<List<String>> valuesList = new ArrayList<>(quotes.values());
        List<String> quotes = valuesList.get(new Random().nextInt(valuesList.size()));

        LOG.info("Quotes found: {}", quotes);

        return quotes;
    }

    /**
     * Load all author quotes from a resource
     */
    private void populateData() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        InputStream sourceStream = getClass().getClassLoader().getResourceAsStream("quotes.yaml");

        Map<String, Object> rawParsedValues =
                mapper.readValue(sourceStream, new TypeReference<Map<String, Object>>()
                {
                });
        for (String key : rawParsedValues.keySet())
        {
            List<?> quoteTexts = (List) rawParsedValues.get(key);
            List<String> authorQuotes = new ArrayList<>(quoteTexts.size());
            for (Object rawQuote : quoteTexts)
            {
                authorQuotes.add(rawQuote.toString());
            }
            quotes.put(key, authorQuotes);
        }
    }
}
