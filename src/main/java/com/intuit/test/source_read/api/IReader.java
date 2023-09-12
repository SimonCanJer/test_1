package com.intuit.test.source_read.api;

import java.util.function.Consumer;

/**
 *  This interface declares functionality to read content from a data source
 * @param <T>
 */
public interface IReader<T> {

    void readContent(Consumer<T> consumer) throws Exception;
    String source();



}
