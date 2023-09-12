package com.intuit.test.model.dao.dao_solr;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.test.TestConstants;
import com.intuit.test.model.dao.api.IDao;
import com.intuit.test.model.dao.api.entities.IPlayer;
import com.intuit.test.source_read.cvs.csvBean.CsvReader;
import com.intuit.test.source_read.cvs.csvBean.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * tests load per
 */
@Slf4j
@EnabledIfEnvironmentVariable(named = "DOCKERIZED_LONG", matches = "do")
class SolrDaoTest {
    /** default page size to communicate to docker **/
    public static final int PAGE_SIZE = 4000;
    /**
     * Performs testing for write players and their retrieval
     */
    @Test
    public void Test() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String[] url = new String[1];
            HttpSolrClient solr = DockerUtils.dockerConnect("http://localhost:8983", "solr_int", "/etc/data", "players", url);
            solr.setParser(new XMLResponseParser());
            if (solr == null) {
                log.error("cannot start solar{}, test impossible", DockerUtils.lastErrorMessage());
            }
            IDao dao = new SolrDao(url[0], PAGE_SIZE,null);
            ArrayList<SolrPlayer> players = new ArrayList<>(PAGE_SIZE);
            CsvReader reader = new CsvReader(TestConstants.CSV_SOURCE, Player.class, null);
            reader.readContent((r) -> {
                try {
                    SolrPlayer p = mapper.convertValue(r, SolrPlayer.class);
                    players.add(p);
                    if (players.size() == PAGE_SIZE) {
                        solr.addBeans(players);
                        players.clear();
                    }
                } catch (Exception e) {
                    fail(e);
                }
            });
            if(players.size()>0)
            {
                solr.addBeans(players);
            }
            solr.commit();
            Flux<IPlayer> flux = dao.allPlayers();
            AtomicInteger counter = new AtomicInteger();
            flux.doOnNext((p) -> counter.incrementAndGet()).timeout(Duration.ofSeconds(2000)).subscribe();
            assertTrue(counter.get()>1000);
        } catch (Exception e) {
            fail(e);
        }
    }

    @AfterAll()
    public static void cleanupSolr() {

        try {
            DockerUtils.removeContainer("solr_int");
        } catch (IOException e) {
            log.error("cannot exclude container");
        }
    }

}