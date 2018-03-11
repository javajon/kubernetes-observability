package org.dijure.biographies.controllers;

import org.dijure.biographies.model.Biography;
import org.dijure.biographies.service.BiographiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class BiographiesController
{
    private static final Logger LOG = LoggerFactory.getLogger(BiographiesController.class);

    @Autowired
    private BiographiesService biographiesService;

    @RequestMapping("/bio/author/{firstName}/{lastName}")
    @ResponseBody
    public Biography getBiography(@PathVariable String firstName, @PathVariable String lastName)
    {
        Biography biography = biographiesService.getBiography(firstName, lastName);
        LOG.info("Biography found for {} {}: {}", firstName, lastName, biography);

        return biography;
    }

    @RequestMapping("/bio/random")
    @ResponseBody
    public Biography getBiographyRandom()
    {
        LOG.info("Request random bio");
        return biographiesService.getRandomBiography();
    }

    @RequestMapping("/bio/list")
    @ResponseBody
    public Map<String, Biography> getBiographies()
    {
        LOG.info("Request bio listing");
        return biographiesService.getBiographies();
    }

    @RequestMapping("/forceError")
    @ResponseBody
    public String forceError()
    {
        LOG.info("Force an error");
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
}
