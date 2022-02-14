import LimesKNN.LimesKNN;

import java.io.*;
import java.util.*;

public class Application {
    static private Properties prop = new Properties();
    private static float[][] data;
    private static float[][] queries;
    private static int[][] gndTruth;
    private static int n;
    private static int d;
    private static int nq;
    private static int k;
    private static int exemplar;

    public static void main(String arg[]) {
        try (
                InputStream inputStream = ClassLoader.getSystemResourceAsStream("config.properties")) {

            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(arg.length != 0)
            exemplar = Integer.parseInt(arg[0]);
        else
            exemplar = -1;
        System.out.println(exemplar);
        intializeParamters();
        LimesKNN limesKNN = new LimesKNN(data, d, n, k, exemplar);
        double recall = 0.0;
        long totaltime = 0;
        for (int i = 0;i<nq;i++) {
            long start = System.currentTimeMillis();
            List<Integer> result = limesKNN.getKNNWithoutDistance(queries[i], k);
            long end = System.currentTimeMillis();
            totaltime+=(end - start);
            for(int j =0;j<k;j++)
            {
                if(result.contains(gndTruth[i][j]))
                    recall+=1;
                else
                    System.out.println(i +"  " + j);
            }
        }
        recall= recall/(nq*k);
        System.out.println("recall: " + recall);
        double avgTime = (float)totaltime/(nq*1000);
        System.out.println("Average Time: " + avgTime);


    }

    private static void intializeParamters() {
        String dataPath = prop.getProperty("dataPath");
        String queryPath = prop.getProperty("queryPath");
        String gndPath = prop.getProperty("gndPath");
        n = Integer.parseInt(prop.getProperty("n"));
        d = Integer.parseInt(prop.getProperty("d"));
        nq = Integer.parseInt(prop.getProperty("nq"));
        k = Integer.parseInt(prop.getProperty("k"));

        data = new float[n][d];
        queries = new float[nq][d];
        gndTruth = new int[nq][k];
        try {

            BufferedReader br = new BufferedReader(new FileReader(dataPath));
            String line;

            for (int l = 0; l < n; l++) {
                line = br.readLine();
                String temp[] = line.split(" ");
                for (int i = 0; i < d; i++) {
                    data[l][i] = Float.parseFloat(temp[i]);
                }
            }
            br = new BufferedReader(new FileReader(queryPath));

            for (int l = 0; l < nq; l++) {
                line = br.readLine();
                String temp[] = line.split(" ");
                for (int i = 0; i < d; i++) {
                    queries[l][i] = Float.parseFloat(temp[i]);
                }
            }
            br = new BufferedReader(new FileReader(gndPath));

            for (int l = 0; l < nq; l++) {
                line = br.readLine();
                String temp[] = line.split(" ");
                for (int i = 0; i < k; i++) {
                    gndTruth[l][i] = Integer.parseInt(temp[i]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
