package com.intuit.test.source_read;

import com.intuit.test.TestConstants;
import com.intuit.test.model.dao.api.entities.IPlayer;
import com.intuit.test.source_read.cvs.csvBean.CsvReader;
import com.intuit.test.source_read.cvs.csvBean.Player;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderTest {
    /**
     * Checks correctity of read file
     */
    @Test
    void readContent() {
        CsvReader<Player> reader= new CsvReader<>(TestConstants.CSV_SOURCE,Player.class,(p)->p.getPlayerID());
        try {
            Map<String, IPlayer> players= new HashMap<>();
             reader.readContent(p->players.put(p.getPlayerID(),p));
            assertTrue(players.size()>0);
            IPlayer p=players.get("mauldma01");
            assertNotNull(p);
            assertEquals(1914,p.getBirthYear());
            assertEquals(19370,players.size());
        }
        catch(Exception e){
            assertNull(e,"exception thrown "+e.getMessage());
        }

    }
}