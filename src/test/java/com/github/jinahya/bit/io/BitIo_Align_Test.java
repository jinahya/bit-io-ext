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

import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_2_uncheckd;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.assertj.core.api.Assertions.assertThat;

class BitIo_Align_Test {

    @RepeatedTest(16)
    void align__() throws IOException {
        write_read_2_uncheckd(o -> {
            final var skip = current().nextBoolean();
            final var bits = current().nextInt(1, 128);
            if (skip) {
                o.skip(bits);
            }
            final var bytes = current().nextInt(1, 128);
            final var padded = o.align(bytes);
            return i -> {
                if (skip) {
                    i.skip(bits);
                }
                final var discarded = i.align(bytes);
                assertThat(discarded).isEqualTo(padded);
            };
        });
    }
}
