# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

bit-io2 is a Java library for reading/writing non-octet-aligned (arbitrary bit-length) values. It has **zero runtime dependencies**. Source targets Java 8; tests require Java 17.

## Build Commands

```bash
./mvnw clean verify                    # build + test
./mvnw test -Dtest=BitIo_Boolean_Test  # single test class
./mvnw verify -P failsafe              # integration tests
./mvnw javadoc:javadoc                 # generate javadoc
```

JDK 17+ is required to build (enforced by maven-enforcer-plugin).

## Architecture

### Layered design (bottom-up)

1. **Byte I/O** — `ByteInput` / `ByteOutput` functional interfaces with implementations for InputStream, ByteBuffer, ReadableByteChannel, DataInput, RandomAccessFile (`StreamByteInput`, `BufferByteInput`, `ChannelByteInput`, etc.)

2. **Bit I/O** — `BitInput` / `BitOutput` interfaces. `ByteInputAdapter` / `ByteOutputAdapter` bridge byte-level to bit-level. Factory classes (`BitInputFactory`, `BitOutputFactory`) create instances from various sources.

3. **Typed readers/writers** — `BitReader<T>` / `BitWriter<T>` functional interfaces with specializations: `IntReader`, `LongReader`, `FloatReader`, `DoubleReader`, `StringReader`, `ByteArrayReader`, `ListReader` (and matching writers). `FloatReader`/`DoubleReader` (with `FloatBase`/`DoubleBase` and `*Constants`/`*Constraints` helpers) implement IEEE-754 bit-level compression of special values (zero, subnormal, infinity, NaN-significand-only); `StringReader`/`ByteArrayReader` implement ASCII/UTF-8 compression. These compression variants are the bulk of the test surface.

4. **Miscellaneous encodings** — `com.github.jinahya.bit.io.miscellaneous` subpackage: VLQ and LEB128 readers/writers, exposed via `MiscellaneousBitInput` / `MiscellaneousBitOutput`.

### Core method signatures

```java
// BitInput
int readInt(boolean unsigned, int size) throws IOException;
long readLong(boolean unsigned, int size) throws IOException;

// BitOutput
void writeInt(boolean unsigned, int size, int value) throws IOException;
void writeLong(boolean unsigned, int size, long value) throws IOException;
```

`size` = number of bits; `unsigned` = true for unsigned values.

### Key patterns

- **Adapter**: ByteInputAdapter wraps ByteInput to implement BitInput
- **Factory**: BitInputFactory/BitOutputFactory create adapters from streams/buffers/channels
- **Decorator**: FilterBitReader/FilterBitWriter for composable behavior (nullable, mapping)
- **Constraint validation**: `BitIoConstraints` centralizes bit-size validation
- **Byte alignment**: `align(bytes)` pads/discards bits to a byte boundary; `BitOutput` must be aligned before its underlying byte sink is complete. Both `BitInput`/`BitOutput` support `reset()`.

## Testing

- JUnit 5 + Mockito + AssertJ + ArchUnit
- Test naming: `ClassName_Scenario_Test.java` (e.g., `Float_Wr_CompressedZero_Test.java`). The `_Wr_` infix marks write-then-read round-trip tests — the dominant test style (write a value, read it back, assert equality).
- Parameterized tests with random value generation (`BitIoRandom`, `BitIoTestUtils`)

## Code Style

- Apache License 2.0 headers on all source files
- Google Java Format is **disabled** (manual formatting)
- Compiler `-Xlint` warnings enabled
- Static analysis: SpotBugs, Checkstyle, PMD
- Module name: `com.github.jinahya.bit.io`

## CI

GitHub Actions on push/PR to `develop` and `master`: JDK 17, Maven build + SonarCloud analysis.
