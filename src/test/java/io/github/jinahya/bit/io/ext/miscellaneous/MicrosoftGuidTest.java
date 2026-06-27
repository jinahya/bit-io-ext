package io.github.jinahya.bit.io.ext.miscellaneous;

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

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.DefaultBitInput;
import com.github.jinahya.bit.io.DefaultBitOutput;
import com.github.jinahya.bit.io.StreamByteInput;
import com.github.jinahya.bit.io.StreamByteOutput;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class MicrosoftGuidTest {

    @Test
    void read_ExpectedValue_KnownBytes() throws IOException {
        final UUID actual = MicrosoftGuid.INSTANCE.read(input(microsoftBytes()));

        assertThat(actual).isEqualTo(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));
    }

    @Test
    void write_ExpectedBytes_KnownValue() throws IOException {
        final byte[] actual = write(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));

        assertThat(actual).containsExactly(microsoftBytes());
    }

    @Test
    void write_NullPointerException_NullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> MicrosoftGuid.INSTANCE.write(output(), null))
                .withMessage("value is null");
    }

    private static BitInput input(final byte[] bytes) {
        return new DefaultBitInput(new StreamByteInput(new ByteArrayInputStream(bytes)));
    }

    private static BitOutput output() {
        return new DefaultBitOutput(new StreamByteOutput(new ByteArrayOutputStream()));
    }

    private static byte[] write(final UUID value) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final BitOutput output = new DefaultBitOutput(new StreamByteOutput(baos));
        MicrosoftGuid.INSTANCE.write(output, value);
        output.align(1);
        return baos.toByteArray();
    }

    private static byte[] microsoftBytes() {
        return new byte[]{
                0x67, 0x45, 0x23, 0x01,
                (byte) 0xAB, (byte) 0x89,
                (byte) 0xEF, (byte) 0xCD,
                0x01, 0x23,
                0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
        };
    }
}
