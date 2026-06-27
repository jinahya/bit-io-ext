# bit-io-ext

[![GitHub Action](https://github.com/jinahya/bit-io-ext/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/jinahya/bit-io-ext/actions?workflow=Java+CI)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_bit-io-ext&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jinahya_bit-io-ext)
![Maven Central](https://img.shields.io/maven-central/v/io.github.jinahya/bit-io-ext)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/bit-io-ext/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/bit-io-ext)

A Java 8+ flavored version of [bit-io](https://github.com/jinahya/bit-io).

## How to use?

Add this module as a dependency. Check the [central](https://search.maven.org/search?q=g:io.github.jinahya%20a:bit-io-ext) for the current version.

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>bit-io-ext</artifactId>
</dependency>
```

```java
OutputStream stream = outputStream();
BitOutput output = BitOutputFactory.from(stream);
output.writeBoolean(true);           // 1 bit   1
output.writeInt(true, 3, 1);         // 3 bits  4
output.writeLong(false, 37, 0L);     // 37 bits 41        
long padded = output.align(1);
assert padded == 7L;
assert (padded + 41) % Byte.SIZE == 0;

InputStream stream = inputStream();
BitInput input = BitInputFactory.from(stream);
assert input.readBoolean();             // 1 bit   1
assert 1 == input.readInt(true, 3);     // 3 bits  4
assert 0L == input.readLong(false, 37); // 37 bits 41
long discarded = input.align(1);
assert discarded == 7L;
assert (discarded + 41) % Byte.SIZE == 0;
```

See [Specifications](https://github.com/jinahya/bit-io-ext/wiki/Specifications) and [Recipes](https://github.com/jinahya/bit-io-ext/wiki/Recipes) for more information.
