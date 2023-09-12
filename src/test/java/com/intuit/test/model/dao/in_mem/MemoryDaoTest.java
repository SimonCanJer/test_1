package com.intuit.test.model.dao.in_mem;

import com.intuit.test.TestConstants;
import com.intuit.test.model.dao.api.ISubscribe;
import com.intuit.test.model.dao.api.entities.IPlayer;
import com.intuit.test.source_read.api.IReader;
import com.intuit.test.source_read.cvs.csvBean.CsvReader;
import com.intuit.test.source_read.cvs.csvBean.Player;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Goal testing consitence of memeory based dao.
 *
 */
class MemoryDaoTest {
    @Test
    public void test() throws InterruptedException {
        Map<String, IPlayer>empty= new ConcurrentHashMap<>();
        IReader<IPlayer> base= new CsvReader<>(TestConstants.CSV_SOURCE, Player.class,(p)->p.getPlayerID());
        boolean [] useReal= new boolean[]{true};
        BiConsumer<String, File>[] refSink= new BiConsumer[1];
        // the pseudo reader initially reads from a corrent source, and then
        // from and empty array to emulate changes in sources
        IReader<IPlayer> reader= new IReader<IPlayer>() {
            @Override
            public void readContent(Consumer<IPlayer> consumer) throws Exception {
                if (useReal[0]) {
                    base.readContent(consumer);
                } else {
                    empty.values().forEach((p) -> {
                        consumer.accept(p);
                    });
                }
            }
            @Override
            public String source() {
                return TestConstants.CSV_SOURCE;
            }
        };
//map is reade normally
        MemoryDao dao=new MemoryDao(reader, new ISubscribe<File>() {
            @Override
            public void subscribe(String file, BiConsumer<String, File> sink) {
                refSink[0]=sink;
            }
        });
        assertNotNull(dao.get("abadan01"));
        Map<String, IPlayer> map= new ConcurrentHashMap<>();
        Flux<IPlayer> f= dao.allPlayers();
        f.doOnNext((p)->{map.put(p.getPlayerID(),p);}).timeout(Duration.ofSeconds(10)).subscribe();
        assertEquals(19370,map.size());
        //now subsctption will be triggered like a changes in source detectedd@
        useReal[0]=false;
        refSink[0].accept("s",null);
        map.clear();
        Thread.sleep(1000);
        f.doOnNext((p)->{map.put(p.getPlayerID(),p);}).timeout(Duration.ofSeconds(10)).subscribe();
        assertEquals(0,map.size());

    }

}