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
 * A writer for writing {@code float} values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class FloatWriter
        extends FloatBase
        implements BitWriter<Float> {

    private static final class SignBitOnly
            extends FloatWriter {

        private SignBitOnly() {
            super(FloatConstants.SIZE_MIN_EXPONENT, FloatConstants.SIZE_MIN_SIGNIFICAND);
        }

        private void writeBits(final BitOutput output, final int bits) throws IOException {
            output.writeInt(true, 1, bits >> FloatConstants.SHIFT_SIGN_BIT);
        }

        private void writeBits(final BitOutput output, final float value) throws IOException {
            writeBits(output, Float.floatToRawIntBits(value));
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            writeBits(output, value);
        }
    }

    /**
     * A writer for writing {@code ±0.0f} in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see FloatReader.CompressedZero
     * @see FloatWriter.CompressedZero
     */
    public static final class CompressedZero
            implements BitWriter<Float> {

        private static final class Holder {

            private static final BitWriter<Float> INSTANCE = new CompressedZero();

            private static final class Nullable {

                private static final BitWriter<Float> INSTANCE = FilterBitWriter.nullable(Holder.INSTANCE);

                private Nullable() {
                    throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
                }
            }

            private Holder() {
                throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance of this class.
         *
         * @return the instance.
         * @see #getInstanceNullable()
         */
        public static BitWriter<Float> getInstance() {
            return Holder.INSTANCE;
        }

        /**
         * Returns the instance handles {@code null} values.
         *
         * @return the instance handles {@code null} values.
         * @see #getInstance()
         */
        public static BitWriter<Float> getInstanceNullable() {
            return Holder.Nullable.INSTANCE;
        }

        private CompressedZero() {
            super();
        }

        @Override
        public BitWriter<Float> nullable() {
            return getInstanceNullable();
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            delegate.write(output, value);
        }

        private final BitWriter<Float> delegate = new SignBitOnly();
    }

    /**
     * A writer for writing either {@link Float#NEGATIVE_INFINITY} or {@link Float#POSITIVE_INFINITY}.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see FloatReader.CompressedInfinity
     * @see DoubleWriter.CompressedInfinity
     */
    public static final class CompressedInfinity
            implements BitWriter<Float> {

        private static final class Holder {

            private static final BitWriter<Float> INSTANCE = new CompressedInfinity();

            private static final class Nullable {

                private static final BitWriter<Float> INSTANCE = FilterBitWriter.nullable(Holder.INSTANCE);

                private Nullable() {
                    throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
                }
            }

            private Holder() {
                throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance of this class which is singleton.
         *
         * @return the instance.
         * @see #getInstanceNullable()
         */
        public static BitWriter<Float> getInstance() {
            return Holder.INSTANCE;
        }

        /**
         * Returns the instance handles {@code null} values.
         *
         * @return the instance handles {@code null} values.
         * @see #getInstance()
         */
        public static BitWriter<Float> getInstanceNullable() {
            return Holder.Nullable.INSTANCE;
        }

        private CompressedInfinity() {
            super();
        }

        @Override
        public BitWriter<Float> nullable() {
            return getInstanceNullable();
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            delegate.write(output, value);
        }

        private final BitWriter<Float> delegate = new SignBitOnly();
    }

    private static final class SignificandOnly
            implements BitWriter<Float> {

        private SignificandOnly(final int significandSize) {
            super();
            this.significandSize = FloatConstraints.requireValidSignificandSize(significandSize);
            shift = FloatConstants.SIZE_SIGNIFICAND - this.significandSize;
            mask = BitIoUtils.bitMaskSingle(this.significandSize);
        }

        private void writeBits(final BitOutput output, final int bits) throws IOException {
            final int significandBits = (bits >> shift) & mask;
            if (significandBits == 0) {
                throw new IllegalArgumentException("significand bits are all zeros");
            }
            output.writeInt(true, significandSize, significandBits);
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            final int bits = Float.floatToRawIntBits(value);
            writeBits(output, bits);
        }

        private final int significandSize;

        private final int shift;

        private final int mask;
    }

    /**
     * A writer for writing {@code subnormal} values in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see FloatReader.CompressedSubnormal
     * @see DoubleWriter.CompressedSubnormal
     */
    public static class CompressedSubnormal
            implements BitWriter<Float> {

        /**
         * Creates a new instance with specified significand size.
         *
         * @param significandSize the number of left-most significand bits to write; between
         *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and
         *                        {@value FloatConstants#SIZE_SIGNIFICAND}, both inclusive.
         */
        public CompressedSubnormal(final int significandSize) {
            super();
            significandOnly = new SignificandOnly(significandSize);
        }

        private void writeBits(final BitOutput output, final int bits) throws IOException {
            signBitOnly.writeBits(output, bits);
            significandOnly.writeBits(output, bits);
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            final int bits = Float.floatToRawIntBits(value);
            writeBits(output, bits);
        }

        private final SignBitOnly signBitOnly = new SignBitOnly();

        private final SignificandOnly significandOnly;
    }

    /**
     * A writer for writing {@code NaN} values in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see FloatReader.CompressedNaN
     * @see DoubleWriter.CompressedNaN
     */
    public static class CompressedNaN
            implements BitWriter<Float> {

        /**
         * Creates a new instance with specified significand size.
         *
         * @param significandSize the number of left-most significand bits to write; between
         *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and
         *                        {@value FloatConstants#SIZE_SIGNIFICAND}, both inclusive.
         */
        public CompressedNaN(final int significandSize) {
            super();
            compressedSubnormal = new CompressedSubnormal(significandSize);
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            if (significandOnly) {
                compressedSubnormal.significandOnly.write(output, value);
                return;
            }
            compressedSubnormal.write(output, value);
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
         * @param significandOnly new value for the {@code significandOnly} property; {@code true} for writing the
         *                        <em>significand</em> part only; {@code false} for including the <em>sign bit</em>.
         */
        public void setSignificandOnly(final boolean significandOnly) {
            this.significandOnly = significandOnly;
        }

        /**
         * Sets the {@code significandOnly} property with specified value.
         *
         * @param significandOnly new value for the {@code significandOnly} property; {@code true} for not writing the sign
         *                        bit, {@code false} otherwise.
         * @return this object.
         * @implSpec This method invokes the {@link #setSignificandOnly(boolean)} method with {@code significandOnly},
         * and returns this object.
         */
        public CompressedNaN significandOnly(final boolean significandOnly) {
            setSignificandOnly(significandOnly);
            return this;
        }

        /**
         * Configures this writer to write the <em>significand</em> part only.
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
     * Creates a new instance with specified exponent size and significand size.
     *
     * @param exponentSize    the number of bits for the exponent part; between
     *                        {@value FloatConstants#SIZE_MIN_EXPONENT} and {@value FloatConstants#SIZE_EXPONENT}, both
     *                        inclusive.
     * @param significandSize the number of bits for the significand part; between
     *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and {@value FloatConstants#SIZE_SIGNIFICAND},
     *                        both inclusive.
     */
    public FloatWriter(final int exponentSize, final int significandSize) {
        super(exponentSize, significandSize);
    }

    @Override
    public void write(final BitOutput output, final Float value) throws IOException {
        BitIoObjects.requireNonNullOutput(output);
        Objects.requireNonNull(value, "value is null");
        output.writeFloat(exponentSize, significandSize, value);
    }
}
