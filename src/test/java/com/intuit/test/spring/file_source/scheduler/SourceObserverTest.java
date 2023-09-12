package com.intuit.test.spring.file_source.scheduler;

import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * we test chron bean to be sure that it trigs;
 */
@Disabled
@SpringBootTest(classes=SourceObserver.class)
class SourceObserverTest {
    static AtomicInteger counter= new AtomicInteger();
    static BiConsumer<String, File> sink=(k,v)->{
        counter.getAndIncrement();
    };
    static File chron= new File("chron");
    // chron property should be set now
    static{
        System.setProperty("observer.chron","*/5 * * * * *");
        System.setProperty("data.source.file","chron");
        writeFakeFile("chron");
    }

    private static void writeFakeFile(String s) {
        try {
            FileOutputStream fos= new FileOutputStream(chron);
            fos.write(s.getBytes());
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    SourceObserver observer;

    @Test
    void test() throws InterruptedException {
        observer.subscribe("chron",sink);
        writeFakeFile("chron");
        Thread.sleep(10000);
        assertTrue(counter.get()>0);
    }
    @AfterAll
    static public  void post(){
        chron.delete();
    }

}