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
 * A codec for {@link UUID} values in Microsoft GUID binary byte order.
 *
 * <p>The first three UUID fields are encoded little-endian; the remaining eight octets are encoded in canonical
 * order.</p>
 *
 * @see UuidRfc9562
 */
public final class MicrosoftGuid
        implements BitReader<UUID>, BitWriter<UUID> {

    /**
     * The singleton instance of this codec.
     */
    public static final MicrosoftGuid INSTANCE = new MicrosoftGuid();

    private MicrosoftGuid() {
        super();
    }

    @Override
    public UUID read(final BitInput input) throws IOException {
        final byte[] bytes = UuidRfc9562Bytes.INSTANCE.read(input);
        reverse(bytes, 0, 4);
        reverse(bytes, 4, 2);
        reverse(bytes, 6, 2);
        return UuidRfc9562.fromBytes(bytes);
    }

    @Override
    public void write(final BitOutput output, final UUID value) throws IOException {
        final byte[] bytes = UuidRfc9562.toBytes(value);
        reverse(bytes, 0, 4);
        reverse(bytes, 4, 2);
        reverse(bytes, 6, 2);
        UuidRfc9562Bytes.INSTANCE.write(output, bytes);
    }

    private static void reverse(final byte[] bytes, final int offset, final int length) {
        for (int i = 0, j = length - 1; i < j; i++, j--) {
            final byte b = bytes[offset + i];
            bytes[offset + i] = bytes[offset + j];
            bytes[offset + j] = b;
        }
    }
}
