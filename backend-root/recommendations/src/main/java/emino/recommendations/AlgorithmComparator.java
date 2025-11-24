package emino.recommendations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;

import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;
import smile.data.vector.ValueVector;
import smile.io.Read;
import smile.io.Write;
import smile.regression.GradientTreeBoost;
import smile.regression.RandomForest;
import smile.regression.RegressionTree;

public class AlgorithmComparator {
    public void setUp() throws IOException {
        DataFrame df = loadCSV();
        
        int n = df.nrow();
        int trainSize = (int) (n * 0.8);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) indices.add(i);
        Collections.shuffle(indices);
        
        List<Integer> trainIndices = indices.subList(0, trainSize);
        List<Integer> testIndices = indices.subList(trainSize, n);
        
        int[] trainIdx = trainIndices.stream().mapToInt(Integer::intValue).toArray();
        int[] testIdx = testIndices.stream().mapToInt(Integer::intValue).toArray();
        
        DataFrame trainDf = createDataFrameFromIndices(df, trainIdx);
        DataFrame testDf = createDataFrameFromIndices(df, testIdx);
        
        Formula formula = Formula.lhs("effectiveness_score");
        
        RegressionTree rt = RegressionTree.fit(formula, trainDf);
        RandomForest rf = RandomForest.fit(formula, trainDf);
        Write.object(rf, Paths.get("random_forest_model.bin"));
        GradientTreeBoost gbt = GradientTreeBoost.fit(formula, trainDf);
    
        double[] yTrueTrain = trainDf.column("effectiveness_score").toDoubleArray();
        double[] yPredRTTrain = rt.predict(trainDf);
        double[] yPredRFTrain = rf.predict(trainDf);
        double[] yPredGBTTrain = gbt.predict(trainDf);
    
        double[] yTrueTest = testDf.column("effectiveness_score").toDoubleArray();
        double[] yPredRTTest = rt.predict(testDf);
        double[] yPredRFTest = rf.predict(testDf);
        double[] yPredGBTTest = gbt.predict(testDf);
    
        double mseRTrain = mse(yTrueTrain, yPredRTTrain);
        double mseRFTrain = mse(yTrueTrain, yPredRFTrain);
        double mseGBTTrain = mse(yTrueTrain, yPredGBTTrain);
        
        double rmseRTrain = Math.sqrt(mseRTrain);
        double rmseRFTrain = Math.sqrt(mseRFTrain);
        double rmseGBTTrain = Math.sqrt(mseGBTTrain);
        
        double r2RTrain = r2Score(yTrueTrain, yPredRTTrain);
        double r2RFTrain = r2Score(yTrueTrain, yPredRFTrain);
        double r2GBTTrain = r2Score(yTrueTrain, yPredGBTTrain);
    
        double mseRTest = mse(yTrueTest, yPredRTTest);
        double mseRFTest = mse(yTrueTest, yPredRFTest);
        double mseGBTTest = mse(yTrueTest, yPredGBTTest);
        
        double rmseRTest = Math.sqrt(mseRTest);
        double rmseRFTest = Math.sqrt(mseRFTest);
        double rmseGBTTest = Math.sqrt(mseGBTTest);
        
        double r2RTest = r2Score(yTrueTest, yPredRTTest);
        double r2RFTest = r2Score(yTrueTest, yPredRFTest);
        double r2GBTTest = r2Score(yTrueTest, yPredGBTTest);
    
        System.out.println("\n===== DANE TRENINGOWE =====");
        printMetrics("Regression Tree", mseRTrain, rmseRTrain, r2RTrain);
        printMetrics("Random Forest", mseRFTrain, rmseRFTrain, r2RFTrain);
        printMetrics("Gradient Boosted Trees", mseGBTTrain, rmseGBTTrain, r2GBTTrain);
    
        System.out.println("\n===== DANE TESTOWE =====");
        printMetrics("Regression Tree", mseRTest, rmseRTest, r2RTest);
        printMetrics("Random Forest", mseRFTest, rmseRFTest, r2RFTest);
        printMetrics("Gradient Boosted Trees", mseGBTTest, rmseGBTTest, r2GBTTest);
    
        System.out.println("\n===== ANALIZA PRZEUCZENIA =====");
        System.out.printf("RT - RMSE (train-test): %.4f %n", rmseRTrain - rmseRTest);
        System.out.printf("RF - RMSE (train-test): %.4f %n", rmseRFTrain - rmseRFTest);
        System.out.printf("GBT - RMSE (train-test): %.4f %n", rmseGBTTrain - rmseGBTTest);
    
