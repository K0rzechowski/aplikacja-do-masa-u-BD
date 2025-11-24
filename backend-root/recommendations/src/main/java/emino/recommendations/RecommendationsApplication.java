package emino.recommendations;

import java.nio.file.Path;
import java.nio.file.Paths;

import smile.data.DataFrame;
import smile.data.vector.DoubleVector;
import smile.io.Read;
import smile.io.Write;
import smile.regression.RandomForest;

public class RecommendationsApplication {
    
    public static void main(String[] args) throws Exception {
        AlgorithmComparator comparator = new AlgorithmComparator();
        comparator.setUp();

        RandomForest model = (RandomForest) Read.object(Paths.get("random_forest_model_the_best.bin"));

        DataFrame df = comparator.loadCSV();

        double[] predictions = model.predict(df);

        df = df.merge(new DataFrame(new DoubleVector("effectiveness_score_pred", predictions)));

        Path resultPath = Paths.get("effectiveness.csv");
        Write.csv(df, resultPath);
    }
}