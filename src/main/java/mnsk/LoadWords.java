package mnsk;

import com.google.gson.Gson;
import mnsk.HttpAnswers.WordFormAnswer;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadWords {
    public static List<String> loadStopWords() {
        List<String>  stopWords = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/stop_words.txt"))) {
            String line = reader.readLine();

            while(line !=  null) {
                stopWords.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    public static List<String>  loadPunctuationMarks() {
        List<String> punctiationMarks = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/punctuation_marks.txt"))) {
            String line = reader.readLine();

            while(line !=  null) {
                punctiationMarks.add(line);
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return punctiationMarks;
    }


    public static String setFirstFormWorld(String basicUrl, String token, String str, Gson gson, CloseableHttpClient httpClient) throws IOException {
        HttpGet request = new HttpGet(basicUrl + "?token=" + token + "&c=vector" + "&query=" + str + "&top=1&lang=ru&format=json&scores=0&forms=0");
        CloseableHttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String result = EntityUtils.toString(entity);
            WordFormAnswer wordFormAnswer = gson.fromJson(result, WordFormAnswer.class);
            WordFormAnswer.AnswerElement answerElement = wordFormAnswer.getResponse().get(1);
            return answerElement.getLemma();
        }
        return "";
    }


    public static void queryPreprocess(List<String> query, List<String> stopWords, List<String> punctiationMarks) {
        boolean flag = false;

        for(int i = query.size() - 1; i > 0; i--) {
            query.set(i, query.get(i).toLowerCase());
            String word = query.get(i);
            for(String stopWord : stopWords)
                if(word.equals(stopWord)) {
                    query.remove(i);
                    flag = true;
                    break;
                }
            if(!flag) {
                for(String punctMark : punctiationMarks)
                    word = word.replaceAll(punctMark, "");
                query.set(i, word);
            }
            flag = false;
        }
    }
}
