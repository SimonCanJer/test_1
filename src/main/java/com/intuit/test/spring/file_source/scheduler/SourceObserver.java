package com.intuit.test.spring.file_source.scheduler;

import com.intuit.test.model.dao.api.ISubscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Component
@Slf4j
@EnableScheduling
public class SourceObserver implements ISubscribe<File> {
    Instant last;
    @Value("${observer.chron}")
    String chron;
    private final List<Observed> sinks= new ArrayList();



    private class Observed implements Runnable{
          private final File file;
          private Instant observed;
          private final BiConsumer<String, File> handler;
          Observed(File f, BiConsumer<String, File> handler){
              file= f;
              observed =readFileTime();
              this.handler = handler;
         }
        private Instant readFileTime(){
            try {
                if(!file.exists())
                    return null;
                BasicFileAttributes fatr = Files.readAttributes(file.toPath(),
                        BasicFileAttributes.class);
                return fatr.lastModifiedTime().toInstant();
            } catch (IOException e) {
                return null;
            }
        }
        @Override
         public void run() {
             Instant curr= readFileTime();
             if(observed==null &&curr==null)
                 return ;
             if(observed==null||observed.isBefore(curr)){
                 observed = curr;
             }
             try{
                 handler.accept(file.getAbsolutePath(),file);
             }
             catch(Exception e){

             }
         }
     }
    SourceObserver(){

    }

    public
    @Scheduled(cron ="${observer.chron:*/10 * * * * *}")
    void scheduled(){

       sinks.forEach(Observed::run);

    }
    @Override
    public void subscribe(String file, BiConsumer<String, File> sink) {
        sinks.add(new Observed(new File(file), sink));

    }

}
