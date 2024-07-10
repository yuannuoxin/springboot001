package com.yuan.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ConfigKeyValue {

    @Value("${ip2regionDbPath:123}")
    private String ip2regionDbPath;
}
