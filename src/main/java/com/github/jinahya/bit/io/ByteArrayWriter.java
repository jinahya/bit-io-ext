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
import java.util.function.ObjIntConsumer;

/**
 * A value write for writing arrays of bytes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ByteArrayReader
 */
public class ByteArrayWriter
        implements BitWriter<byte[]>,
                   CountWriter {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * .
     *
     * @see ByteArrayReader.Unsigned
     */
    static class Unsigned
            extends ByteArrayWriter {

        @SuppressWarnings({
                "java:S1700" // UNSIGNED <> Unsigned
        })
        private static final boolean UNSIGNED = true; // NOSONAR

        Unsigned(final int elementSize) {
            super(BitIoConstraints.requireValidSizeForByte(UNSIGNED, elementSize));
        }

        @Override
        void writeElement(final BitOutput output, final byte value) throws IOException {
            output.writeInt(UNSIGNED, getElementSize(), value);
        }
    }

    /**
     * Creates a new instance for writing arrays of unsigned bytes.
     *
     * @param elementSize a number of bits for each element in the array; between {@code 1} (inclusive) and
     *                    {@value Byte#SIZE} (exclusive).
     * @return a new instance.
     * @see ByteArrayReader#unsigned(int)
     */
    public static ByteArrayWriter unsigned(final int elementSize) {
        return new Unsigned(elementSize);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * .
     *
     * @see ByteArrayReader.CompressedAscii
     */
    static class CompressedAscii
            extends Unsigned {

        /**
         * .
         *
         * @see ByteArrayReader.CompressedAscii.PrintableOnly
         */
        // https://en.wikipedia.org/wiki/ASCII#Printable_characters
        static class PrintableOnly
                extends CompressedAscii {

            private PrintableOnly() {
                super();
            }

            @Override
            void writeElement(final BitOutput output, final byte value) throws IOException {
                if (value < 0x60) {
                    output.writeBoolean(true);
                    output.writeInt(true, 6, value - 0x20);
                    return;
                }
                output.writeBoolean(false);
                output.writeInt(true, 5, value - 0x60);
            }
        }

        private CompressedAscii() {
            super(7);
        }

        @Override
        void writeElement(final BitOutput output, final byte value) throws IOException {
            output.writeByte(true, getElementSize(), value);
        }
    }

    /**
     * Creates a new instance for writing an array of ASCII bytes.
     *
     * @param printableOnly a flag for printable characters only; {@code true} for printable ascii characters only;
     *                      {@code false} otherwise.
     * @return a new instance.
     * @see ByteArrayReader.CompressedAscii#compressedAscii(boolean)
     */
    public static ByteArrayWriter compressedAscii(final boolean printableOnly) {
        if (printableOnly) {
            return new CompressedAscii.PrintableOnly();
        }
        return new CompressedAscii();
    }

//    /**
//     * Creates a new instance, for writing arrays of ASCII bytes, which writes an unsigned {@code 31}-bit {@code int}
//     * value for the length of arrays.
//     *
//     * @param printableOnly a flag for printable characters only; {@code true} for printable ascii characters only;
//     *                      {@code false} otherwise.
//     * @return a new instance.
//     */
//    public static ByteArrayWriter compressedAscii31(final boolean printableOnly) {
//        return compressedAscii(printableOnly);
//    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A writer for writing arrays of UTF-8 bytes in a compressed manner.
     *
     * @see ByteArrayReader.CompressedUtf8
     */
    static class CompressedUtf8
            extends ByteArrayWriter {

        private CompressedUtf8() {
            super(Byte.SIZE);
        }

        @Override
        void writeElements(final BitOutput output, final byte[] value) throws IOException {
            for (int i = 0; i < value.length; i++) {
                final int b = value[i] & 0xFF;
                if ((b & 0b0111_1111) == b) { // 0xxx_xxxx
                    output.writeInt(true, 2, 0b00);
                    output.writeInt(true, 7, b & 0xFF);
                    continue;
                }
                if ((b & 0b1101_1111) == b) { // 110x_xxxx
                    output.writeInt(true, 2, 0b01);
                    output.writeInt(true, 5, b);
                    output.writeInt(true, 6, value[++i] & 0xFF);
                    continue;
                }
                if ((b & 0b1110_1111) == b) { // 1110_xxxx
                    output.writeInt(true, 2, 0b10);
                    output.writeInt(true, 4, b);
                    output.writeInt(true, 6, value[++i] & 0xFF);
                    output.writeInt(true, 6, value[++i] & 0xFF);
                    continue;
                }
                output.writeInt(true, 2, 0b11);
                output.writeInt(true, 3, b);
                output.writeInt(true, 6, value[++i] & 0xFF);
                output.writeInt(true, 6, value[++i] & 0xFF);
                output.writeInt(true, 6, value[++i] & 0xFF);
            }
        }
    }

    /**
     * Creates a new instance writes UTF-8 byte arrays in a compressed mannager.
     *
     * @return a new instance.
     * @see ByteArrayReader#compressedUtf8()
     */
    public static ByteArrayWriter compressedUtf8() {
        return new CompressedUtf8();
    }

    /**
     * Creates a new instance.
     *
     * @param elementSize a number of bits for each element in an array; between {@code 1} and {@value Byte#SIZE}, both
     *                    inclusive.
     */
    ByteArrayWriter(final int elementSize) {
        super();
        this.elementSize = BitIoConstraints.requireValidSizeForByte(false, elementSize);
    }

    @Override
    public void write(final BitOutput output, final byte[] value) throws IOException {
        Objects.requireNonNull(value, "value is null");
        countWriter.accept(output, value.length);
        writeElements(output, value);
    }

    void writeElements(final BitOutput output, final byte[] elements) throws IOException {
        for (final byte element : elements) {
            writeElement(output, element);
        }
    }

    void writeElement(final BitOutput output, final byte element) throws IOException {
        output.writeByte(false, elementSize, element);
    }

    int getElementSize() {
        return elementSize;
    }

    @Override
    public void setCountWriter(final ObjIntConsumer<? super BitOutput> countWriter) {
        this.countWriter = Objects.requireNonNull(countWriter, "countWriter is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final int elementSize;

    private ObjIntConsumer<? super BitOutput> countWriter = CountWriter.COUNT_WRITER_31;
}
