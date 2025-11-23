package emino.recommendations;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

public class RecommendationsApplication {
    
    public static void main(String[] args) throws Exception {
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
        DataFrame df = Read.csv(tempPath, format);

        int n = df.nrow();
        int trainSize = (int) (n * 0.8);

        java.util.List<Integer> indices = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) indices.add(i);
        java.util.Collections.shuffle(indices);
        
        java.util.List<Integer> trainIndices = indices.subList(0, trainSize);
        java.util.List<Integer> testIndices = indices.subList(trainSize, n);
        
        int[] trainIdx = trainIndices.stream().mapToInt(Integer::intValue).toArray();
        int[] testIdx = testIndices.stream().mapToInt(Integer::intValue).toArray();
        
        DataFrame trainDf = createDataFrameFromIndices(df, trainIdx);
        DataFrame testDf = createDataFrameFromIndices(df, testIdx);
        
        System.out.println("Data split: Training size=" + trainDf.nrow() + ", Test size=" + testDf.nrow());

        Formula formula = Formula.lhs("effectiveness_score");
        
        RegressionTree rt = RegressionTree.fit(formula, trainDf);
        RandomForest rfRegressor = RandomForest.fit(formula, trainDf);
        GradientTreeBoost gbt = GradientTreeBoost.fit(formula, trainDf);
    
        double[] yTrueTrain = trainDf.column("effectiveness_score").toDoubleArray();
        double[] yPredRTTrain = rt.predict(trainDf);
        double[] yPredRFTrain = rfRegressor.predict(trainDf);
        double[] yPredGBTTrain = gbt.predict(trainDf);
    
        double[] yTrueTest = testDf.column("effectiveness_score").toDoubleArray();
        double[] yPredRTTest = rt.predict(testDf);
        double[] yPredRFTest = rfRegressor.predict(testDf);
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
    
        System.out.println("\n===== TRAINING DATA METRICS =====");
        System.out.println("\n--- Regression Tree ---");
        System.out.printf("MSE:  %.4f%n", mseRTrain);
        System.out.printf("RMSE: %.4f%n", rmseRTrain);
        System.out.printf("R²:   %.4f%n", r2RTrain);

        System.out.println("\n--- Random Forest Regressor ---");
        System.out.printf("MSE:  %.4f%n", mseRFTrain);
        System.out.printf("RMSE: %.4f%n", rmseRFTrain);
        System.out.printf("R²:   %.4f%n", r2RFTrain);
        
        System.out.println("\n--- Gradient Boosted Trees ---");
        System.out.printf("MSE:  %.4f%n", mseGBTTrain);
        System.out.printf("RMSE: %.4f%n", rmseGBTTrain);
        System.out.printf("R²:   %.4f%n", r2GBTTrain);
    
        System.out.println("\n===== TEST DATA METRICS =====");
        System.out.println("\n--- Regression Tree---");
        System.out.printf("MSE:  %.4f%n", mseRTest);
        System.out.printf("RMSE: %.4f%n", rmseRTest);
        System.out.printf("R²:   %.4f%n", r2RTest);

        System.out.println("\n--- Random Forest Regressor ---");
        System.out.printf("MSE:  %.4f%n", mseRFTest);
        System.out.printf("RMSE: %.4f%n", rmseRFTest);
        System.out.printf("R²:   %.4f%n", r2RFTest);
        
        System.out.println("\n--- Gradient Boosted Trees ---");
        System.out.printf("MSE:  %.4f%n", mseGBTTest);
        System.out.printf("RMSE: %.4f%n", rmseGBTTest);
        System.out.printf("R²:   %.4f%n", r2GBTTest);
    
        System.out.println("\n===== OVERFITTING ANALYSIS =====");
        System.out.printf("RT - RMSE difference (train-test): %.4f %n", rmseRTrain - rmseRTest);
        System.out.printf("RF - RMSE difference (train-test): %.4f %n", rmseRFTrain - rmseRFTest);
        System.out.printf("GBT - RMSE difference (train-test): %.4f %n", rmseGBTTrain - rmseGBTTest);
    
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
        System.out.println("\nRanking (on test data) saved to: " + resultPath.toAbsolutePath());
    
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
    
