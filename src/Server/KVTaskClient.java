package Server;

import com.google.gson.*;
import manager.InMemoryTaskManager;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiToken;
    private final HttpClient client;
    URI url;
    String key;

    public KVTaskClient(URI url) {
        client = HttpClient.newHttpClient();
        this.url = url;
        URI urlRegister = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(urlRegister).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            apiToken = jsonElement.getAsString();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса на регистрацию возникла ошибка.");
        }
    }

    public void put(String key, String json){
        this.key = key;
        URI urlSave = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSave).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса на сохранение возникла ошибка.");
        }
    }

    public String load(String key) {
        String responseLoad = null;
        URI urlLoad = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(urlLoad).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            Gson gson = new GsonBuilder().serializeNulls().create();
            //Тут не считывается таска при загрузке с сервера
            Task task = gson.fromJson(jsonElement, Task.class);
            responseLoad = gson.toJson(task).toString();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса на загрузку возникла ошибка.");
        }
        return responseLoad;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
