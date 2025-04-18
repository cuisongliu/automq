/*
 * Copyright 2025, AutoMQ HK Limited.
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

package org.apache.kafka.common.record;

import org.apache.kafka.common.network.TransferableChannel;
import org.apache.kafka.common.utils.AbstractIterator;
import org.apache.kafka.common.utils.Time;

import java.io.IOException;

/**
 * A wrapper of {@link Records} which will run the specified release hook when {@link #release()} is called.
 */
public class PooledRecords extends AbstractRecords implements PooledResource, Records {

    private final Records records;
    private final Runnable releaseHook;

    public PooledRecords(Records records, Runnable releaseHook) {
        this.records = records;
        this.releaseHook = releaseHook;
    }

    @Override
    public void release() {
        releaseHook.run();
        if (records instanceof PooledResource) {
            ((PooledResource) records).release();
        }
    }

    @Override
    public int sizeInBytes() {
        return records.sizeInBytes();
    }

    @Override
    public Iterable<? extends RecordBatch> batches() {
        return records.batches();
    }

    @Override
    public AbstractIterator<? extends RecordBatch> batchIterator() {
        return records.batchIterator();
    }

    @Override
    public ConvertedRecords<? extends Records> downConvert(byte toMagic, long firstOffset, Time time) {
        return records.downConvert(toMagic, firstOffset, time);
    }

    @Override
    public int writeTo(TransferableChannel channel, int position, int length) throws IOException {
        return records.writeTo(channel, position, length);
    }
}