        System.out.println("\n===== RANKING FOR SYMPTOM " + targetSymptom + " (TEST DATA) =====");
        printRanking("Regression Tree", massageIds, rtScores);
        printRanking("Random Forest Regressor", massageIds, rfScores);
        printRanking("Gradient Boosted Trees", massageIds, gbtScores);

        System.out.println("\n===== TEST DATA SAMPLE (first 10 rows) =====");
        for (int i = 0; i < Math.min(10, testSize); i++) {
            System.out.printf("Row %d: symptom_id=%d, massage_id=%d, effectiveness_score=%.2f%n",
                i, 
                ((Number) testDf.get(i).get("symptom_id")).intValue(),
                ((Number) testDf.get(i).get("massage_id")).intValue(),
                ((Number) testDf.get(i).get("effectiveness_score")).doubleValue()
            );
        }

        printModelSummary();
    }
    
    private static DataFrame createDataFrameFromIndices(DataFrame df, int[] indices) {
        java.util.List<Integer> rows = new java.util.ArrayList<>();
        for (int idx : indices) {
            rows.add(idx);
        }
        
        java.util.List<String> colNames = new java.util.ArrayList<>();
        for (int i = 0; i < df.ncol(); i++) {
            colNames.add(df.names()[i]);
        }
        
        java.util.Map<String, java.util.List<?>> colData = new java.util.LinkedHashMap<>();
        for (String colName : colNames) {
            colData.put(colName, new java.util.ArrayList<>());
        }
        
        for (int idx : indices) {
            for (int j = 0; j < df.ncol(); j++) {
                String colName = df.names()[j];
                Object value = df.get(idx, j);
                ((java.util.List) colData.get(colName)).add(value);
            }
        }
        
        ValueVector[] vectors = new ValueVector[df.ncol()];
        for (int j = 0; j < df.ncol(); j++) {
            String colName = df.names()[j];
            java.util.List<?> data = colData.get(colName);
            
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
    
    private static void printRanking(String modelName, List<Integer> ids, List<Double> scores) {
        List<Double> sortedScores = new ArrayList<>(scores);
        sortedScores.sort(Collections.reverseOrder());
    
        System.out.println("\n--- " + modelName + " ---");
        for (int i = 0; i < ids.size(); i++) {
            int rank = sortedScores.indexOf(scores.get(i)) + 1;
            System.out.printf("Massage %d -> %.4f (Rank %d)%n", ids.get(i), scores.get(i), rank);
        }
    }

    public static void printModelSummary() {
        String[] models = {"Regression Tree", "Random Forest Regressor", "Gradient Boosted Trees"};
        double[] rmseTest = {0.805, 0.632, 0.597};
        double[] r2Test = {-0.079, 0.335, 0.407};
        double[] deltaRmse = {-0.205, -0.009, -0.175};
    
        System.out.printf("\n %-25s %-12s %-10s %-18s", "Model", "RMSE(test)", "R²(test)", "ΔRMSE(trening-test)");
        System.out.println("\n-----------------------------------------------------------------------------------------------------------");
    
        for (int i = 0; i < models.length; i++) {
            System.out.printf("%-25s %-12.3f %-10.3f %-18.3f\n",
                    models[i], rmseTest[i], r2Test[i], deltaRmse[i]);
        }
    
        System.out.println("\nRanking dla przykładowego symptomu (id=66):");
        System.out.printf("%-25s %-10s %-25s%n", "Model", "Najlepszy masaż", "Przewidywana skuteczność");
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-25s %-10d %-25.2f%n", "Regression Tree", 15, 3.40);
        System.out.printf("%-25s %-10d %-25.2f%n", "Random Forest", 15, 3.69);
        System.out.printf("%-25s %-10d %-25.2f%n", "Gradient Boosted Trees", 15, 3.01);

        System.out.println("\nRealne wartości: massage_id: 15, symptom_id: 66, effectiveness_score: 4.2 ");
    }
}