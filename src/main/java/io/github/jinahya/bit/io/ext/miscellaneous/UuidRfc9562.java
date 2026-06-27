package io.github.jinahya.bit.io.ext.miscellaneous;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.BitReader;
import com.github.jinahya.bit.io.BitWriter;
import com.github.jinahya.bit.io.miscellaneous.UuidRfc9562Bytes;

import java.io.IOException;
import java.util.UUID;

/*-
 * #%L
 * bit-io-ext
 * %%
 * Copyright (C) 2020 Jinahya, Inc.
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

/**
 * A codec for {@link UUID} values in RFC 9562 canonical 16-octet byte order.
 *
 * @see UuidRfc9562Bytes
 */
public final class UuidRfc9562
        implements BitReader<UUID>, BitWriter<UUID> {

    /**
     * The singleton instance of this codec.
     */
    public static final UuidRfc9562 INSTANCE = new UuidRfc9562();

    private UuidRfc9562() {
        super();
    }

    @Override
    public UUID read(final BitInput input) throws IOException {
        return fromBytes(UuidRfc9562Bytes.INSTANCE.read(input));
    }

    @Override
    public void write(final BitOutput output, final UUID value) throws IOException {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        UuidRfc9562Bytes.INSTANCE.write(output, toBytes(value));
    }

    static UUID fromBytes(final byte[] value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (value.length != UuidRfc9562Bytes.LENGTH) {
            throw new IllegalArgumentException("value.length(" + value.length + ") != " + UuidRfc9562Bytes.LENGTH);
        }
        return new UUID(toLong(value, 0), toLong(value, Long.BYTES));
    }

    static byte[] toBytes(final UUID value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        final byte[] bytes = new byte[UuidRfc9562Bytes.LENGTH];
        putLong(bytes, 0, value.getMostSignificantBits());
        putLong(bytes, Long.BYTES, value.getLeastSignificantBits());
        return bytes;
    }

    private static long toLong(final byte[] bytes, final int offset) {
        long value = 0L;
        for (int i = 0; i < Long.BYTES; i++) {
            value <<= Byte.SIZE;
            value |= bytes[offset + i] & 0xFFL;
        }
        return value;
    }

    private static void putLong(final byte[] bytes, final int offset, final long value) {
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            bytes[offset + i] = (byte) (value >>> ((Long.BYTES - 1 - i) * Byte.SIZE));
        }
    }
}
