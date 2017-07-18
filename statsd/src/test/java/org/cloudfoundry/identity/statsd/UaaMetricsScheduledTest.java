package org.cloudfoundry.identity.statsd;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/*******************************************************************************
 * Cloud Foundry
 * Copyright (c) [2009-2015] Pivotal Software, Inc. All Rights Reserved.
 * <p/>
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 * <p/>
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UaaMetricsScheduledTest {

    @Autowired
    private UaaMetricsEmitter uaaMetricsEmitter;

    @Test
    public void emittingMetrics_Is_Scheduled() throws Exception {
        Scheduled schedulerAnnotation = uaaMetricsEmitter.getClass().getMethod("emitMetrics").getAnnotation(Scheduled.class);
        Assert.assertEquals(5000, schedulerAnnotation.fixedRate());
    }
}