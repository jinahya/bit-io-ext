# bit-io-ext

[![Java CI with Maven](https://github.com/jinahya/bit-io-ext/actions/workflows/maven.yml/badge.svg)](https://github.com/jinahya/bit-io-ext/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_bit-io-ext&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jinahya_bit-io-ext)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jinahya/bit-io-ext)](https://central.sonatype.com/artifact/io.github.jinahya/bit-io-ext)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/bit-io-ext/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/bit-io-ext)

A thin extension on top of [bit-io](https://github.com/jinahya/bit-io), targeting Java 8+.

As of now, this module adds nothing to bit-io's core reading/writing model — it only layers a few conveniences on top of it:

- `ExtendedFilterBitReader` / `ExtendedFilterBitWriter` — extended reader/writer filters with `mapping(...)` helpers.
- `io.github.jinahya.bit.io.ext.miscellaneous` codecs for `java.util.UUID`:
  - `UuidRfc9562` — RFC 9562 canonical (big-endian) byte order.
  - `MicrosoftGuid` — Microsoft GUID (mixed-endian) byte order.

  Each is just a `UUID` ↔ `byte[]` wrapper around the corresponding `…Bytes` codec (`UuidRfc9562Bytes` from bit-io, `MicrosoftGuidBytes` here); the actual bit-level I/O lives in bit-io.

## How to use?

Add this module as a dependency. Check the [central](https://search.maven.org/search?q=g:io.github.jinahya%20a:bit-io-ext) for the current version.

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>bit-io-ext</artifactId>
</dependency>
```

See [bit-io](https://github.com/jinahya/bit-io) for the core reading/writing API, and the
[Specifications](https://github.com/jinahya/bit-io-ext/wiki/Specifications) and
[Recipes](https://github.com/jinahya/bit-io-ext/wiki/Recipes) wiki pages for more information.
