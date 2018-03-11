package org.dijure.biographies.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.dijure.biographies.model.Biography;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
public class BiographiesService
{
    private static final Logger LOG = LoggerFactory.getLogger(BiographiesService.class);

    /**
     * In the event of an unknown item.
     */
    private static final Biography UNKNOWN = new Biography();

    /**
     * The biographies associated with an author name.
     */
    private Map<String, Biography> biographies = new HashMap<>(20);

    BiographiesService() throws IOException
    {
        populateData();
    }

    /**
     * Get the author's bio.
     *
     * @param firstName Author's name
     * @param lastName  Author's name
     * @return The author's bio
     */
    public Biography getBiography(String firstName, String lastName)
    {
        String fullName = firstName + ' ' + lastName;
        if (biographies.containsKey(fullName))
        {
            return biographies.get(fullName);
        }

        return UNKNOWN;
    }

    /**
     * Obtain the full collection of bios.
     *
     * @return
     */
    public Map<String, Biography> getBiographies()
    {
        return biographies;
    }

    /**
     * Obtain a randomly selected bio.
     *
     * @return
     */
    public Biography getRandomBiography()
    {
        List<Biography> valuesList = new ArrayList<>(biographies.values());
        Biography biography = valuesList.get(new Random().nextInt(valuesList.size()));

        LOG.info("Biography found: {}", biography);

        return biography;
    }

    /**
     * Load the bios from a resource
     */
    private void populateData() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        InputStream sourceStream = getClass().getClassLoader().getResourceAsStream("biographies.yaml");

        Map<String, Object> rawParsedValues =
                mapper.readValue(sourceStream, new TypeReference<Map<String, Object>>()
                {
                });
        for (String key : rawParsedValues.keySet())
        {
            LinkedHashMap value = (LinkedHashMap) rawParsedValues.get(key);

            Biography biography = new Biography();
            biography.setBirth(value.get("birth").toString());
            biography.setOccupation(value.get("occupation").toString());
            biography.setNationality(value.get("nationality").toString());

            biographies.put(key, biography);
        }
    }
}
