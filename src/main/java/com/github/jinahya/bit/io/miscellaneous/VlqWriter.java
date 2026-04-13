package com.github.jinahya.bit.io.miscellaneous;

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

import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.LongWriter;

import java.io.IOException;
import java.util.Objects;

/**
 * A writer for writing <a href="https://en.wikipedia.org/wiki/Variable-length_quantity">VLQ</a>-encoded values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://en.wikipedia.org/wiki/Variable-length_quantity">Variable-length quantity</a>
 */
public class VlqWriter
        implements LongWriter {

    // -----------------------------------------------------------------------------------------------------------------
    private static final class InstanceHolder {

        private static final VlqWriter INSTANCE = new VlqWriter();

        private InstanceHolder() {
            throw new AssertionError(BitIoMiscellaneousConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
        }
    }

    /**
     * Returns the instance of this writer. The {@code VlqWriter} is singleton.
     *
     * @return the instance of this writer.
     */
    public static VlqWriter getInstance() {
        return InstanceHolder.INSTANCE;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    private VlqWriter() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void writeLong(final BitOutput output, long value) throws IOException {
        Objects.requireNonNull(output, "output is null");
        if (value < 0L) {
            throw new IllegalArgumentException("negative value: " + value);
        }
        if (value == 0L) {
            output.writeLong(true, Byte.SIZE, 0L);
            return;
        }
        final int ones = Long.SIZE - Long.numberOfLeadingZeros(value);
        final int quotient = ones / 7;
        final int remainder = ones % 7;
        final byte[] bytes = new byte[quotient + (remainder > 0 ? 1 : 0)];
        int index = 0;
        if (quotient > 0) {
            bytes[index++] = (byte) (value & 0x7FL); // last octet
            value >>= 7;
        }
        for (int i = 1; i < quotient; i++) {
            bytes[index++] = (byte) (0x80L | (value & 0x7FL)); // intermediate octets
            value >>= 7;
        }
        if (remainder > 0) {
            bytes[index++] = (byte) ((quotient > 0 ? 0x80L : 0x00L) | (value & 0x7FL)); // first octet
        }
        for (int i = index - 1; i >= 0; i--) {
            output.writeInt(true, Byte.SIZE, bytes[i]);
        }
    }
}
