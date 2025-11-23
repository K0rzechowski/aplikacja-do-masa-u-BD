package emino.recommendations;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;

import static smile.base.cart.SplitRule.GINI;
import smile.classification.DecisionTree;
import smile.classification.KNN;
import smile.classification.RandomForest;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.vector.IntVector;
import smile.io.Read;

public class RecommendationsApplication {

    public static void main(String[] args) throws Exception {
        // Load CSV
        DataFrame df = loadCSV();
        
        // Extract data
        int n = df.nrow();
        int[] symptomIds = df.column("symptom_id").toIntArray();
        double[] eff = df.column("effectiveness_score").toDoubleArray();
        int[] massageIds = df.column("massage_id").toIntArray();
        
        // Convert scores to classes: 0=low, 1=medium, 2=high
        int[] labels = convertToClasses(eff);
        
        // Split into train/test (80/20)
        TrainTestSplit split = splitData(n, labels, symptomIds, massageIds, eff);
        
        // Train and evaluate models
        evaluateKNN(split);
        evaluateDecisionTree(split);
        evaluateRandomForest(split);
        
        // Show recommendations for target symptom
        showRecommendations(split);
    }
    
    private static DataFrame loadCSV() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream input = cl.getResourceAsStream("massage_effectiveness.csv");
        if (input == null) throw new RuntimeException("CSV file not found in resources!");
        
        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();
        
        Path tempPath = Files.createTempFile("massage_effectiveness", ".csv");
        Files.copy(input, tempPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        return Read.csv(tempPath, format);
    }
    
