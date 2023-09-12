package com.intuit.test.model.dao.api;

import java.util.function.BiConsumer;

/**
 * subscribe changes interface
 * @param <T> type of expected object
 */
public interface ISubscribe<T> {

    /**
     * subscription method
     *
     * @param sink : {@link BiConsumer, expects data changes}
     */
    void subscribe(String file,BiConsumer<String,T> sink);


}
