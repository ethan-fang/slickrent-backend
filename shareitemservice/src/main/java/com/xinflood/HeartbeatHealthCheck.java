package com.xinflood;

import com.codahale.metrics.health.HealthCheck;

/**
 * Created by xinxinwang on 11/17/14.
 */
public class HeartbeatHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
