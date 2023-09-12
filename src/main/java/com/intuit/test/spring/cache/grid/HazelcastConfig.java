package com.intuit.test.spring.cache.grid;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientClasspathXmlConfig;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.config.impl.ClientXmlConfigRootTagRecognizer;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerToKeyCodec;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.util.Optional;

@Slf4j
@Configuration
@EnableCaching
public class HazelcastConfig {

    @Bean
    HazelcastInstance hazelcastInstance(@Autowired Environment env) {
/*
 * here should be a bit more work to configure hazelcast's IQueues for i messaging.
 * But this option requires to know a concrete discovery mechanism and networking.
 * But it is ok for internal caching.
 * Configuration should be delegates to maps
 */
        Boolean isClient = Optional.ofNullable(env.getProperty("config.hazelcast.is_client", Boolean.class)).orElse(false);
        if (isClient)
            return HazelcastClient.newHazelcastClient();
        else
            return Hazelcast.newHazelcastInstance();
    }


}
