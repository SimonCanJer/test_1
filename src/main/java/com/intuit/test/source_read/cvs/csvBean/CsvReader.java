package com.intuit.test.source_read.cvs.csvBean;

import com.intuit.test.source_read.api.IReader;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class implements the {@link IReader} for Csv
 * @param <T>
 */
@Slf4j
@SuppressWarnings("unchecked")
public class CsvReader<T> implements IReader<T> {

    private final String file;
    private final Class<? extends T> type;
    private final Function<T, String> mapper;

    public <IMPL extends T> CsvReader(String file, Class<IMPL> c, Function<T,String> mapper) {

        this.file = file;
        this.type = c;
        this.mapper=mapper;

    }

    @Override
    public void  readContent(Consumer<T> handler) throws FileNotFoundException {

        String from= file;
        File f= new File(from);
        if(!f.exists()){
            from="/etc/intuit/data/player.csv";
            log.warn("The file does not exist {}, trying {}", file,from );
        }
        new CsvToBeanBuilder<T>(new FileReader(from))
                .withType(type)
                .build().parse().forEach((t)->handler.accept(t));
    }

    @Override
    public String source() {
        return file;
    }

}
