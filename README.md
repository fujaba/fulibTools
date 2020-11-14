# fulibTools - Additional features for fulib.

[![Build Status](https://travis-ci.org/fujaba/fulibTools.svg?branch=master)](https://travis-ci.org/fujaba/fulibTools)
[![Java CI](https://github.com/fujaba/fulibTools/workflows/Java%20CI/badge.svg)](https://github.com/fujaba/fulibTools/actions)
[![Download](https://api.bintray.com/packages/fujaba/maven/fulibTools/images/download.svg)](https://bintray.com/fujaba/maven/fulibTools/_latestVersion "Download")
[![javadoc](https://javadoc.io/badge2/org.fulib/fulibTools/javadoc.svg)](https://javadoc.io/doc/org.fulib/fulibTools)

fulibTools is a library for testing that complements the [fulib](https://github.com/fujaba/fulib) code generator.
It provides features like class and object diagrams as well as a mechanism for automatically updating code snippets in documentation.

## Installation

`build.gradle`:

```groovy
repositories {
    mavenCentral()
    jcenter()
}
```

```groovy
dependencies {
    // https://mvnrepository.com/artifact/org.fulib/fulibTools
    testCompile group: 'org.fulib', name: 'fulibTools', version: '1.4.0'
}
```

## Features

### Class Diagrams

fulibTools can generate class diagrams from a fulib `ClassModel`.
View the [`ClassDiagrams` JavaDocs](https://javadoc.io/doc/org.fulib/fulibTools/latest/org/fulib/tools/ClassDiagrams.html) to find out more.

### Object Diagrams

You can create Object Diagrams using the [`ObjectDiagrams`](https://javadoc.io/doc/org.fulib/fulibTools/latest/org/fulib/tools/ObjectDiagrams.html) utility.

### Code Fragments

This feature allows you to keep your documentation up-to-date by automatically copying code fragments between source files.
Check out the [`CodeFragments` JavaDocs](https://javadoc.io/doc/org.fulib/fulibTools/latest/org/fulib/tools/CodeFragments.html) for an extensive explanation.

## License

[MIT](LICENSE.md)