        int[] symptomIdsAll = testDf.column("symptom_id").toIntArray();
        int[] massageIdsAll = testDf.column("massage_id").toIntArray();
    
        Set<Integer> uniqueSymptoms = Arrays.stream(symptomIdsAll).boxed().collect(Collectors.toSet());
    
        List<Integer> symptomCol = new ArrayList<>();
        List<Integer> massageCol = new ArrayList<>();
        List<Double> rtCol = new ArrayList<>();
        List<Double> rfCol = new ArrayList<>();
        List<Double> gbtCol = new ArrayList<>();
        List<Integer> rtRankCol = new ArrayList<>();
        List<Integer> rfRankCol = new ArrayList<>();
        List<Integer> gbtRankCol = new ArrayList<>();
    
        int testSize = testDf.nrow();
    
        for (int symptom : uniqueSymptoms) {
            List<Integer> massageIds = new ArrayList<>();
            List<Double> rtScores = new ArrayList<>();
            List<Double> rfScores = new ArrayList<>();
            List<Double> gbtScores = new ArrayList<>();
    
            for (int i = 0; i < testSize; i++) {
                if (symptomIdsAll[i] == symptom) {
                    massageIds.add(massageIdsAll[i]);
                    rtScores.add(yPredRTTest[i]);
                    rfScores.add(yPredRFTest[i]);
                    gbtScores.add(yPredGBTTest[i]);
                }
            }
    
            List<Double> sortedRT = new ArrayList<>(rtScores);
            List<Double> sortedRF = new ArrayList<>(rfScores);
            List<Double> sortedGBT = new ArrayList<>(gbtScores);
            sortedRF.sort(Collections.reverseOrder());
            sortedGBT.sort(Collections.reverseOrder());
    
            for (int i = 0; i < massageIds.size(); i++) {
                symptomCol.add(symptom);
                massageCol.add(massageIds.get(i));
                rtCol.add(rtScores.get(i));
                rfCol.add(rfScores.get(i));
                gbtCol.add(gbtScores.get(i));
                rtRankCol.add(sortedRT.indexOf(rtScores.get(i)) + 1);
                rfRankCol.add(sortedRF.indexOf(rfScores.get(i)) + 1);
                gbtRankCol.add(sortedGBT.indexOf(gbtScores.get(i)) + 1);
            }
        }
    
        int[] symptomArr = symptomCol.stream().mapToInt(Integer::intValue).toArray();
        int[] massageArr = massageCol.stream().mapToInt(Integer::intValue).toArray();
        double[] rtArr = rtCol.stream().mapToDouble(Double::doubleValue).toArray();
        double[] rfArr = rfCol.stream().mapToDouble(Double::doubleValue).toArray();
        double[] gbtArr = gbtCol.stream().mapToDouble(Double::doubleValue).toArray();
        int[] rtRankArr = rtRankCol.stream().mapToInt(Integer::intValue).toArray();
        int[] rfRankArr = rfRankCol.stream().mapToInt(Integer::intValue).toArray();
        int[] gbtRankArr = gbtRankCol.stream().mapToInt(Integer::intValue).toArray();
    
        DataFrame rankingDf = new DataFrame(
                new IntVector("symptom_id", symptomArr),
                new IntVector("massage_id", massageArr),
                new DoubleVector("RT_predicted", rtArr),
                new IntVector("RT_rank", rtRankArr),
                new DoubleVector("RF_predicted", rfArr),
                new IntVector("RF_rank", rfRankArr),
                new DoubleVector("GBT_predicted", gbtArr),
                new IntVector("GBT_rank", gbtRankArr)
        );
    
        Path resultPath = Paths.get("ranking_test_data.csv");
        Write.csv(rankingDf, resultPath);
    
        int targetSymptom = symptomIdsAll[0];
    
        List<Integer> massageIds = new ArrayList<>();
        List<Double> rtScores = new ArrayList<>();
        List<Double> rfScores = new ArrayList<>();
        List<Double> gbtScores = new ArrayList<>();
    
        for (int i = 0; i < testSize; i++) {
            if (symptomIdsAll[i] == targetSymptom) {
                massageIds.add(massageIdsAll[i]);
                rtScores.add(yPredRTTest[i]);
                rfScores.add(yPredRFTest[i]);
                gbtScores.add(yPredGBTTest[i]);
            }
        }
    
