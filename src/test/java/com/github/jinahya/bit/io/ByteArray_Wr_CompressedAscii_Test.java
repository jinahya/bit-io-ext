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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoConstants.COUNT_READER;
import static com.github.jinahya.bit.io.BitIoConstants.COUNT_WRITER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ByteArray_Wr_CompressedAscii_Test {

    private static byte[] randomize(final byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ThreadLocalRandom.current().nextInt(0x00, 0x80);
        }
        return bytes;
    }

    static byte[] randomBytes() {
        final int length = ThreadLocalRandom.current().nextInt(131072);
        return randomize(new byte[length]);
    }

    static Stream<byte[]> randomValueStream() {
        return Stream.concat(
                IntStream.range(0, 16).mapToObj(i -> randomBytes()),
                Stream.of(new byte[0])
        );
    }

    private static final boolean PRINTABLE_ONLY = false;

    private void run(final byte[] expected) throws IOException {
        final boolean printableOnly = false;
        final var baos = new ByteArrayOutputStream();
        final var output = new ByteOutputAdapter(new StreamByteOutput(baos));
        final var writer = ByteArrayWriter.compressedAscii(printableOnly);
        writer.write(output, expected);
        final var padded = output.align(1);
        if (false) {
            final var given = expected.length + Integer.BYTES;
            log.debug("given: {}, written: {}, ratio: {}", given, baos.size(), (baos.size() / (double) given) * 100.0d);
        }
        final var bais = new ByteArrayInputStream(baos.toByteArray());
        final var input = new ByteInputAdapter(new StreamByteInput(bais));
        final var reader = ByteArrayReader.compressedAscii(printableOnly);
        final var actual = reader.read(input);
        final var discarded = input.align(1);
        assertThat(actual).isEqualTo(expected);
        assertThat(discarded).isEqualTo(padded);
    }

    @MethodSource({"randomValueStream"})
    @ParameterizedTest
    void __random(final byte[] expected) throws IOException {
        run(expected);
    }

    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    @ParameterizedTest(name = "[{index}] byte[{0}]")
    void __length(final int length) throws IOException {
        run(randomize(new byte[length]));
    }

    @Test
    void nullable_() throws IOException {
        final var printableOnly = false;
        final var baos = new ByteArrayOutputStream();
        final var output = new ByteOutputAdapter(new StreamByteOutput(baos));
        final var writer = ByteArrayWriter.compressedAscii(printableOnly).nullable();
        writer.write(output, null);
        final var padded = output.align(1);
        final var bais = new ByteArrayInputStream(baos.toByteArray());
        final var input = new ByteInputAdapter(new StreamByteInput(bais));
        final var reader = ByteArrayReader.compressedAscii(printableOnly).nullable();
        final var actual = reader.read(input);
        final var discarded = input.align(1);
        assertThat(actual).isNull();
        assertThat(discarded).isEqualTo(padded);
    }

    @MethodSource({"randomValueStream"})
    @ParameterizedTest
    void wr__UncompressedCount(final byte[] expected) throws IOException {
        final byte[] actual = BitIoTestUtils.write_read_1_unchecked(o -> {
            final var writer = ByteArrayWriter.compressedAscii(PRINTABLE_ONLY);
            writer.setCountWriter(COUNT_WRITER);
            writer.write(o, expected);
            return i -> {
                final var reader = ByteArrayReader.compressedAscii(PRINTABLE_ONLY);
                reader.setCountReader(COUNT_READER);
                return reader.read(i);
            };
        });
        assertThat(actual).isEqualTo(expected);
    }
}
