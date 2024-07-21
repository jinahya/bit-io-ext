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

import java.io.IOException;
import java.util.Arrays;

/**
 * Utilities for reading/writing bits.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class BitIoUtils {

    private static final int SIZE_COUNT = 31;

    /**
     * Reads a {@code 31}-bit unsigned count value from specified input.
     *
     * @param input the input from which the count value is read.
     * @return a count value read.
     * @throws IOException if an I/O error occurs.
     */
    public static int readCount(final BitInput input) throws IOException {
        BitIoObjects.requireNonNullInput(input);
        return input.readInt(true, SIZE_COUNT);
    }

    /**
     * Writes specified count value as a {@code 31}-bit unsigned value to specified output.
     *
     * @param output the output to which the {@code count} is written.
     * @param count  the value to write.
     * @throws IOException if an I/O error occurs.
     */
    public static void writeCount(final BitOutput output, final int count) throws IOException {
        BitIoObjects.requireNonNullOutput(output);
        if (count < 0) {
            throw new IllegalArgumentException("negative count: " + count);
        }
        output.writeInt(true, SIZE_COUNT, count);
    }

    private static final int SIZE_SIZE_COUNT_COMPRESSED = 4;

    /**
     * Writes specified count value to specified output, in a compressed manner.
     *
     * @param output the output to which the {@code count} value is written.
     * @param count  the value to write.
     * @throws IOException if an I/O error occurs.
     */
    public static void writeCountCompressed(final BitOutput output, final int count) throws IOException {
        BitIoObjects.requireNonNullOutput(output);
        if (count < 0) {
            throw new IllegalArgumentException("negative count: " + count);
        }
        if (count == 0) {
            output.writeBoolean(true); // zero
            return;
        }
        output.writeBoolean(false); // not zero
        if (count <= 65536) {
            output.writeBoolean(true); // compressed
            int size = Integer.SIZE - Integer.numberOfLeadingZeros(count - 1);
            if (size == 0) {
                size++;
            }
            output.writeInt(true, SIZE_SIZE_COUNT_COMPRESSED, size - 1); // 4 bits for [0..15]
            output.writeInt(true, size, count - 1); // 16 bits in maximum
            return;
        }
        output.writeBoolean(false); // uncompressed
        writeCount(output, count);
    }

    /**
     * Reads a count value from specified input, in a compressed-manner.
     *
     * @param input the input from which the count value is read.
     * @return the count value read.
     * @throws IOException if an I/O error occurs.
     */
    public static int readCountCompressed(final BitInput input) throws IOException {
        BitIoObjects.requireNonNullInput(input);
        if (input.readBoolean()) { // zero
            return 0;
        }
        if (input.readBoolean()) { // compressed
            final int size = input.readInt(true, SIZE_SIZE_COUNT_COMPRESSED) + 1; // [0..15] + 1 -> [1..16]
            final int count = input.readInt(true, size) + 1;
            return count;
        }
        return readCount(input);
    }

    private static final int[] BIT_MASKS = new int[30]; // (size -1) 를 피하기 위해 [0] 은 버린다.

    static {
        Arrays.fill(BIT_MASKS, 0);
    }

    static int bitMaskSingle(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("non-positive size: " + size);
        }
        if (size > Integer.SIZE) {
            throw new IllegalArgumentException("size(" + size + ") > " + Integer.SIZE);
        }
        if (size == 1) {
            return 0x00000001;
        }
        if (size == Integer.SIZE) {
            return 0xFFFFFFFF;
        }
        final int index = size - 2; // size = 2 -> index = 0;
        final int bitMask = BIT_MASKS[index];
        if (bitMask > 0) {
            return bitMask;
        }
        BIT_MASKS[index] = -1 >>> (Integer.SIZE - size);
        return bitMaskSingle(size);
    }

    static long bitMaskDouble(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("non-positive size: " + size);
        }
        if (size > Long.SIZE) {
            throw new IllegalArgumentException("size(" + size + ") > " + Long.SIZE);
        }
        if (size == Long.SIZE) {
            return 0xFFFFFFFFFFFFFFFFL;
        }
        if (size <= Integer.SIZE) {
            return bitMaskSingle(size) & 0xFFFFFFFFL;
        }
        return (bitMaskDouble(size - Integer.SIZE) << Integer.SIZE) | bitMaskDouble(Integer.SIZE);
    }

    private BitIoUtils() {
        throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
    }
}
