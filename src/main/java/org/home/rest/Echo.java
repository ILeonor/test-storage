package org.home.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/echo")
public class Echo {
    @GetMapping
    public ResponseEntity<String> formResponse(@RequestParam MultiValueMap<String, String> params,
                                               @RequestHeader MultiValueMap<String, String> headers) {
        StringBuilder response = new StringBuilder();

        response.append("HEADERS:\n");
        headers.forEach((key, values) -> {
            values.forEach((value) -> {
                response.append(key).append(": ").append(value).append("\n");
            });
        });

        response.append("\nPARAMS:\n");
        params.forEach((key, values) -> {
            values.forEach((value) -> {
                response.append(key).append(": ").append(value).append("\n");
            });
        });

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }
}
