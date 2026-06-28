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

import com.github.jinahya.bit.io.BitReader;
import com.github.jinahya.bit.io.BitReaders;
import com.github.jinahya.bit.io.FilterBitReaders;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ExtendedFilterBitReaderTest {

    @Test
    void mapping_MapsReadValue_() throws Exception {
        final BitReader<Integer> delegate = i -> 3;
        final var reader = ExtendedFilterBitReader.mapping(delegate, v -> v + 4);

        assertThat(reader.read(null)).isEqualTo(7);
    }

    @Test
    void mapping_MapsReadValue_Chained() throws Exception {
        final BitReader<Integer> delegate = i -> 3;
        final var reader = ExtendedFilterBitReader.mapping(ExtendedFilterBitReader.mapping(delegate, v -> v + 4),
                                                           v -> Integer.toString(v));

        assertThat(reader.read(null)).isEqualTo("7");
    }

    @Test
    void mapping_CompatibleWithBitReadersNullable_() {
        final BitReader<Integer> delegate = i -> 3;
        final var reader = ExtendedFilterBitReader.mapping(delegate, v -> v + 4);

        assertThat(BitReaders.nullable(reader)).isInstanceOf(BitReader.class);
    }

    @Test
    void mapping_CompatibleWithFilterBitReadersIdentity_() throws Exception {
        final BitReader<Integer> delegate = i -> 3;
        final var reader = ExtendedFilterBitReader.mapping(delegate, v -> v + 4);
        final var identity = FilterBitReaders.identity(reader);

        assertThat(identity.read(null)).isEqualTo(7);
    }

    @Test
    void mapping_NullPointerException_NullDelegate() {
        assertThatNullPointerException()
                .isThrownBy(() -> ExtendedFilterBitReader.mapping(null, v -> v))
                .withMessage("delegate is null");
    }

    @Test
    void mapping_NullPointerException_NullMapper() {
        final BitReader<Integer> delegate = i -> 3;

        assertThatNullPointerException()
                .isThrownBy(() -> ExtendedFilterBitReader.mapping(delegate, null))
                .withMessage("mapper is null");
    }

    @Test
    void constructor_NullPointerException_NullDelegate() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ExtendedFilterBitReader<Integer, Integer>(null) {
                    @Override
                    protected Integer apply(final Integer value) {
                        return value;
                    }
                })
                .withMessage("delegate is null");
    }
}
