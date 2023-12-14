package com.academy.fintech.scoring.pe.client.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scoring.client.pe.grpc")
public record ProductEngineGrpcClientProperty(String host, int port) { }
