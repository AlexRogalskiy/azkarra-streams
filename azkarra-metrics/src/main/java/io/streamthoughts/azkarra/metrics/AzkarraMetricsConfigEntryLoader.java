/*
 * Copyright 2019-2021 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.azkarra.metrics;

import io.streamthoughts.azkarra.api.annotations.Component;
import io.streamthoughts.azkarra.api.config.Conf;
import io.streamthoughts.azkarra.api.config.ConfEntry;
import io.streamthoughts.azkarra.streams.AbstractConfigEntryLoader;
import io.streamthoughts.azkarra.streams.AzkarraApplication;

@Component
public class AzkarraMetricsConfigEntryLoader extends AbstractConfigEntryLoader {

    public static final String CONFIG_ENTRY_KEY = "metrics";

    /**
     * Creates a new {@link AzkarraMetricsConfigEntryLoader} instance.
     */
    public AzkarraMetricsConfigEntryLoader() {
        super(CONFIG_ENTRY_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(final ConfEntry configEntryObject,
                     final AzkarraApplication application) {
        // Injects "metrics." prefixed properties into Azkarra Context.
        application.getContext().addConfiguration(Conf.of(CONFIG_ENTRY_KEY, configEntryObject.asSubConf()));
    }
}
