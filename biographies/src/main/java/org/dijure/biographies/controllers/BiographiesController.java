package org.dijure.biographies.controllers;

import org.dijure.biographies.model.Biography;
import org.dijure.biographies.service.BiographiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
        Biography randomBiography = biographiesService.getRandomBiography();

        return randomBiography;
    }

    @RequestMapping("/bio/list")
    @ResponseBody
    public Map<String, Biography> getBiographies()
    {
        return biographiesService.getBiographies();
    }
}
