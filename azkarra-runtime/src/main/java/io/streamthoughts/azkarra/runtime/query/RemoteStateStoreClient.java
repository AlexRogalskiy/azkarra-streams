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
package io.streamthoughts.azkarra.runtime.query;

import io.streamthoughts.azkarra.api.query.QueryOptions;
import io.streamthoughts.azkarra.api.query.QueryRequest;
import io.streamthoughts.azkarra.api.query.result.QueryResult;
import io.streamthoughts.azkarra.api.util.Endpoint;

import java.util.concurrent.CompletableFuture;

/**
 * A {@code RemoteStateStoreClient} is used to execute a query on a remote state store.
 */
public interface RemoteStateStoreClient {

    /**
     * Query a remote Kafka Streams state store using the specified server.
     *
     * @param application   the Kafka Streams {@code application.id}.
     * @param endpoint      the {@link Endpoint}.
     * @param queryObject   the {@link QueryRequest}.
     * @param queryOptions  the {@link QueryOptions}.
     *
     * @return  a {@link CompletableFuture} of {@link QueryResult}.
     */
    <K, V> CompletableFuture<QueryResult<K, V>> query(final String application,
                                                      final Endpoint endpoint,
                                                      final QueryRequest queryObject,
                                                      final QueryOptions queryOptions);
}
