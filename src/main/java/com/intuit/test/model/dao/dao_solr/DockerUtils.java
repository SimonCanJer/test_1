package com.intuit.test.model.dao.dao_solr;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;
import java.util.Scanner;

public class DockerUtils {

    static private ThreadLocal<String> lastErrorMessage = new ThreadLocal<String>();

    static HttpSolrClient dockerConnect(String url, String containerName, String volume, String core,String[]clientUrl) throws IOException {
        int start=startContainer(containerName,volume,core);
        if(0>start){
            return null;
        }
        createPartition(containerName, core);
        String urlString = String.format("%s/solr/%s",url,core);
        clientUrl[0]=urlString;
        return new HttpSolrClient.Builder(urlString).build();
    }
    public static int startContainer(String name, String mappingDir,String partition) throws IOException {
        String mapping= "";
        //mappingDir!=null?String.format("-v %s:/var/solr",mappingDir):"";
        String execRun = String.format("docker run -di  %s -p 8983:8983 --name %s solr solr-precreate %s",mapping, name, partition);
        String[] res = collectProcessInput(Runtime.getRuntime().exec(execRun));
        if (res[0].length() > 0) {
            if (res[0].contains(" The container name") && res[0].contains("is already in use by container ")) {
                if (isContainerRunning(name))
                    return 0;
                else {
                    if (!removeContainer(name))
                        return -1;
                }
                res = collectProcessInput(processOf(execRun));
                return (res[0].length() == 0)?0:-1;
            }
            return -1;
        }
        return 1;
    }

    public static boolean removeContainer(String name) throws IOException {
        String execRun = String.format("docker kill %s", name);
        String[] res = collectProcessInput(Runtime.getRuntime().exec(execRun));
        if(res[0].length()>0&&!res[0].contains("is not running"))
            return false;
        execRun = String.format("docker rm %s", name);
        res = collectProcessInput(Runtime.getRuntime().exec(execRun));
        return res[0].length() == 0 && !res[0].toLowerCase().contains("no such container");
    }

    public static  String[] collectProcessInput(Process p) {
        Scanner error = new Scanner(p.getErrorStream());
        Scanner report = new Scanner(p.getInputStream());
        StringBuilder sbe = new StringBuilder();
        StringBuilder sbr = new StringBuilder();
        while (error.hasNextLine()) {
            sbe.append(error.nextLine());
        }
        while (report.hasNextLine()) {
            sbr.append(report.nextLine());

        }
        String errorMessage=sbe.toString();
        lastErrorMessage.set(errorMessage);
        return new String[]{errorMessage, sbr.toString()};

    }

    public static boolean startReadyContainer(String name) throws IOException {
        String execRun = String.format("docker start  %s", name);
        String[] out = collectProcessInput(Runtime.getRuntime().exec(execRun));
        if (out[0].length() == 0)
            return true;
        else {
            return false;
        }
    }

    public static boolean createPartition(String name, String core) {
        String str = String.format("docker exec -i --user solr %s bin/solr create_core -c %s", name, core);
        try {
            Process p = Runtime.getRuntime().exec(str);
            String[] result = collectProcessInput(p);

            if (result[0].contains("already exists"))
                return true;
            return false;


        } catch (IOException e) {
            return false;
        }

    }
    public static String  lastErrorMessage(){
        return lastErrorMessage.get();

    }

    static boolean isContainerRunning(String name) throws IOException {
        String[] results = collectProcessInput(processOf(String.format("docker ps ")));
        if (results[1].contains(" " + name))
            return true;
        return false;

    }

    static Process processOf(String s) throws IOException {
        return Runtime.getRuntime().exec(s);
    }
    static boolean deleteCore(String container,String core) throws IOException {
        String[] result= collectProcessInput(processOf(String.format("docker exec -i --user solr %s bin/solr delete -c %s ",container,core)));
        return result[0].length()==0;

    }
}
