package com.project;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;

/**
 * Created by Sigal on 5/20/2016.
 */
@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;

    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    @PostConstruct
    public void init () {

    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Object o) {
        sessionFactory.getCurrentSession().saveOrUpdate(o);
    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return (T) getQuerySession().get(clazz, oid);
    }
}