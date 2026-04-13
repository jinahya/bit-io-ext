package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io2
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

import java.io.IOException;
import java.util.Objects;

/**
 * An implementation of {@link BitInput} reads octets from an instance of {@link ByteInput}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ByteOutputAdapter
 */
public class ByteInputAdapter
        implements BitInput {

    /**
     * Creates a new instance on top of specified byte input.
     *
     * @param input the byte input.
     */
    public ByteInputAdapter(final ByteInput input) {
        super();
        this.input = Objects.requireNonNull(input, "input is null");
    }

    @Override
    public int readInt(final boolean unsigned, int size) throws IOException {
        BitIoConstraints.requireValidSizeForInt(unsigned, size);
        int value = 0;
        if (!unsigned) {
            value -= readInt(true, 1);
            if (--size > 0) {
                value <<= size;
                value |= readInt(true, size);
            }
            return value;
        }
        for (; size >= Byte.SIZE; size -= Byte.SIZE) {
            value <<= Byte.SIZE;
            value |= unsigned8(Byte.SIZE);
        }
        if (size > 0) {
            value <<= size;
            value |= unsigned8(size);
        }
        return value;
    }

    @Override
    public long align(final int bytes) throws IOException {
        if (bytes <= 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is not positive");
        }
        long bits = 0L; // the number of discarded bits
        if (available > 0) {
            bits += available; // must be prior to the below
            skip(available);
        }
        if (bytes == 1) {
            assert available == 0;
            return bits;
        }
        final int remainder = (int) (this.count % bytes);
        if (remainder > 0) {
            for (int i = bytes - remainder; i > 0; i--) {
                skip(Byte.SIZE);
                bits += Byte.SIZE;
            }
        }
        assert available == 0;
        return bits;
    }

    @Override
    public void reset() {
        if (available > 0) {
            throw new IllegalStateException("not aligned yet");
        }
        count = 0L;
    }

    /**
     * Reads an unsigned {@code int} value of specified number of bits.
     *
     * @param size the number of bits to read; between {@code 1} and {@value java.lang.Byte#SIZE}, both inclusive.
     * @return an unsigned {@code int} value.
     * @throws IOException if an I/O error occurs.
     */
    private int unsigned8(final int size) throws IOException {
        if (available == 0) {
            octet = input.read();
            count++;
            available = Byte.SIZE;
        }
        if (available == Byte.SIZE && size == Byte.SIZE) { // return, directly, a full 8-bit octet
            available = 0;
            return octet;
        }
        final int required = size - available;
        if (required > 0) {
            return (unsigned8(available) << required) | unsigned8(required);
        }
        available -= size;
        return (octet >> available) & BitIoUtils.bitMaskSingle(size);
    }

    private final ByteInput input;

    /**
     * The current octet.
     */
    private int octet;

    /**
     * The number of available bits in {@link #octet}.
     */
    private int available = 0;

    /**
     * The number of bytes read, from the {@link #input}, so far.
     */
    private long count;
}
