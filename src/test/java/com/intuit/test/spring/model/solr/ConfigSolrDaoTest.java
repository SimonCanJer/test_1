package com.intuit.test.spring.model.solr;

import com.intuit.test.model.dao.api.IDao;
import com.intuit.test.model.dao.api.ISubscribe;
import com.intuit.test.model.dao.api.entities.IPlayer;
import com.intuit.test.model.dao.dao_solr.SolrDao;
import com.intuit.test.spring.file_source.scheduler.SourceObserver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * tests validity of configuration, not of connection one
 */
@SpringBootTest(classes={ConfigSolrDao.class, SourceObserver.class})
class ConfigSolrDaoTest {

    @Autowired
    IDao dao;
    @Autowired
    ISubscribe subscriber;
    @Test
    public void test(){
        SolrDao slr = ConfigSolrDao.lastInstance;
        assertEquals("observe/update.solr",slr.observed());
        assertEquals("http://host.docker.internal:8983/solr/players",slr.url());
    }

}