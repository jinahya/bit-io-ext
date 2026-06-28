package io.github.jinahya.bit.io.ext.miscellaneous;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.BitReader;
import com.github.jinahya.bit.io.BitWriter;
import com.github.jinahya.bit.io.miscellaneous.UuidRfc9562Bytes;

import java.io.IOException;

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
 * A codec for Microsoft GUID values in 16-octet binary byte order.
 *
 * <p>The first three UUID fields are encoded little-endian; the remaining eight octets are encoded in canonical
 * order. Values returned from {@link #read(BitInput)} and accepted by {@link #write(BitOutput, byte[])} are in RFC 9562
 * canonical byte order; this class performs the Microsoft GUID field-byte reversal at the I/O boundary.</p>
 *
 * @see UuidRfc9562Bytes
 * @see MicrosoftGuid
 */
public final class MicrosoftGuidBytes
        implements BitReader<byte[]>, BitWriter<byte[]> {

    /**
     * The number of octets in a Microsoft GUID value.
     */
    public static final int LENGTH = UuidRfc9562Bytes.LENGTH;

    /**
     * The singleton instance of this codec.
     */
    public static final MicrosoftGuidBytes INSTANCE = new MicrosoftGuidBytes();

    private MicrosoftGuidBytes() {
        super();
    }

    @Override
    public byte[] read(final BitInput input) throws IOException {
        final var value = UuidRfc9562Bytes.INSTANCE.read(input);
        reverse(value, 0, 4);
        reverse(value, 4, 2);
        reverse(value, 6, 2);
        return value;
    }

    @Override
    public void write(final BitOutput output, final byte[] value) throws IOException {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (value.length != LENGTH) {
            throw new IllegalArgumentException("value.length(" + value.length + ") != " + LENGTH);
        }
        final var reversed = value.clone();
        reverse(reversed, 0, 4);
        reverse(reversed, 4, 2);
        reverse(reversed, 6, 2);
        UuidRfc9562Bytes.INSTANCE.write(output, reversed);
    }

    private static void reverse(final byte[] bytes, final int offset, final int length) {
        for (var i = 0; i < length / 2; i++) {
            final var j = length - 1 - i;
            final var b = bytes[offset + i];
            bytes[offset + i] = bytes[offset + j];
            bytes[offset + j] = b;
        }
    }
}
