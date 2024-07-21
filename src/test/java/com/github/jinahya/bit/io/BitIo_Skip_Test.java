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

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_2_uncheckd;
import static java.util.concurrent.ThreadLocalRandom.current;

class BitIo_Skip_Test {

    @Test
    void skip__() throws IOException {
        final var expected = current().nextInt(1, 1024);
        write_read_2_uncheckd(o -> {
            o.skip(expected);
            return i -> i.skip(expected);
        });
    }
}
