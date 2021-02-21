# fulibTools v1.0.0

# fulibTools v1.0.1

# fulibTools v1.0.2

# fulibTools v1.1.0

* Bumped version number.

# fulibTools v1.2.0

## General

+ Added a license.
* Updated to fulib v1.2.0.

## New Features

+ Added pipes, which allow customizing the output of code fragments. #11
+ Added support for defining manual code fragments that are not present in code, e.g. for capturing output. #9
+ Added customizable scaling for PNG and SVG diagrams. #4

## Improvements

* Files with code fragments are no longer overridden when their content didn't change. #5
* Usage of code fragments that are not defined now prints a warning to stderr. #6
* Code fragment insertions now retain their indentation. #8
* Redefining code fragments now prints a warning to stderr. #10
* Lambda expressions in attributes are now rendered specially. #12
* Improved class diagram rendering performance. #13

## Bugfixes

* HTML special characters in attribute values no longer produce incorrectly escaped text. #3

# fulibTools v1.2.1

## General

* Updated to fulib v1.2.1.

## Improvements

* Improved object diagram rendering performance. #14 #18
* The title of object diagrams is now the package name of the displayed objects' types. #19
* The `ObjectDiagrams.dumpYaml` method now automatically creates parent directories of the target file. #23 #24

## Bugfixes

* Fixed an exception when attempting to generate class diagrams for models with attributes of generic types. #21 #22

# fulibTools v1.3.0

## General

* Updated to fulib v1.3.0

## New Features

+ Class Diagrams now include edges for inheritance. #15

# fulibTools v1.4.0

## General

* Updated to fulib v1.4.1. #29

## Improvements

* Attribute types in class diagrams are now simplified if the original type has `import(...)` syntax or annotations. #29

## Bugfixes

* Fixed an exception when generating diagrams for classes or objects named like Graphviz keywords. #27 #28

# fulibTools v1.4.1

## Bugfixes

* Object diagrams now include properties from super classes. #30
* Collection attributes now show the collection type in class diagrams. #31
* Object diagrams now show collection attributes if they contain at least one data value. #31

# fulibTools v1.5.0

## General

* Updated to fulib v1.5.1.

## Improvements

* Class and object diagrams are now generated deterministically. #32
