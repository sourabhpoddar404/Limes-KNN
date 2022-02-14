package Util;

public class Util {
    public static Double euclidean(float[] vectorA, float[] vectorB) {
        double c = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            c += Math.pow((vectorA[i] - vectorB[i]),2);
        }
        return Math.sqrt(c);
    }
}
