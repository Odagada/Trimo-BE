package com.example.demo.global.webHook;

import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebHook {

    private final String url = "https://discordapp.com/api/webhooks/1222413264127000648/4aTqvcbPDhX_O1apeO8OZkndweCsnDEpwUFstWBWNt-ZjPeGxPO_WiqzXNEggkvrkZRz";
    private final String username = "Bot";
    private EmbedObject embeds;

    public DiscordWebHook(String title, String description, Integer color){
        this.embeds = new EmbedObject(title, description, color);
    }

    @Getter
    public static class EmbedObject {

        private String title;
        private String description;
        private Integer color;

        public EmbedObject(String title, String description, Integer color){
            this.title = title;
            this.description = description;
            this.color = color;
        }
    }

    public void jsonConverter(){
        JSONObject embedJson = new JSONObject();
        embedJson.put("title", this.embeds.getTitle());
        embedJson.put("description", this.embeds.getDescription());
        embedJson.put("color", this.embeds.getColor());

        List<JSONObject> embedObjects = new ArrayList<>();
        embedObjects.add(embedJson);

        JSONObject json = new JSONObject();
        json.put("url", this.url);
        json.put("username", this.username);
        json.put("embeds", embedObjects.toArray());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
        restTemplate.postForObject(this.url, entity, String.class);
    }
}
