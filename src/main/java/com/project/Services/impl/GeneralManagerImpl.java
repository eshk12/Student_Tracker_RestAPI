package com.project.Services.impl;

import com.project.Persist;
import com.project.Services.GeneralManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


/**
 * Created by Sigal on 5/21/2016.
 */

@Service
@DependsOn(value = {"persist"})
public class GeneralManagerImpl implements GeneralManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralManagerImpl.class);

    @Autowired
    private Persist persist;

    @PostConstruct
    public void init() {
    }


}
