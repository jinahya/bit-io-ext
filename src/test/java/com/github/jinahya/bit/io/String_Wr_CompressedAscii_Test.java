package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io2
 * %%
 * Copyright (C) 2020 - 2022 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoConstants.COUNT_READER;
import static com.github.jinahya.bit.io.BitIoConstants.COUNT_WRITER;
import static com.github.jinahya.bit.io.BitIoTestUtils.wr1u;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class String_Wr_CompressedAscii_Test {

    private static Stream<String> randomValueStream() {
        return ByteArray_Wr_CompressedAscii_Test.randomValueStream()
                .map(b -> new String(b, StandardCharsets.US_ASCII))
                ;
    }

    private static Stream<String> randomValueStreamWithNull() {
        return Stream.concat(randomValueStream(), Stream.of(new String[]{null}));
    }

    private static final boolean PRINTABLE_ONLY = false;

    @MethodSource({"randomValueStream"})
    @ParameterizedTest
    void test(final String expected) throws IOException {
        final var printable = false;
        final var actual = wr1u(o -> {
            final var writer = StringWriter.compressedAscii(printable);
            o.writeObject(writer, expected);
            return i -> {
                final var reader = StringReader.compressedAscii(printable);
                return i.readObject(reader);
            };
        });
        assertThat(actual).isEqualTo(expected);
    }

    @MethodSource({"randomValueStreamWithNull"})
    @ParameterizedTest
    void nullable(final String expected) throws IOException {
        final var printable = false;
        final var actual = wr1u(o -> {
            StringWriter.compressedAscii(printable).nullable().write(o, expected);
            return StringReader.compressedAscii(printable).nullable()::read;
        });
        if (expected == null) {
            assertThat(actual).isNull();
        } else {
            assertThat(actual).isEqualTo(expected);
        }
    }

    @MethodSource({"randomValueStream"})
    @ParameterizedTest
    void wr__UncompressedCount(final String expected) throws IOException {
        final String actual = BitIoTestUtils.wr1u(o -> {
            final var writer = StringWriter.compressedAscii(PRINTABLE_ONLY);
            writer.setCountWriter(COUNT_WRITER);
            writer.write(o, expected);
            return i -> {
                final var reader = StringReader.compressedAscii(PRINTABLE_ONLY);
                reader.setCountReader(COUNT_READER);
                return reader.read(i);
            };
        });
        assertThat(actual).isEqualTo(expected);
    }
}
