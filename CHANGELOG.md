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
