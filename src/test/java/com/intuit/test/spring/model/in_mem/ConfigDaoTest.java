package com.intuit.test.spring.model.in_mem;

import com.intuit.test.model.dao.api.IDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestConfig.class)
class ConfigDaoTest {
    @Autowired
    IDao dao;

    @Test
    public void test() {

        assertNotNull(dao);
        AtomicInteger counter=new AtomicInteger();
        dao.allPlayers().doOnNext((p)->{counter.getAndIncrement();}).timeout(Duration.ofSeconds(20)).subscribe();
        assertTrue(counter.get()>19000, "size must be not null");

    }

}