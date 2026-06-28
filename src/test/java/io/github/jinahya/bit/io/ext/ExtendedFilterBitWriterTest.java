package io.github.jinahya.bit.io.ext;

/*-
 * #%L
 * bit-io-ext
 * %%
 * Copyright (C) 2020 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.bit.io.BitWriter;
import com.github.jinahya.bit.io.BitWriters;
import com.github.jinahya.bit.io.FilterBitWriters;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ExtendedFilterBitWriterTest {

    @Test
    void mapping_MapsWrittenValue_() throws Exception {
        final var written = new AtomicReference<Integer>();
        final BitWriter<Integer> delegate = (o, v) -> written.set(v);
        final var writer = ExtendedFilterBitWriter.mapping(delegate, String::length);

        writer.write(null, "abcd");

        assertThat(written).hasValue(4);
    }

    @Test
    void mapping_MapsWrittenValue_Chained() throws Exception {
        final var written = new AtomicReference<Integer>();
        final BitWriter<Integer> delegate = (o, v) -> written.set(v);
        final var writer = ExtendedFilterBitWriter.mapping(ExtendedFilterBitWriter.mapping(delegate, String::length),
                                                           Object::toString);

        writer.write(null, new StringBuilder("abcd"));

        assertThat(written).hasValue(4);
    }

    @Test
    void mapping_CompatibleWithBitWritersNullable_() {
        final BitWriter<Integer> delegate = (o, v) -> {
        };
        final var writer = ExtendedFilterBitWriter.mapping(delegate, String::length);

        assertThat(BitWriters.nullable(writer)).isInstanceOf(BitWriter.class);
    }

    @Test
    void mapping_CompatibleWithFilterBitWritersIdentity_() throws Exception {
        final var written = new AtomicReference<Integer>();
        final BitWriter<Integer> delegate = (o, v) -> written.set(v);
        final var writer = ExtendedFilterBitWriter.mapping(delegate, String::length);
        final var identity = FilterBitWriters.identity(writer);

        identity.write(null, "abcd");

        assertThat(written).hasValue(4);
    }

    @Test
    void mapping_NullPointerException_NullDelegate() {
        assertThatNullPointerException()
                .isThrownBy(() -> ExtendedFilterBitWriter.mapping(null, v -> v))
                .withMessage("delegate is null");
    }

    @Test
    void mapping_NullPointerException_NullMapper() {
        final BitWriter<Integer> delegate = (o, v) -> {
        };

        assertThatNullPointerException()
                .isThrownBy(() -> ExtendedFilterBitWriter.mapping(delegate, null))
                .withMessage("mapper is null");
    }

    @Test
    void constructor_NullPointerException_NullDelegate() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ExtendedFilterBitWriter<Integer, Integer>(null) {
                    @Override
                    protected Integer apply(final Integer value) {
                        return value;
                    }
                })
                .withMessage("delegate is null");
    }
}
