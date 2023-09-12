package com.intuit.test.spring.source_read;

import com.intuit.test.model.dao.api.entities.IPlayer;
import com.intuit.test.source_read.api.IReader;
import com.intuit.test.source_read.cvs.csvBean.CsvReader;
import com.intuit.test.source_read.cvs.csvBean.Player;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class configures source reader to
 * read a csv file from source, which is names in a related property
 */
@Configuration
@Slf4j
public class ConfigReaders {

    /** this class is necessary because direct injection of a value not works always
     * when exporting beans
     */
    @Getter
    static class Property{
        /** this is path to file, where read data from */
        @Value("${data.source.file}")
        private String file;
    }
    @Bean
    Property props(){

        return new Property();

    }

    /**
     * exports {@link IReader bean}
     * @param props  - export properties
     * @return
     */
    @Bean
    IReader<IPlayer>  csvReadBean(@Autowired Property props){
        log.debug("exporting csv reader bean..");
        return new CsvReader<IPlayer>(props().getFile(), Player.class,(p)->p.getPlayerID() );
    }
}
