package com.academy.fintech.scoring.configuration;

import com.academy.fintech.scoring.pe.client.grpc.ProductEngineGrpcClientProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ ProductEngineGrpcClientProperty.class })
public class ScoringConfiguration { }
