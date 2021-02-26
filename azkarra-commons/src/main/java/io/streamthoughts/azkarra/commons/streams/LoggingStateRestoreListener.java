/*
 * Copyright 2021 StreamThoughts.
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
package io.streamthoughts.azkarra.commons.streams;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.processor.StateRestoreListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * A {@link StateRestoreListener} implementation which logs state restoration progress.
 */
public class LoggingStateRestoreListener implements StateRestoreListener {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingStateRestoreListener.class);

    private final Map<TopicPartition, Long> totalOffsetToRestore = new ConcurrentHashMap<>();
    private final Map<TopicPartition, Long> totalRecordRestored = new ConcurrentHashMap<>();
    private final Map<TopicPartition, Long> startTimes = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRestoreStart(final TopicPartition topicPartition,
                               final String storeName,
                               final long startingOffset,
                               final long endingOffset) {
        LOG.info("Starting restoration process for store '{}' on topicPartition '{}': startOffset={}, endingOffset={}",
                storeName,
                topicPartition,
                startingOffset,
                endingOffset);

        final long offsetToRestore = endingOffset - startingOffset;
        totalOffsetToRestore.put(topicPartition, offsetToRestore);
        startTimes.put(topicPartition, Time.SYSTEM.milliseconds());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBatchRestored(final TopicPartition topicPartition,
                                final String storeName,
                                final long batchEndOffset,
                                final long numRestored) {

        final Long totalRestored =  totalRecordRestored.getOrDefault(topicPartition, 0L) + numRestored;
        totalRecordRestored.put(topicPartition, totalRestored);
        LOG.info(
                "Batch restored for store '{}' on topicPartition '{}': "
                + "batchEndOffset={}, numRecordRestored={}, totalRestored={}. "
                + "Percentage remaining:  {}%",
                storeName,
                topicPartition,
                batchEndOffset,
                numRestored,
                totalRestored,
                calculateRemainingFormatted(topicPartition, batchEndOffset)
                );
    }

    public String calculateRemainingFormatted(final TopicPartition topicPartition,
                                              final long batchEndOffset) {
        final long offsetToRestore = totalOffsetToRestore.get(topicPartition);
        final long currentProgress = offsetToRestore - batchEndOffset;
        final NumberFormat formatter = new DecimalFormat("#.##");
        return formatter.format(((double)currentProgress / offsetToRestore) * 100.0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRestoreEnd(final TopicPartition topicPartition,
                             final String storeName,
                             final long totalRestored) {

        final long startTs = startTimes.remove(topicPartition);
        LOG.info(
                "Restoration completed for store '{}' on topicPartition '{}', totalRestored={}. Duration: {}",
                storeName,
                topicPartition,
                totalRestored,
                humanReadableFormat(Duration.between(Instant.ofEpochMilli(startTs), Instant.now()))
        );
        totalOffsetToRestore.remove(topicPartition);
        totalRecordRestored.remove(topicPartition);
    }

    private static final Pattern PATTERN = Pattern.compile("(\\d[HMS])(?!$)");

    private static String humanReadableFormat(final Duration duration) {
        return PATTERN.matcher(duration.toString().substring(2)).
                replaceAll("$1 ")
                .toLowerCase();
    }
}
