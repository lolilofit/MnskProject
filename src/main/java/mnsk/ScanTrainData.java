package mnsk;

import com.google.gson.Gson;
import mnsk.HttpAnswers.WordFormAnswer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ScanTrainData {
    private int numberTrainLines;
    private Dataset dataset;
    private List<String> stopWords;
    private List<String> punctiationMarks;
    private String token;
    private String basicUrl;
    private Gson gson;
    private CloseableHttpClient httpClient;

    public ScanTrainData(int numberTrainLines, Dataset dataset, String token, String basicUrl, CloseableHttpClient httpClient) {
        this.numberTrainLines = numberTrainLines;
        this.dataset = dataset;
        this.token = token;
        this.basicUrl = basicUrl;
        this.httpClient = httpClient;
        gson = new Gson();
    }

    private float calkSimilarity(List<String> firstVector, List<String> secondVector) throws IOException {
        float sum = 0.0f;

            for (String str : secondVector) {
                if (!firstVector.contains(str)) {
                    HttpGet request = new HttpGet(basicUrl + "?token=" + token + "&c=vector" + "&query=" + str + "&top=20&lang=ru&format=json&scores=0&forms=0");
                    CloseableHttpResponse response = httpClient.execute(request);

                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String result = EntityUtils.toString(entity);
                        WordFormAnswer wordFormAnswer = gson.fromJson(result, WordFormAnswer.class);

                        boolean flag = false;

                        for (Integer k : wordFormAnswer.getResponse().keySet()) {
                            WordFormAnswer.AnswerElement answerElement = wordFormAnswer.getResponse().get(k);
                            List<String> similar = answerElement.getVector();

                            for (String similarString : similar) {
                                if (firstVector.contains(similarString)) {
                                    sum ++;
                                    flag = true;
                                    break;
                                }
                            }
                            if(flag)
                                break;
                            flag = false;
                        }
                    }
                }
                else sum++;
            }

        return sum / ((float)firstVector.size() + (float)secondVector.size() - sum);
    }

    private  Map.Entry<List<String>, String> findNearest(List<String> params) throws IOException {
        Map.Entry<List<String>, String> currentNearest = null;
        float currentNearestDistance = -1;

         for(int i = 0; i < numberTrainLines + 1; i++) {
            Map.Entry<List<String>, String> dataLine = dataset.listedData.get(i);
            LoadWords.queryPreprocess(dataLine.getKey(), stopWords, punctiationMarks);

            float simil = calkSimilarity(dataLine.getKey(), params);
            if(simil > currentNearestDistance || currentNearestDistance == -1) {
                currentNearestDistance = simil;
                currentNearest = dataLine;
            }
        }
        return currentNearest;
    }


    public void closestNeigh() throws IOException {
        stopWords = LoadWords.loadStopWords();
        punctiationMarks = LoadWords.loadPunctuationMarks();

        for( Map.Entry<List<String>, String> entry : dataset.listedData) {
            LoadWords.queryPreprocess(entry.getKey(), stopWords, punctiationMarks);

            int size = entry.getKey().size();
            for (int i = 0; i < size; i++) {
                String str = entry.getKey().get(i);

                    //String req = "http://paraphraser.ru/api?token=" + token + "&c=vector&query=женщину&top=5&lang=ru&format=json&scores=0&forms=0";
                    //basicUrl + "?token=" + token + "&c=vector" + "&query=" + str + "&top=3&forms=1&lang=ru&format=json"

                String word = LoadWords.setFirstFormWorld(basicUrl, token, str, gson, httpClient);
                entry.getKey().set(i, word);
            }
        }


        for(int i = numberTrainLines + 1; i < dataset.listedData.size(); i++) {
            Map.Entry<List<String>, String> dataLine = dataset.listedData.get(i);
            Map.Entry<List<String>, String> answer = findNearest(dataLine.getKey());
            System.out.println(answer.getValue());
        }
    }
}
