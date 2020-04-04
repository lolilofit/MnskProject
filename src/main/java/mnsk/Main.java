package mnsk;

import com.google.gson.Gson;

import mnsk.HttpAnswers.SimpleToken;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String token = null;
        String basicUrl = "http://paraphraser.ru/";
        String login = "darya";
        String password = "123";
        Gson gson = new Gson();

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(basicUrl + "token?login=" + login + "&password=" + password);
            CloseableHttpResponse response = httpClient.execute(request);
            System.out.println(response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                SimpleToken simpleToken = gson.fromJson(result, SimpleToken.class);
                token = simpleToken.getToken();
            }

            Dataset dataset = new Dataset();

            /*
            dataset.loadData("dataset");
            if (token != null) {
                ScanTrainData scanTrainData = new ScanTrainData(4, dataset, token, basicUrl + "api", httpClient);
                scanTrainData.closestNeigh();
            }
             */
            if(token != null) {
                TreeScanTran treeScanTran = new TreeScanTran(4, dataset, token, basicUrl + "api", httpClient);
                treeScanTran.closestNeigh();
            }
        }
    }
}
