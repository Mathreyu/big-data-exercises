package nearsoft.academy.bigdata.recommendation;


import com.google.common.collect.BiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MovieRecommender{

    private int totalReviews;
    private int totalProducts;
    private int totalUsers;
    public UserBasedRecommender recommender;
    public Map<String, Integer> usersTable;
    public BiMap<Integer, String> productsTable;

    public MovieRecommender(String path) throws IOException, TasteException{

        FileOps file = new FileOps(path);
            file.unZipIt();
            try {
                file.csvIt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        totalReviews = file.getReviews();
        totalProducts = file.getProducts();
        totalUsers = file.getUsers();

        File moviesCSV = new File("movies.csv");
        productsTable = file.getProductsTable();
        usersTable = file.getUserTable();

        DataModel model = new FileDataModel(moviesCSV);
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    }

    public double getTotalReviews(){
        return totalReviews;
    }

    public double getTotalProducts(){
        return totalProducts;
    }

    public double getTotalUsers(){
        return totalUsers;
    }

    public List<String> getRecommendationsForUser(String userId) throws TasteException{
        int lookUpId = Integer.valueOf(usersTable.get(userId));
        List<String> results = new ArrayList<String>();
        List<RecommendedItem> recomendations = recommender.recommend(lookUpId, 5);

        for (RecommendedItem sugestion: recomendations) {
            int currentItem = (int) sugestion.getItemID();
            String recommendationID = productsTable.get(currentItem);
            results.add(recommendationID);
        }
        return results;
    }

}
