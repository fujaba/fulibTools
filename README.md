# fulibTools - Additional features for fulib.

[![Build Status](https://travis-ci.org/fujaba/fulibTools.svg?branch=master)](https://travis-ci.org/fujaba/fulibTools)
[![Java CI](https://github.com/fujaba/fulibTools/workflows/Java%20CI/badge.svg)](https://github.com/fujaba/fulibTools/actions)
[![Download](https://api.bintray.com/packages/fujaba/maven/fulibTools/images/download.svg)](https://bintray.com/fujaba/maven/fulibTools/_latestVersion "Download")

FulibTools provides some additional features like class diagrams and object diagrams to the
[Fulib](https://github.com/fujaba/fulib) code generator.
These additional features require some large dependencies and as not every user benefits from them,
we separated them from Fulib into FulibTools.

Another big difference is that as of Fulib v1.2.0, it is not used as a main or test dependency any more.
FulibTools however serves as a library mostly for testing and should be used as a test dependency.

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
    testCompile group: 'org.fulib', name: 'fulibTools', version: '1.1.0'
}
```

## Features

### Class Diagrams

FulibTools can generate class diagrams from a Fulib `ClassModel`.
View the [`ClassDiagrams` JavaDocs](https://javadoc.io/doc/org.fulib/fulibTools/latest/org/fulib/tools/ClassDiagrams.html) to find out more.

### Object Diagrams

You can create Object Diagrams using the [`ObjectDiagrams`](https://javadoc.io/doc/org.fulib/fulibTools/latest/org/fulib/tools/ObjectDiagrams.html) utility.

### Code Fragments

This feature allows you to keep your documentation up-to-date by automatically copying code fragments between source files.
Check out the [`CodeFragments` JavaDocs](https://javadoc.io/doc/org.fulib/fulibTools/latest/org/fulib/tools/CodeFragments.html) for an extensive explanation.
