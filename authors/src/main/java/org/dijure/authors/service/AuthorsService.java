package org.dijure.authors.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.dijure.authors.model.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;


@Service
public class AuthorsService
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthorsService.class);

    private final List<Author> authors;

    AuthorsService() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        InputStream authorsStream = getClass().getClassLoader().getResourceAsStream("authors.yaml");
        authors = mapper.readValue(authorsStream, new TypeReference<List<Author>>(){});
    }

    public List<Author> getAuthors()
    {
        return authors;
    }

    public Author getRandomAuthor()
    {
        Author author = authors.get(new Random().nextInt(authors.size()));
        LOG.info("Author found: {}", author);

        return author;
    }
}
