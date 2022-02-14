package LimesKNN;

import java.util.*;

import Util.Util;

public class LimesKNN {
    private final Map<Integer, List<Map.Entry<Integer, Double>>> exemplarToWordList = new HashMap<>();
    int data_size;
    int dim;
    int k;
    Map<Integer, float[]> dataset;
    HashMap<Integer, float[]> exemplarList;
    int numExemplar;
    SortedMap<Double, Integer> sortedMap ;

    public LimesKNN(float[][] data, int dim, int n, int k, int numExemplar) {
        data_size = n;
        this.dim = dim;
        this.k = k;
        dataset = new HashMap<>();
        for (int i = 0; i < n; i++) {
            dataset.put(i, data[i]);
        }
        if(numExemplar==-1)
            this.numExemplar = (int) Math.sqrt(data_size);
        else
            this.numExemplar = numExemplar;
        buildIndex();
    }

    private void buildIndex() {
        exemplarList = new HashMap<>();
        Map<Integer, Double> distanceSum = new HashMap<>();
        long timeMillis = System.currentTimeMillis();
        int seed = (int) (timeMillis % 10000);
        int firstExemplar = new Random(seed).nextInt(data_size);
        float[] exempVec = dataset.get(firstExemplar);
        exemplarList.put(firstExemplar, exempVec);
        dataset.remove(firstExemplar);
        Map<Integer, List<String>> wordToExemplarMap = new HashMap<>();
        for (Map.Entry<Integer, float[]> vector : dataset.entrySet()) {
            Double dis = Util.euclidean(exempVec, vector.getValue());
            wordToExemplarMap.put(vector.getKey(), new ArrayList<String>() {{
                add(String.valueOf(firstExemplar));
                add(String.valueOf(dis));
            }});
            distanceSum.put(vector.getKey(), dis);
        }

        for (int i = 1; i < numExemplar; i++) {
            int key = Collections.max(distanceSum.entrySet(), Map.Entry.comparingByValue()).getKey();
            distanceSum.remove(key);

            exemplarList.put(key, dataset.get(key));
            dataset.remove(key);
            wordToExemplarMap.remove(key);
            for (Map.Entry<Integer, float[]> vector : dataset.entrySet()) {
                double dis = Util.euclidean(exemplarList.get(key), vector.getValue());
                distanceSum.put(vector.getKey(), distanceSum.get(vector.getKey()) + dis);
                if (dis < Double.parseDouble(wordToExemplarMap.get(vector.getKey()).get(1)))
                    wordToExemplarMap.put(vector.getKey(), new ArrayList<String>() {{
                        add(String.valueOf(key));
                        add(String.valueOf(dis));
                    }});
            }
        }
        distanceSum.clear();
        Map<Integer, Map<Integer, Double>> exemplarToWordMap = new HashMap<>();
        for (Map.Entry<Integer, List<String>> word : wordToExemplarMap.entrySet()) {
            int exemplar = Integer.parseInt(word.getValue().get(0));
            if (exemplarToWordMap.containsKey(exemplar)) {
                Map<Integer, Double> temp = exemplarToWordMap.get(exemplar);
                temp.put(word.getKey(), Double.parseDouble(word.getValue().get(1)));
                exemplarToWordMap.put(exemplar, temp);

            } else {
                Map<Integer, Double> temp = new HashMap<>();
                temp.put(word.getKey(), Double.parseDouble(word.getValue().get(1)));
                exemplarToWordMap.put(exemplar, temp);
            }
        }
        wordToExemplarMap.clear();
        for (int key : exemplarToWordMap.keySet()) {
            List<Map.Entry<Integer, Double>> list =
                    new LinkedList<>(exemplarToWordMap.get(key).entrySet());
            list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
            exemplarToWordList.put(key, list);
        }
        for (Map.Entry<Integer,float[]> entry : exemplarList.entrySet())
        {
            if(!exemplarToWordList.containsKey(entry.getKey()))
                exemplarToWordList.put(entry.getKey(),new ArrayList<>());
        }
    }

    public Map<Double, Integer> getKNN(float[] vector, int k) {
        double min = Float.MAX_VALUE;
        int minKey = -1;
        sortedMap = new TreeMap();
        sortedMap.put(min,minKey);
        for (int key : exemplarToWordList.keySet()) {
            double dis = Util.euclidean(exemplarList.get(key), vector);
            if (dis < sortedMap.lastKey()) {
                insertnew(dis,key);
            }
            for (Map.Entry<Integer, Double> Entry : exemplarToWordList.get(key)) {
                if ((dis - Entry.getValue()) < sortedMap.lastKey()) {
                    double newDis = Util.euclidean(vector, dataset.get(Entry.getKey()));
                    if (newDis < sortedMap.lastKey()) {
                        insertnew(newDis,Entry.getKey());
                    }
                } else {
                    break;
                }
            }
        }
        return sortedMap;
    }

    public Map<Double,Integer> getKNNWithDistance(float[] vector, int k) {
       return getKNN(vector,k);
    }

    public List<Integer> getKNNWithoutDistance(float[] vector, int k) {
        Map<Double, Integer> result = getKNN(vector,k);
        List<Integer> resultList = new ArrayList<>();
        for(Map.Entry<Double,Integer> entry : result.entrySet())
        {
            resultList.add(entry.getValue());
        }
       return resultList;
    }

    private void insertnew(double dis, int key) {
        sortedMap.put(dis,key);
        if(sortedMap.size()>k)
            sortedMap.remove(sortedMap.lastKey());
    }

}
