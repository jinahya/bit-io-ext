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

/**
 * A reader for reading {@code float} values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class FloatReader
        extends FloatBase
        implements BitReader<Float> {

    private static final class SignBitOnly
            extends FloatReader {

        private SignBitOnly() {
            super(FloatConstants.SIZE_MIN_EXPONENT, FloatConstants.SIZE_MIN_SIGNIFICAND);
        }

        int readBits(final BitInput input) throws IOException {
            return input.readInt(true, 1) << FloatConstants.SHIFT_SIGN_BIT;
        }

        @Override
        public Float read(final BitInput input) throws IOException {
            return Float.intBitsToFloat(readBits(input));
        }
    }

    /**
     * A reader for reading either {@code +.0f} or {@code -.0f} in a compressed manner.
     *
     * @implSpec The implementation read only {@code 1} bit for the <em>sign bit</em> and returns either
     * <code>0b<strong>0</strong>__00000000__00000000_00000000_0000_000<sub>2</sub></code> or
     * <code>0b<strong>1</strong>__00000000__00000000_00000000_0000_000<sub>2</sub></code>.
     * @see FloatWriter.CompressedZero
     * @see DoubleReader.CompressedZero
     */
    public static final class CompressedZero
            implements BitReader<Float> {

        private static final class Holder {

            private static final BitReader<Float> INSTANCE = new CompressedZero();

            private static final class Nullable {

                private static final BitReader<Float> INSTANCE = FilterBitReader.nullable(Holder.INSTANCE);

                private Nullable() {
                    throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
                }
            }

            private Holder() {
                throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance.
         *
         * @return the instance.
         */
        public static BitReader<Float> getInstance() {
            return Holder.INSTANCE;
        }

        /**
         * Returns the instance handles {@code null} values.
         *
         * @return the instance handles {@code null} values.
         */
        public static BitReader<Float> getInstanceNullable() {
            return Holder.Nullable.INSTANCE;
        }

        private CompressedZero() {
            super();
        }

        @Override
        public BitReader<Float> nullable() {
            return getInstanceNullable();
        }

        @Override
        public Float read(final BitInput input) throws IOException {
            return delegate.read(input);
        }

        private final BitReader<Float> delegate = new SignBitOnly();
    }

    /**
     * A reader for reading either {@link Float#NEGATIVE_INFINITY} or {@link Float#POSITIVE_INFINITY} in a compresses
     * manner.
     *
     * @implSpec This reader reads {@code 1} bit for the <em>sign bit</em>, and returns either
     * <code>0b<strong>0</strong>__11111111__00000000_00000000_0000_000<sub>2</sub></code> or
     * <code>0b<strong>1</strong>__11111111__00000000_00000000_0000_000<sub>2</sub></code>.
     * @see FloatWriter.CompressedInfinity
     * @see DoubleReader.CompressedInfinity
     */
    public static final class CompressedInfinity
            implements BitReader<Float> {

        private static final class Holder {

            private static final BitReader<Float> INSTANCE = new CompressedInfinity();

            private static final class Nullable {

                private static final BitReader<Float> INSTANCE = FilterBitReader.nullable(Holder.INSTANCE);

                private Nullable() {
                    throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
                }
            }

            private Holder() {
                throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance.
         *
         * @return the instance.
         */
        public static BitReader<Float> getInstance() {
            return Holder.INSTANCE;
        }

        /**
         * Returns the instance handles {@code null} values.
         *
         * @return the instance handles {@code null} values.
         */
        public static BitReader<Float> getInstanceNullable() {
            return Holder.Nullable.INSTANCE;
        }

        private CompressedInfinity() {
            super();
        }

        @Override
        public BitReader<Float> nullable() {
            return getInstanceNullable();
        }

        @Override
        public Float read(final BitInput input) throws IOException {
            return Float.intBitsToFloat(delegate.readBits(input) | FloatConstants.MASK_EXPONENT);
        }

        private final SignBitOnly delegate = new SignBitOnly();
    }

    private static final class SignificandOnly
            implements BitReader<Float> {

        private SignificandOnly(int significandSize) {
            super();
            this.significandSize = FloatConstraints.requireValidSignificandSize(significandSize);
            shift = FloatConstants.SIZE_SIGNIFICAND - this.significandSize;
        }

        private int readBits(final BitInput input) throws IOException {
            final int significandBits = input.readInt(true, significandSize) << shift;
            if (significandBits == 0) {
                throw new IOException("significand bits are all zeros");
            }
            return significandBits;
        }

        @Override
        public Float read(final BitInput input) throws IOException {
            return Float.intBitsToFloat(readBits(input));
        }

        private final int significandSize;

        private final int shift;
    }

    /**
     * A reader for reading {@code subnormal} values in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see FloatWriter.CompressedSubnormal
     * @see DoubleReader.CompressedSubnormal
     */
    public static class CompressedSubnormal
            implements BitReader<Float> {

        /**
         * Creates a new instance with specified number bits for the {@code significand} part.
         *
         * @param significandSize a number of bits for the significand part; between
         *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and
         *                        {@value FloatConstants#SIZE_SIGNIFICAND}, both inclusive.
         */
        public CompressedSubnormal(final int significandSize) {
            super();
            significandOnly = new SignificandOnly(significandSize);
        }

        private int readBits(final BitInput input) throws IOException {
            final int signBitOnlyBits = signBitOnly.readBits(input);
            final int significandBits = significandOnly.readBits(input);
            return signBitOnlyBits | significandBits;
        }

        @Override
        public Float read(final BitInput input) throws IOException {
            return Float.intBitsToFloat(readBits(input));
        }

        private final SignBitOnly signBitOnly = new SignBitOnly();

        private final SignificandOnly significandOnly;
    }

    /**
     * A reader for reading {@code NaN} values in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see FloatWriter.CompressedNaN
     * @see DoubleReader.CompressedNaN
     */
    public static class CompressedNaN
            implements BitReader<Float> {

        /**
         * Creates a new instance with specified number bits for the {@code significand} part.
         *
         * @param significandSize a number of bits for the significand part; between
         *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and
         *                        {@value FloatConstants#SIZE_SIGNIFICAND}, both inclusive.
         */
        public CompressedNaN(int significandSize) {
            super();
            compressedSubnormal = new CompressedSubnormal(significandSize);
        }

        @Override
        public Float read(final BitInput input) throws IOException {
            if (significandOnly) {
                return Float.intBitsToFloat(
                        compressedSubnormal.significandOnly.readBits(input) | FloatConstants.MASK_EXPONENT);
            }
            return Float.intBitsToFloat(compressedSubnormal.readBits(input) | FloatConstants.MASK_EXPONENT);
        }

        /**
         * Returns current value of {@code significandOnly} property.
         *
         * @return current value of {@code significandOnly} property.
         * @apiNote initial value of the property is {@code false}.
         */
        public boolean isSignificandOnly() {
            return significandOnly;
        }

        /**
         * Replaces current value of {@code significandOnly} property with specified value.
         *
         * @param significandOnly new value for the {@code significandOnly} property; {@code true} for not reading the
         *                        sign bit; {@code false} for reading the sign bit.
         */
        public void setSignificandOnly(final boolean significandOnly) {
            this.significandOnly = significandOnly;
        }

        /**
         * Invokes {@link #setSignificandOnly(boolean)} method with specified arguments and returns this object.
         *
         * @param readSignBit the argument to passed to the {@link #setSignificandOnly(boolean)} method.
         * @return this object.
         */
        public CompressedNaN significandOnly(final boolean readSignBit) {
            setSignificandOnly(readSignBit);
            return this;
        }

        /**
         * Configures this reader not to read the sign bit.
         *
         * @return this object.
         * @implSpec This method invokes the {@link #significandOnly(boolean)} method with {@code true}, and returns the
         * result.
         */
        public CompressedNaN significandOnly() {
            return significandOnly(true);
        }

        private final CompressedSubnormal compressedSubnormal;

        private boolean significandOnly;
    }

    /**
     * Creates a new instance with specified sizes of the exponent part and the significand part.
     *
     * @param exponentSize    the number of bits for the exponent part; between
     *                        {@value FloatConstants#SIZE_MIN_EXPONENT} and {@value FloatConstants#SIZE_EXPONENT}, both
     *                        inclusive.
     * @param significandSize the number of bits for the significand part; between
     *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and {@value FloatConstants#SIZE_SIGNIFICAND},
     *                        both inclusive.
     */
    public FloatReader(final int exponentSize, final int significandSize) {
        super(exponentSize, significandSize);
    }

    @Override
    public Float read(final BitInput input) throws IOException {
        BitIoObjects.requireNonNullInput(input);
        return input.readFloat(exponentSize, significandSize);
    }
}
