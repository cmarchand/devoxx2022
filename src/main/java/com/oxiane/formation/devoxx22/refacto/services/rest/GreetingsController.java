package com.oxiane.formation.devoxx22.refacto.services.rest;

import com.oxiane.formation.devoxx22.refacto.model.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/greetings")
public class GreetingsController {
    private static final String TEMPLATE = "Hello, %s !";
    private static final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greetings(
            @RequestParam(value="name", defaultValue="World !") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(TEMPLATE, name));
    }
}
