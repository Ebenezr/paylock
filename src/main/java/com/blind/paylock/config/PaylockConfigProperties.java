package com.blind.paylock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;


@Configuration
@ConfigurationProperties(prefix = "dxl.ms")
@Data
public class PaylockConfigProperties {

    @Value("${lock.threshold.percent}")
    private String lockThresholdPercent;


    @Value("${cancellation.fee.percent}")
    private String cancellationFeePercent;
}
