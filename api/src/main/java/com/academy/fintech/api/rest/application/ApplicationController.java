package com.academy.fintech.api.rest.application;

import com.academy.fintech.api.core.origination.OriginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    private final OriginationService originationService;
    private final ApplicationMapper applicationMapper;

    @PostMapping
    public String create(@RequestBody ApplicationRequest applicationRequest) {
        return originationService.createApplication(applicationMapper.mapRequestToDto(applicationRequest));
    }

}