        System.out.println("\n=====REKOMENDACJE DLA SYMPTOMU: " + targetSymptom+" =====");
        printRanking("Regression Tree", massageIds, rtScores);
        printRanking("Random Forest", massageIds, rfScores);
        printRanking("Gradient Boosted Trees", massageIds, gbtScores);

        System.out.println("\n===== TEST DATA SAMPLE =====");
        for (int i = 0; i < Math.min(16, testSize); i++) {
            System.out.printf("Row %d: symptom_id=%d, massage_id=%d, effectiveness_score=%.2f%n",
                i, 
                ((Number) testDf.get(i).get("symptom_id")).intValue(),
                ((Number) testDf.get(i).get("massage_id")).intValue(),
                ((Number) testDf.get(i).get("effectiveness_score")).doubleValue()
            );
        }
    }
    
    public DataFrame loadCSV() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream input = cl.getResourceAsStream("massage_effectiveness.csv");
        if (input == null) {
            throw new RuntimeException("CSV file not found in resources!");
        }
        
        Path tempPath = Files.createTempFile("massage_effectiveness", ".csv");
        Files.copy(input, tempPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        CSVFormat format = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .get();
        return Read.csv(tempPath, format);
    }

    private static DataFrame createDataFrameFromIndices(DataFrame df, int[] indices) {
        List<Integer> rows = new ArrayList<>();
        for (int idx : indices) {
            rows.add(idx);
        }
        
        List<String> colNames = new ArrayList<>();
        for (int i = 0; i < df.ncol(); i++) {
            colNames.add(df.names()[i]);
        }
        
        Map<String, List<?>> colData = new LinkedHashMap<>();
        for (String colName : colNames) {
            colData.put(colName, new ArrayList<>());
        }
        
        for (int idx : indices) {
            for (int j = 0; j < df.ncol(); j++) {
                String colName = df.names()[j];
                Object value = df.get(idx, j);
                ((List) colData.get(colName)).add(value);
            }
        }
        
        ValueVector[] vectors = new ValueVector[df.ncol()];
        for (int j = 0; j < df.ncol(); j++) {
            String colName = df.names()[j];
            List<?> data = colData.get(colName);
            
            if (df.column(j) instanceof smile.data.vector.DoubleVector) {
                double[] arr = new double[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    arr[i] = ((Number) data.get(i)).doubleValue();
                }
                vectors[j] = new smile.data.vector.DoubleVector(colName, arr);
            } else if (df.column(j) instanceof smile.data.vector.IntVector) {
                int[] arr = new int[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    arr[i] = ((Number) data.get(i)).intValue();
                }
                vectors[j] = new smile.data.vector.IntVector(colName, arr);
            } else {
                String[] arr = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    arr[i] = data.get(i).toString();
                }
                vectors[j] = new smile.data.vector.StringVector(colName, arr);
            }
        }
        
        return new DataFrame(vectors);
    }
    
    private static double mse(double[] yTrue, double[] yPred) {
        return IntStream.range(0, yTrue.length)
                .mapToDouble(i -> Math.pow(yPred[i] - yTrue[i], 2))
                .average()
                .orElse(0.0);
    }
    
    private static double r2Score(double[] yTrue, double[] yPred) {
        double meanY = Arrays.stream(yTrue).average().orElse(0.0);
        
        double ssRes = IntStream.range(0, yTrue.length)
                .mapToDouble(i -> Math.pow(yTrue[i] - yPred[i], 2))
                .sum();
        
        double ssTot = IntStream.range(0, yTrue.length)
                .mapToDouble(i -> Math.pow(yTrue[i] - meanY, 2))
                .sum();
        
        return 1.0 - (ssRes / ssTot);
    }

    private static void printMetrics(String modelName, double mse, double rmse, double r2) {
        System.out.println("\n--- " + modelName + " ---");
        System.out.printf("MSE:  %.4f%n", mse);
        System.out.printf("RMSE: %.4f%n", rmse);
        System.out.printf("R²:   %.4f%n", r2);
    }
    
    private static void printRanking(String modelName, List<Integer> ids, List<Double> scores) {
        List<Double> sortedScores = new ArrayList<>(scores);
        sortedScores.sort(Collections.reverseOrder());
    
        System.out.println("\n--- " + modelName + " ---");
        for (int i = 0; i < ids.size(); i++) {
            int rank = sortedScores.indexOf(scores.get(i)) + 1;
            System.out.printf("Massage %d -> %.4f (Rank %d)%n", ids.get(i), scores.get(i), rank);
        }
    }
}
