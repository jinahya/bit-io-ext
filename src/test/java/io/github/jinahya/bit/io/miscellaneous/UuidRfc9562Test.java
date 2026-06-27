package io.github.jinahya.bit.io.miscellaneous;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.DefaultBitInput;
import com.github.jinahya.bit.io.DefaultBitOutput;
import com.github.jinahya.bit.io.StreamByteInput;
import com.github.jinahya.bit.io.StreamByteOutput;
import com.github.jinahya.bit.io.miscellaneous.UuidRfc9562;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class UuidRfc9562Test {

    @Test
    void read_ExpectedValue_KnownBytes() throws IOException {
        final UUID actual = UuidRfc9562.INSTANCE.read(input(canonicalBytes()));

        assertThat(actual).isEqualTo(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));
    }

    @Test
    void write_ExpectedBytes_KnownValue() throws IOException {
        final byte[] actual = write(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));

        assertThat(actual).containsExactly(canonicalBytes());
    }

    @Test
    void write_NullPointerException_NullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> UuidRfc9562.INSTANCE.write(output(), null))
                .withMessage("value is null");
    }

    private static BitInput input(final byte[] bytes) {
        return new DefaultBitInput(new StreamByteInput(new ByteArrayInputStream(bytes)));
    }

    private static BitOutput output() {
        return new DefaultBitOutput(new StreamByteOutput(new ByteArrayOutputStream()));
    }

    private static byte[] write(final UUID value) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final BitOutput output = new DefaultBitOutput(new StreamByteOutput(baos));
        UuidRfc9562.INSTANCE.write(output, value);
        output.align(1);
        return baos.toByteArray();
    }

    private static byte[] canonicalBytes() {
        return new byte[]{
                0x01, 0x23, 0x45, 0x67,
                (byte) 0x89, (byte) 0xAB,
                (byte) 0xCD, (byte) 0xEF,
                0x01, 0x23,
                0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
        };
    }
}
