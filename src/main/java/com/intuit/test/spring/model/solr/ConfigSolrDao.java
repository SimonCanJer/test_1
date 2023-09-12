package com.intuit.test.spring.model.solr;

import com.intuit.test.model.dao.api.IDao;
import com.intuit.test.model.dao.api.ISubscribe;
import com.intuit.test.model.dao.api.entities.IPlayer;
import com.intuit.test.model.dao.dao_solr.SolrDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.Optional;

/**
 * Configures Solr based dao
 */
@Configuration
public class ConfigSolrDao {


    public static final String CONFIG_DATA_SOLR_PAGE_SIZE = "config.data.solr.pageSize";
    static private String SOLR_URL= "config.data.solr.url";
    static private String SOLR_UPDATE= "config.data.solr.update";

    //for test inspection
    static SolrDao lastInstance;
    /**
     * Delivers IDo beam in Solr implementation
     * @param e - Spring's environment
     * @param subscriber -String subscriver
     * @return the neab required
     */
    @Bean
    IDao solrDao(@Autowired Environment e, ISubscribe<File> subscriber){
        String fileLocation=e.getProperty(SOLR_UPDATE) ;
        Integer pageSize= Optional.ofNullable(e.getProperty(CONFIG_DATA_SOLR_PAGE_SIZE,Integer.class)).orElse(1000);
        String url= e.getProperty(SOLR_URL);
        SolrDao dao= null;
        if(fileLocation!=null)
        {
            File f= new File(fileLocation);
            if(f.exists()){
                dao= new SolrDao(f,pageSize,subscriber);
            }

        }
        else{
            dao=new SolrDao(url,pageSize,subscriber).observe(fileLocation);
        }
        lastInstance=dao;
        dao.observe(fileLocation);
        return cachedDao(dao);
    }

    private static IDao cachedDao(SolrDao dao) {
        return new IDao() {
            @Override
            public Flux<IPlayer> allPlayers() {
                return dao.allPlayers();
            }
            @Override
            @Cacheable
            public IPlayer get(String id) {
                return dao.get(id);
            }
        };
    }
}
