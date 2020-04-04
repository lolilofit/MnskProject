package mnsk;

import com.google.gson.Gson;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;


public class Dataset {

    public class DataTreeExample {
        DataTree dataTree;
        String answer;

        public DataTreeExample (DataTree dataTree, String answer) {
            this.dataTree = dataTree;
            this.answer = answer;
        }
        public DataTree getDataTree() { return dataTree; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public void setDataTree(DataTree dataTree) { this.dataTree = dataTree; }
    }

    List<Map.Entry<List<String>, String>> listedData;
    List<DataTreeExample> treeData = new ArrayList<>();

    public void loadData(String filename) {
        String line;
        Map<List<String>, String> data = new LinkedHashMap<>();

        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/" + filename + ".csv"))) {
            line = reader.readLine();

            while(line != null) {

                    List<String> values = new ArrayList<>();
                    AtomicReference<Integer> pos = new AtomicReference<>(0);

                    String[] splitedLine = line.split(";");
                    Stream<String> stringStream = Arrays.stream(splitedLine);

                    stringStream.forEach(str -> {
                        if(pos.get() == splitedLine.length - 1)
                            return;
                        else {
                            values.addAll(Arrays.asList(str.split(" ")));
                        }
                        pos.getAndSet(pos.get() + 1);
                    });
                    data.put(values, splitedLine[splitedLine.length - 1]);

                    line = reader.readLine();
            }

            listedData = new ArrayList<>(data.entrySet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDataToTree(String filename, String basicUrl, String token, CloseableHttpClient httpClient, List<String> stopWords, List<String> punctiationMarks) {
        String line;
        Gson gson = new Gson();

        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/" + filename + ".csv"))) {
            line = reader.readLine();

            while(line != null) {
                AtomicReference<Integer> pos = new AtomicReference<>(0);

                String[] splitedLine = line.split(";");
                Stream<String> stringStream = Arrays.stream(splitedLine);

                stringStream.forEach(str -> {
                    if(pos.get() == splitedLine.length - 1)
                        return;
                    else {

                        List<String> wordLine = new ArrayList<>(Arrays.asList(str.split(" ")));
                        LoadWords.queryPreprocess(wordLine, stopWords, punctiationMarks);

                        for(int k = 0; k < wordLine.size(); k++) {
                            String oneWord = wordLine.get(k);

                            try {
                                String transformedWord = LoadWords.setFirstFormWorld(basicUrl, token, oneWord, gson, httpClient);
                                wordLine.set(k, transformedWord);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        DataTree dataTree = new DataTree();
                        dataTree.buildTree(wordLine);
                        treeData.add(new DataTreeExample(dataTree, splitedLine[splitedLine.length - 1]));
                    }

                    pos.getAndSet(pos.get() + 1);
                });

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