    private static int[] convertToClasses(double[] scores) {
        int[] labels = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] < 3) labels[i] = 0;
            else if (scores[i] < 4.5) labels[i] = 1;
            else labels[i] = 2;
        }
        return labels;
    }
    
    private static TrainTestSplit splitData(int n, int[] labels, int[] symptomIds, int[] massageIds, double[] eff) {
        double testRatio = 0.2;

        // --- 1. Group indices by class ---
        Map<Integer, List<Integer>> perClass = new HashMap<>();
        for (int i = 0; i < n; i++) {
            perClass.computeIfAbsent(labels[i], k -> new ArrayList<>()).add(i);
        }

        List<Integer> trainList = new ArrayList<>();
        List<Integer> testList = new ArrayList<>();

        Random rand = new Random(42);

        // --- 2. Stratified sampling ---
        for (var entry : perClass.entrySet()) {
            List<Integer> indices = entry.getValue();
            Collections.shuffle(indices, rand);

            int testSize = (int) Math.round(indices.size() * testRatio);

            testList.addAll(indices.subList(0, testSize));
            trainList.addAll(indices.subList(testSize, indices.size()));
        }

        // Convert lists → arrays
        int trainSize = trainList.size();
        int testSize = testList.size();

        int[] trainIdx = trainList.stream().mapToInt(i -> i).toArray();
        int[] testIdx = testList.stream().mapToInt(i -> i).toArray();

        // --- 3. Build train feature matrix ---
        double[][] Xtrain = new double[trainSize][3];
        int[] labelsTrain = new int[trainSize];
        int[] trainSymptomIds = new int[trainSize];
        int[] trainMassageIds = new int[trainSize];

        for (int i = 0; i < trainSize; i++) {
            int idx = trainIdx[i];
            Xtrain[i][0] = symptomIds[idx];
            Xtrain[i][1] = massageIds[idx];
            Xtrain[i][2] = eff[idx];
            labelsTrain[i] = labels[idx];
            trainSymptomIds[i] = symptomIds[idx];
            trainMassageIds[i] = massageIds[idx];
        }

        // --- 4. Build test feature matrix ---
        double[][] Xtest = new double[testSize][3];
        int[] labelsTest = new int[testSize];
        int[] testSymptomIds = new int[testSize];
        int[] testMassageIds = new int[testSize];

        for (int i = 0; i < testSize; i++) {
            int idx = testIdx[i];
            Xtest[i][0] = symptomIds[idx];
            Xtest[i][1] = massageIds[idx];
            Xtest[i][2] = eff[idx];
            labelsTest[i] = labels[idx];
            testSymptomIds[i] = symptomIds[idx];
            testMassageIds[i] = massageIds[idx];
        }
        System.out.printf("Overall class counts: 0=%d, 1=%d, 2=%d%n",
            Arrays.stream(labels).filter(x -> x==0).count(),
            Arrays.stream(labels).filter(x -> x==1).count(),
            Arrays.stream(labels).filter(x -> x==2).count()
        );

        System.out.printf("TRAIN class counts: 0=%d, 1=%d, 2=%d%n",
            Arrays.stream(labelsTrain).filter(x -> x==0).count(),
            Arrays.stream(labelsTrain).filter(x -> x==1).count(),
            Arrays.stream(labelsTrain).filter(x -> x==2).count()
        );

        System.out.printf("TEST class counts: 0=%d, 1=%d, 2=%d%n",
            Arrays.stream(labelsTest).filter(x -> x==0).count(),
            Arrays.stream(labelsTest).filter(x -> x==1).count(),
            Arrays.stream(labelsTest).filter(x -> x==2).count()
        );

        
        return new TrainTestSplit(Xtrain, labelsTrain, Xtest, labelsTest, 
                                  trainSymptomIds, trainMassageIds, testSymptomIds, testMassageIds);
    }
    
    private static void evaluateKNN(TrainTestSplit split) {
        KNN<double[]> knn = KNN.fit(split.Xtrain, split.labelsTrain, 3);
        int[] predictions = knn.predict(split.Xtest);
        double acc = accuracy(split.labelsTest, predictions);
        double f1 = f1Score(split.labelsTest, predictions);
        System.out.printf("Accuracy kNN: %.4f%n | F1: %.4f%n", acc, f1);
        classificationReport(split.labelsTest, predictions);
    }
    
    private static void evaluateDecisionTree(TrainTestSplit split) {
        DataFrame dfTrain = createDataFrame(split.trainSymptomIds, split.trainMassageIds, split.labelsTrain);
        DataFrame dfTest = createDataFrame(split.testSymptomIds, split.testMassageIds, split.labelsTest);
        
        Formula formula = Formula.lhs("class_label");
        DecisionTree.Options options = new DecisionTree.Options(GINI, 5, 30, 5);
        DecisionTree dt = DecisionTree.fit(formula, dfTrain, options);
        
        int[] predictions = dt.predict(dfTest);
        double acc = accuracy(split.labelsTest, predictions);
        double f1 = f1Score(split.labelsTest, predictions);
        System.out.printf("Accuracy Decision Tree: %.4f%n | F1: %.4f%n", acc, f1);
        classificationReport(split.labelsTest, predictions);
    }
    
    private static void evaluateRandomForest(TrainTestSplit split) {
        DataFrame dfTrain = createDataFrame(split.trainSymptomIds, split.trainMassageIds, split.labelsTrain);
        DataFrame dfTest = createDataFrame(split.testSymptomIds, split.testMassageIds, split.labelsTest);
        
        Formula formula = Formula.lhs("class_label");
        RandomForest.Options options = new RandomForest.Options(200);
        RandomForest rf = RandomForest.fit(formula, dfTrain, options);
        
        int[] predictions = rf.predict(dfTest);
        int[] predictionsInt = predictions;
        double acc = accuracy(split.labelsTest, predictionsInt);
        double f1 = f1Score(split.labelsTest, predictions);
        System.out.printf("Accuracy RandomForest: %.4f%n | F1: %.4f%n", acc, f1);
        classificationReport(split.labelsTest, predictions);
    }
    
    private static void showRecommendations(TrainTestSplit split) {
        // Find available symptoms in test data
        Set<Integer> availableSymptoms = new HashSet<>();
        for (int symptomId : split.testSymptomIds) {
            availableSymptoms.add(symptomId);
        }
        
        System.out.println("\nAvailable symptoms in test data: " + availableSymptoms);
            
        // Train final DT on test data for recommendations
        DataFrame dfTest = createDataFrame(split.testSymptomIds, split.testMassageIds, split.labelsTest);
        Formula formula = Formula.lhs("class_label");
        DecisionTree.Options options = new DecisionTree.Options(GINI, 5, 10, 5);
        DecisionTree dt = DecisionTree.fit(formula, dfTest, options);
        
        int[] predictions = dt.predict(dfTest);

        for (int targetSymptom : availableSymptoms) {
            Map<Integer, List<Integer>> massagesByClass = new HashMap<>();
            for (int i = 0; i < split.testSymptomIds.length; i++) {
                if (split.testSymptomIds[i] == targetSymptom) {
                    int cls = predictions[i];
                    massagesByClass.computeIfAbsent(cls, k -> new ArrayList<>())
                        .add(split.testMassageIds[i]);
                }
            }

            System.out.println("\nDecision Tree - massage recommendations for symptom_id = " + targetSymptom);
            if (massagesByClass.isEmpty()) {
                System.out.println("No massages found for this symptom in test data");
            } else {
                massagesByClass.forEach((cls, massages) -> 
                    System.out.printf("Class %d (effectiveness): %s%n", cls, massages)
                );
            }
        }
    }
    
    private static DataFrame createDataFrame(int[] symptomIds, int[] massageIds, int[] labels) {
        return new DataFrame(
            new IntVector("symptom_id", symptomIds),
            new IntVector("massage_id", massageIds),
            new IntVector("class_label", labels)
        );
    }
    
    private static double accuracy(int[] trueLabels, int[] predicted) {
        if (trueLabels.length != predicted.length) {
            throw new IllegalArgumentException("Array lengths don't match!");
        }
        
        int correct = 0;
        for (int i = 0; i < trueLabels.length; i++) {
            if (trueLabels[i] == predicted[i]) correct++;
        }
        return (double) correct / trueLabels.length;
    }

    private static double f1Score(int[] trueLabels, int[] predicted) {
        if (trueLabels.length != predicted.length)
            throw new IllegalArgumentException("Array lengths don't match!");
    
        int tp = 0, fp = 0, fn = 0;
    
        // Macro-averaged over 3 classes (0,1,2)
        double f1Sum = 0;
    
        for (int c = 0; c < 3; c++) {
            tp = fp = fn = 0;
    
            for (int i = 0; i < trueLabels.length; i++) {
                if (predicted[i] == c && trueLabels[i] == c) tp++;
                else if (predicted[i] == c && trueLabels[i] != c) fp++;
                else if (predicted[i] != c && trueLabels[i] == c) fn++;
            }
    
            double precision = tp + fp == 0 ? 0 : (double) tp / (tp + fp);
            double recall = tp + fn == 0 ? 0 : (double) tp / (tp + fn);
            double f1 = (precision + recall == 0) ? 0 : (2 * precision * recall) / (precision + recall);
    
            f1Sum += f1;
        }
    
        return f1Sum / 3.0;  // macro F1
    }
    
    
    // Helper class for train/test data
    private static class TrainTestSplit {
        double[][] Xtrain, Xtest;
        int[] labelsTrain, labelsTest;
        int[] trainSymptomIds, trainMassageIds;
        int[] testSymptomIds, testMassageIds;
        
        TrainTestSplit(double[][] Xtrain, int[] labelsTrain, double[][] Xtest, int[] labelsTest,
                      int[] trainSymptomIds, int[] trainMassageIds, int[] testSymptomIds, int[] testMassageIds) {
            this.Xtrain = Xtrain;
            this.Xtest = Xtest;
            this.labelsTrain = labelsTrain;
            this.labelsTest = labelsTest;
            this.trainSymptomIds = trainSymptomIds;
            this.trainMassageIds = trainMassageIds;
            this.testSymptomIds = testSymptomIds;
            this.testMassageIds = testMassageIds;
        }
    }

    private static void classificationReport(int[] trueLabels, int[] predicted) {
        if (trueLabels.length != predicted.length)
            throw new IllegalArgumentException("Array lengths don't match!");
    
        int n = trueLabels.length;
        int numClasses = 3; // change if you have more
        int[][] cm = new int[numClasses][numClasses];
    
        for (int i = 0; i < n; i++) {
            cm[trueLabels[i]][predicted[i]]++;
        }
    
        System.out.println("Confusion Matrix (rows=true, cols=pred):");
        for (int i = 0; i < numClasses; i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < numClasses; j++) {
                System.out.print(cm[i][j] + (j == numClasses-1 ? "" : ", "));
            }
            System.out.println();
        }
    
        System.out.println("\nPer-class precision, recall, f1 (macro):");
        double macroF1 = 0.0;
        double macroPrecision = 0.0;
        double macroRecall = 0.0;
    
        for (int c = 0; c < numClasses; c++) {
            int tp = cm[c][c];
            int fp = 0;
            int fn = 0;
            for (int j = 0; j < numClasses; j++) {
                if (j != c) {
                    fp += cm[j][c];
                    fn += cm[c][j];
                }
            }
            int denomP = tp + fp;
            int denomR = tp + fn;
            double precision = denomP == 0 ? 0.0 : (double) tp / denomP;
            double recall = denomR == 0 ? 0.0 : (double) tp / denomR;
            double f1 = (precision + recall == 0) ? 0.0 : 2 * precision * recall / (precision + recall);
    
            System.out.printf("Class %d -> Precision: %.4f, Recall: %.4f, F1: %.4f, Support: %d%n",
                              c, precision, recall, f1, (tp + fn));
    
            macroF1 += f1;
            macroPrecision += precision;
            macroRecall += recall;
        }
    
        macroF1 /= numClasses;
        macroPrecision /= numClasses;
        macroRecall /= numClasses;
    
        System.out.printf("%nMacro Precision: %.4f, Macro Recall: %.4f, Macro F1: %.4f%n",
                          macroPrecision, macroRecall, macroF1);
    }
    
}