package com.recipes.FlavourFoundry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.recipes.FlavourFoundry.model.Url;

@RestController
@RequestMapping("/api")
public class ExternalRecipeRestController {

    @Autowired
    Url url;

    RestTemplate restTemplate = new RestTemplate();

    @Value("${api.key}")
    private String apiKey;

    @GetMapping("/getrecipe")
    public ResponseEntity<String> getRecipe(@RequestParam MultiValueMap<String, String> params) {
        String number = params.getFirst("number");
        String includeTags = params.getFirst("include-tags");
        String excludeTags = params.getFirst("exclude-tags");

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(url.getRecipeApi())
                                        .queryParam("apiKey", apiKey)
                                        .queryParam("number", number);

        if (includeTags != null) {
            urlBuilder.queryParam("include-tags", includeTags);
        }

        if (excludeTags != null) {
            urlBuilder.queryParam("exclude-tags", excludeTags);
        }

        String url = urlBuilder.toUriString();

        System.out.println(url);

        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
        //System.out.println(resp.getBody());

        return ResponseEntity.ok().body(resp.getBody());

    }
}
