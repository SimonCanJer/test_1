package com.intuit.test.model.dao.dao_solr;

import com.intuit.test.model.dao.api.IDao;
import com.intuit.test.model.dao.api.ISubscribe;
import com.intuit.test.model.dao.api.entities.IPlayer;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.StringUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link IDao} implementation for Solr search engine.
 */
@Slf4j
public
class SolrDao implements IDao {

    private final AtomicReference<HttpSolrClient> refClient = new AtomicReference<>();
    private final int pageSize;
    private String targetFile;

    private final String url;

    public SolrDao(File f,int pageSize,ISubscribe<File>  subscriber){
        this(extractUrl(f), pageSize,subscriber);
    }

    public SolrDao(String solrUrl, int pageSize, ISubscribe<File> subscriber) {
        this.url= solrUrl;
        this.pageSize = pageSize;
        refClient.set( new HttpSolrClient.Builder(solrUrl).build());
        refClient.get().setParser(new XMLResponseParser());
        if(subscriber!=null && targetFile!=null) {
            subscriber.subscribe(targetFile,(file,val)->{
                Thread t= new Thread(()->{
                    String line = extractUrl(val);
                    if (line == null) return;
                    HttpSolrClient clientNew = new HttpSolrClient.Builder(solrUrl).build();
                    if(clientNew==null)
                    {
                        log.warn("bad url received {}, cannot connect",line);
                        return;
                    }
                    log.info("changed url for {}", line);
                    clientNew.setParser(new XMLResponseParser() );
                    refClient.set(clientNew);
                });
            });
        }
    }

    private static String extractUrl(File val) {
        String line=null;
        try{
            Scanner scanner = new Scanner(val);

            while(StringUtils.isEmpty(line)&& scanner.hasNextLine()){
                line=scanner.nextLine();
            }
            scanner.close();
        }
        catch(Exception e){
            log.error("Error while scanning new for solr {}",e.getMessage());
            return null;
        }
        if(StringUtils.isEmpty(line)){
            log.error("no url received");
            return null;
        }
        line=line.trim();
        return line;
    }

    @Override
    public Flux<IPlayer> allPlayers() {
        AtomicInteger pages= new AtomicInteger();
        HttpSolrClient client= refClient.get();
        return Flux.create((fs)->{
            if(!fs.isCancelled()){
                SolrQuery batchQ = new SolrQuery("playerID:*");
                batchQ.setStart(pages.getAndIncrement()*pageSize);
                batchQ.setRows(pageSize);
                try {
                    List<SolrPlayer> result= client.query(batchQ).getBeans(SolrPlayer.class);
                    Iterator<SolrPlayer> it= result.iterator();
                    while(it.hasNext() && !fs.isCancelled()){
                        fs.next(it.next());
                    }
                } catch (Exception e) {
                  log.error("error in batch query: {}",e.getMessage());
                  fs.error(e);
                }
            }
            if(fs.isCancelled())
                fs.complete();
        });
    }

    @Override
    public IPlayer get(String id) {
        SolrQuery findQ = new SolrQuery(String.format("playerID:%s"));
        try {
            QueryResponse response = refClient.get().query(findQ);
            List<SolrPlayer> res= response.getBeans(SolrPlayer.class);
            if(res!=null && res.size()>0){
                return res.get(0);
            }
        } catch (Exception e) {
            log.error("error in query {}", e.getMessage());
        }
        return null;
    }
    public SolrDao observe(String targetFile){
        this.targetFile= targetFile;
        return this;
    }

    public String observed() {
        return targetFile;
    }
    public String  url(){
        return url;
    }
}
