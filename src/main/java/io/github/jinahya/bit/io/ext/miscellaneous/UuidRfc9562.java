package io.github.jinahya.bit.io.ext.miscellaneous;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.BitReader;
import com.github.jinahya.bit.io.BitWriter;
import com.github.jinahya.bit.io.miscellaneous.UuidRfc9562Bytes;

import java.io.IOException;
import java.nio.ByteBuffer;
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
 * <p>This class converts between {@link UUID} and canonical byte arrays, while {@link UuidRfc9562Bytes} performs the
 * byte-level I/O.</p>
 *
 * @see UuidRfc9562Bytes
 */
public final class UuidRfc9562
        implements BitReader<UUID>, BitWriter<UUID> {

    static UUID fromBytes(final byte[] value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (value.length != UuidRfc9562Bytes.LENGTH) {
            throw new IllegalArgumentException("value.length(" + value.length + ") != " + UuidRfc9562Bytes.LENGTH);
        }
        final var buffer = ByteBuffer.wrap(value);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    static byte[] toBytes(final UUID value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        final var bytes = new byte[UuidRfc9562Bytes.LENGTH];
        final var buffer = ByteBuffer.wrap(bytes);
        buffer.putLong(value.getMostSignificantBits());
        buffer.putLong(value.getLeastSignificantBits());
        return bytes;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The singleton instance of this codec.
     */
    public static final UuidRfc9562 INSTANCE = new UuidRfc9562();

    // -----------------------------------------------------------------------------------------------------------------
    private UuidRfc9562() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
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
}
