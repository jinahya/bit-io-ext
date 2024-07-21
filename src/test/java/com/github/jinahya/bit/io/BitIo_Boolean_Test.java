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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_1_unchecked;
import static org.assertj.core.api.Assertions.assertThat;

class BitIo_Boolean_Test {

    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    void test__(final boolean expected) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            o.writeBoolean(expected);
            return BitInput::readBoolean;
        });
        assertThat(actual).isEqualTo(expected);
    }
}
