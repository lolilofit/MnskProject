package mnsk;

import com.google.gson.Gson;
import mnsk.HttpAnswers.WordFormAnswer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeScanTran {
    private int numberTrainLines;
    private Dataset dataset;
    private List<String> stopWords;
    private List<String> punctiationMarks;
    private String token;
    private String basicUrl;
    private Gson gson;
    private CloseableHttpClient httpClient;

    public TreeScanTran(int numberTrainLines, Dataset dataset, String token, String basicUrl, CloseableHttpClient httpClient) {
        this.numberTrainLines = numberTrainLines;
        this.dataset = dataset;
        this.token = token;
        this.basicUrl = basicUrl;
        this.httpClient = httpClient;
        gson = new Gson();
    }

    private float calkSimilarity(DataTree firstTree, DataTree secondTree) throws IOException {
        float sum = 0.0f;

        //убрать
        List<String> secondVector = new ArrayList<>();
        secondTree.toList(secondVector);

        for (String str : secondVector) {
            if (!firstTree.contains(str)) {
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
                            if (firstTree.contains(similarString)) {
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

        return sum / ((float)firstTree.getSize() + (float)secondVector.size() - sum);
    }

    private String findNearest(DataTree dataTree) throws IOException {
        Dataset.DataTreeExample currentNearest = null;
        float currentNearestDistance = -1;

        for(int i = 0; i < numberTrainLines + 1; i++) {
            Dataset.DataTreeExample dataLine = dataset.treeData.get(i);

            float simil = calkSimilarity(dataLine.getDataTree(), dataTree);
            if(simil > currentNearestDistance || currentNearestDistance == -1) {
                currentNearestDistance = simil;
                currentNearest = dataLine;
            }
        }
        assert currentNearest != null;

        return currentNearest.getAnswer();
    }


    public void closestNeigh() throws IOException {
        stopWords = LoadWords.loadStopWords();
        punctiationMarks = LoadWords.loadPunctuationMarks();

        dataset.loadDataToTree("dataset", basicUrl, token, httpClient, stopWords, punctiationMarks);

        for(int i = numberTrainLines + 1; i < dataset.treeData.size(); i++) {
            Dataset.DataTreeExample dataLine = dataset.treeData.get(i);
            String answer = findNearest(dataLine.getDataTree());
            System.out.println(answer);
        }

    }
}
