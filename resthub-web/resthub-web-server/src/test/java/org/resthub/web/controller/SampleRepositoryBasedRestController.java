package org.resthub.web.controller;

import org.resthub.web.model.Sample;
import org.resthub.web.repository.SampleResourceRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

@Controller
@RequestMapping("/repository-based")
@Profile("resthub-jpa")
public class SampleRepositoryBasedRestController extends
        RepositoryBasedRestController<Sample, Long, SampleResourceRepository> {

    @Override
    @Inject
    public void setRepository(SampleResourceRepository repository) {
        this.repository = repository;
    }
    
}
