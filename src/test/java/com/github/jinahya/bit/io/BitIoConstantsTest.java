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

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_1_unchecked;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.assertj.core.api.Assertions.assertThat;

class BitIoConstantsTest {

    @Test
    void wr__Count() throws IOException {
        final int expected = current().nextInt() & Integer.MAX_VALUE;
        final int actual = write_read_1_unchecked(o -> {
            BitIoConstants.COUNT_WRITER.accept(o, expected);
            return BitIoConstants.COUNT_READER::applyAsInt;
        });
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void wr__CountCompressed() throws IOException {
        final int expected = current().nextInt() & Integer.MAX_VALUE;
        final int actual = write_read_1_unchecked(o -> {
            BitIoConstants.COUNT_WRITER_COMPRESSED.accept(o, expected);
            return BitIoConstants.COUNT_READER_COMPRESSED::applyAsInt;
        });
        assertThat(actual).isEqualTo(expected);
    }
}
