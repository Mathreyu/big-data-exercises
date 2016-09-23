package nearsoft.academy.bigdata.recommendation;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class FileOps{

    private static String INPUT_FILE = "";
    private static final String OUTPUT_FILE = "movies.txt";
    private static final String CSV = "movies.csv";
    private static final String NEW_LINE = "\n";
    private static final String COMMA = ",";
    private final Map<String, Integer> userTable = new HashMap<String, Integer>();
    private final HashBiMap<String, Integer> productsTable = HashBiMap.create();
    private int reviews = 0;
    private int products = 0;
    private int users = 0;

    public FileOps(String path){
        this.INPUT_FILE = path;
    }

    public void unZipIt(){
        byte[] buffer = new byte[1024];

        try{

            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(INPUT_FILE));

            FileOutputStream out =
                    new FileOutputStream(OUTPUT_FILE);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();
            System.out.println("Done with extraction");

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void csvIt() throws IOException {
        File movies = new File(OUTPUT_FILE);
        String outFile = CSV;
        FileWriter writer = new FileWriter(outFile);

        try {
            LineIterator index = FileUtils.lineIterator(movies);
            try {
                int productTotal = 0;
                int userTotal = 0;
                String productString = "";
                String userString;
                StringBuilder fileString = new StringBuilder();

                while (index.hasNext()) {
                    String currentLine = index.nextLine();
                    String cont = currentLine.substring(currentLine.indexOf(" ") + 1);

                    //Checking if this line is ProductID
                    if (currentLine.contains("product/productId:")) {
                        if (!productsTable.containsKey(cont)) {
                            productTotal++;
                            productsTable.put(cont, productTotal);
                        }
                        productString = String.valueOf(productsTable.get(cont));
                    }
                    //checking if this line is UserID
                    else if (currentLine.contains("review/userId:")) {
                        if (!userTable.containsKey(cont)) {
                            userTotal++;
                            userTable.put(cont, userTotal);
                        }
                        userString = String.valueOf(userTable.get(cont));

                        fileString.append(userString);
                        fileString.append(COMMA);
                        fileString.append(productString);
                        fileString.append(COMMA);
                    }
                    //Checking if this line is the Score
                    else if(currentLine.contains("review/score:")){
                        reviews++;
                        fileString.append(cont);
                        fileString.append(NEW_LINE);
                    }
                }
                writer.append(fileString);
                writer.flush();

                users = userTotal;
                products = productTotal;
            } finally {
                System.out.println("Done with CSV and tables");
                LineIterator.closeQuietly(index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Map<String, Integer> getUserTable(){
        return userTable;
    }

    public BiMap<Integer, String> getProductsTable() {
        return productsTable.inverse();
    }

    public int getReviews() {
        return reviews;
    }

    public int getProducts() {
        return products;
    }

    public int getUsers() {
        return users;
    }
}