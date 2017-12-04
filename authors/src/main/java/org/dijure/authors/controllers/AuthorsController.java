package org.dijure.authors.controllers;

import org.dijure.authors.model.Author;
import org.dijure.authors.service.AuthorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorsController
{
    @Autowired
    private AuthorsService authorsService;

    @RequestMapping("/author/random")
    @ResponseBody
    public Author getAuthorRandom()
    {
        Author randomAuthor = authorsService.getRandomAuthor();
        return randomAuthor;
    }

    @RequestMapping("/author/list")
    @ResponseBody
    public List<Author> getAuthors()
    {
        return authorsService.getAuthors();
    }
}
