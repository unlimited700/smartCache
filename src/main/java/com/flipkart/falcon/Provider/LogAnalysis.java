package com.flipkart.falcon.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by pradeep.joshi on 14/01/18.
 */
public class LogAnalysis {

    private static final ObjectMapper objectMapper = new ObjectMapper() ;

    public static void main(String[] args) {
        LogAnalysis logAnalysis = new LogAnalysis() ;
        logAnalysis.readAndAnalyseFile() ;

    }

    public class ProbData{
        long diff ;
        long currentTime ;
        long expiryTime ;
        String key ;//key modified as "key_expiryTime"

        public long getDiff() {
            return diff;
        }

        public void setDiff(long diff) {
            this.diff = diff;
        }

        public long getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(long currentTime) {
            this.currentTime = currentTime;
        }

        public long getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(long expiryTime) {
            this.expiryTime = expiryTime;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public class CacheKeyStatus{
        String key ;
        long betaRefreshConcurrentCount = 0 ;
        long betaRefreshTrueConcurrentCount = 0 ;
        long totalCacheHitCount = 0;
        double percentageBetaRefresh = 0 ;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getBetaRefreshConcurrentCount() {
            return betaRefreshConcurrentCount;
        }

        public void setBetaRefreshConcurrentCount(long betaRefreshConcurrentCount) {
            this.betaRefreshConcurrentCount = betaRefreshConcurrentCount;
        }

        public long getTotalCacheHitCount() {
            return totalCacheHitCount;
        }

        public void setTotalCacheHitCount(long totalCacheHitCount) {
            this.totalCacheHitCount = totalCacheHitCount;
        }

        public double getPercentageBetaRefresh() {
            return percentageBetaRefresh;
        }

        public void setPercentageBetaRefresh(double percentageBetaRefresh) {
            this.percentageBetaRefresh = percentageBetaRefresh;
        }

        public long getBetaRefreshTrueConcurrentCount() {
            return betaRefreshTrueConcurrentCount;
        }

        public void setBetaRefreshTrueConcurrentCount(long betaRefreshTrueConcurrentCount) {
            this.betaRefreshTrueConcurrentCount = betaRefreshTrueConcurrentCount;
        }
    }

    private void readAndAnalyseFile(){

        try {

            List<String> betaRefreshCacheHitLog1 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_1").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog2 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_2").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog3 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_3").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog4 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_4").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog5 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_5").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog6 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_6").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog7 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_7").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog8 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_8").getPath()), Charset.defaultCharset());
            List<String> betaRefreshCacheHitLog9 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("betaRefreshCacheHitLog_9").getPath()), Charset.defaultCharset());

            List<String> cacheHitLog1 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_1").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog2 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_2").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog3 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_3").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog4 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_4").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog5 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_5").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog6 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_6").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog7 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_7").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog8 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_8").getPath()), Charset.defaultCharset());
            List<String> cacheHitLog9 = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("cacheHitLog_9").getPath()), Charset.defaultCharset());

            List<ProbData> probDataListForBetaRefresh = new ArrayList<ProbData>() ;
            setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog1) ;
            setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog2) ; setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog3) ;
            setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog4) ; setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog5) ;
            setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog6) ; setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog7) ;
            setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog8) ; setProbData(probDataListForBetaRefresh,betaRefreshCacheHitLog9) ;

            Map<String,List<Long>> betaRefreshStore = new HashMap<String,List<Long>>();
            setBetaRefreshStore(betaRefreshStore,probDataListForBetaRefresh) ;

            List<ProbData> probDataListForCacheHit = new ArrayList<ProbData>() ;
            setProbData(probDataListForCacheHit,cacheHitLog1) ;
            setProbData(probDataListForCacheHit,cacheHitLog2) ; setProbData(probDataListForCacheHit,cacheHitLog3) ;
            setProbData(probDataListForCacheHit,cacheHitLog4) ; setProbData(probDataListForCacheHit,cacheHitLog5) ;
            setProbData(probDataListForCacheHit,cacheHitLog6) ; setProbData(probDataListForCacheHit,cacheHitLog7) ;
            setProbData(probDataListForCacheHit,cacheHitLog8) ; setProbData(probDataListForCacheHit,cacheHitLog9) ;


            Map<String,CacheKeyStatus> cacheKeyStatusMap = new HashMap<String,CacheKeyStatus>() ;
            Collections.reverse(probDataListForCacheHit) ;
            setCacheKeyStatusMap(cacheKeyStatusMap,betaRefreshStore,probDataListForCacheHit) ;

            double counterBelow35 = 0 , counterBelow60GreaterTo35 = 0, counterBelow100GreaterTo60 = 0 , counter100 = 0;

            for(Map.Entry<String,CacheKeyStatus> entry : cacheKeyStatusMap.entrySet()){
                if( entry.getValue().getPercentageBetaRefresh() < 35l ) counterBelow35++ ;
                else if( entry.getValue().getPercentageBetaRefresh() < 60l ) counterBelow60GreaterTo35++ ;
                else if( entry.getValue().getPercentageBetaRefresh() < 100l ) counterBelow100GreaterTo60++;
                else counter100++ ;
            }


            System.out.println("total beta Refresh candidate:"+cacheKeyStatusMap.size());
            System.out.println("counterBelow35:"+counterBelow35+", percentage:"+ (counterBelow35*100)/cacheKeyStatusMap.size()) ;
            System.out.println("counterBelow60GreaterTo35:"+counterBelow60GreaterTo35+", percentage:"+ (100*counterBelow60GreaterTo35)/cacheKeyStatusMap.size()) ;
            System.out.println("counterBelow100GreaterTo60:"+counterBelow100GreaterTo60+", percentage:"+ (100*counterBelow100GreaterTo60)/cacheKeyStatusMap.size()) ;
            System.out.println("counter100:"+counter100+", percentage:"+ (100*counter100)/cacheKeyStatusMap.size()) ;

            System.out.println("The end!!!");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setCacheKeyStatusMap(Map<String, CacheKeyStatus> cacheKeyStatusMap,Map<String,List<Long>> betaRefreshStore, List<ProbData> probDataListForCacheHit) {
        for(ProbData probData : probDataListForCacheHit) {
            if( null == probData.getKey() ||  !betaRefreshStore.containsKey(probData.getKey())) continue;

            if( !cacheKeyStatusMap.containsKey(probData.getKey())) {
                cacheKeyStatusMap.put(probData.getKey(),new CacheKeyStatus()) ;
            }

            long betaRefreshDiff = betaRefreshStore.get(probData.getKey()).get(0) ;
            CacheKeyStatus cacheKeyStatus = cacheKeyStatusMap.get(probData.getKey()) ;
            cacheKeyStatus.setBetaRefreshTrueConcurrentCount(betaRefreshStore.get(probData.getKey()).size());
            long totalCacheHitCount = cacheKeyStatus.getTotalCacheHitCount() ;
            if( betaRefreshDiff+500l > probData.getDiff() && betaRefreshDiff-500 < probData.getDiff()){
                long betaRefreshCacheHitCount = cacheKeyStatus.getBetaRefreshConcurrentCount() ;
                cacheKeyStatus.setBetaRefreshConcurrentCount( betaRefreshCacheHitCount+1 );
            }
            cacheKeyStatus.setTotalCacheHitCount( totalCacheHitCount+1 );
            //cacheKeyStatus.setPercentageBetaRefresh((cacheKeyStatus.getBetaRefreshConcurrentCount()*100)/cacheKeyStatus.getTotalCacheHitCount() );
            if( cacheKeyStatus.getBetaRefreshConcurrentCount() > 0 )
                cacheKeyStatus.setPercentageBetaRefresh((cacheKeyStatus.getBetaRefreshTrueConcurrentCount()*100)/cacheKeyStatus.getBetaRefreshConcurrentCount() );
        }
    }

    private void setBetaRefreshStore(Map<String,List<Long>> betaRefreshStore,List<ProbData> probDataList) {

        for(ProbData probData : probDataList){
            if( betaRefreshStore.containsKey(probData.getKey()))
                betaRefreshStore.get(probData.getKey()).add(Long.valueOf(probData.getDiff())) ;
            else {
                List<Long> list = new ArrayList<Long>() ;
                list.add(probData.getDiff()) ;
                betaRefreshStore.put(probData.getKey(),list) ;
            }
        }
    }


    private List<ProbData> getProbData(List<String> logs) {
        List<ProbData> probDataList = new ArrayList<ProbData>() ;

        for(String log : logs){
            List<String> data = Arrays.asList(log.split(" ")) ;
            if( data.size() < 3 ) continue;
            ProbData probData = new ProbData() ;

            probData.setDiff(Long.valueOf(data.get(0)));
            probData.setCurrentTime(Long.valueOf(data.get(1)));
            probData.setExpiryTime(Long.valueOf(data.get(2)));
            if( data.size() > 3 ) probData.setKey(data.get(3)+"_"+data.get(2));
            probDataList.add(probData) ;
        }
        return probDataList ;
    }

    private void setProbData(List<ProbData> probDataList,List<String> logs) {

        for(String log : logs){
            List<String> data = Arrays.asList(log.split(" ")) ;
            if( data.size() < 3 ) continue;
            ProbData probData = new ProbData() ;

            probData.setDiff(Long.valueOf(data.get(0)));
            probData.setCurrentTime(Long.valueOf(data.get(1)));
            probData.setExpiryTime(Long.valueOf(data.get(2)));
            if( data.size() > 3 ) probData.setKey(data.get(3)+"_"+data.get(2));
            probDataList.add(probData) ;
        }
    }
}
