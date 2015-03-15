---
layout: default
title: Change log
---

# Change Log

## 1.2.1 (2014-11-19)

   * Fix javadoc for building under Java 8 ([Issue 18](https://github.com/reckart/tt4j/issues/18)).
   * Allow setting flush sequence in DefaultModel ([Issue 19](https://github.com/reckart/tt4j/issues/19)).

## 1.2.0 (2014-08-17)

   * Changed license to Apache License version 2. ([Issue 17](https://github.com/reckart/tt4j/issues/17))
   * Note that TreeTagger itself is subject different license terms available from the [TreeTagger][treetagger] TreeTagger website.

## 1.1.2 (2014-02-14)

   * Fixed bug: model files cannot be read unless assertions are enabled ([Issue 16](https://github.com/reckart/tt4j/issues/16))

## 1.1.1 (2012-07-14)

   * Initial support for reading TreeTagger model files. ([Issue 14](https://github.com/reckart/tt4j/issues/14))

## 1.1.0 (2012-04-28)

   * Support for getting multiple tag/lemmas and their probabilities. This feature requires a TreeTagger binary **newer than 2012-04-25**. When used with previous versions, it will just hang. At the time of writing, the TreeTagger versions for OS X (Intel), Windows and Linux support this feature. It is possible that the versions for Solaris and OS X (PPC) may not be updated to support this feature. TT4J continues to work with other/older TreeTagger versions as long as this feature is not used. ([Issue 13](https://github.com/reckart/tt4j/issues/13))
   * Improved parsing of TreeTagger output.

## 1.0.16 (2011-10-24)

   * Changed default flush sequence to work with the TreeTagger model for chinese (Issue 6 - thanks Jérôme)
 
## 1.0.15 (2011-06-03)

   * Added detection if communication with TreeTagger starts running out-of-sync due to some odd characters appearing in tokens. This can be disabled, but per default the strict-mode is on.
   * Added setting for the maximal token length (default 90000 bytes) - TreeTagger seems to have a limit of 99998 bytes per token and crashes when this is exceeded
   * Improved handling of crashed TreeTagger process

## 1.0.14 (2010-11-12)

  * Generate a better exception message when no model or executable could be found.
  * Provide getters for all properties of TreeTaggerWrapper.
  * Updated license header and added it where missing.
  * Fixed typo in Javadoc.
  * Set plugin versions in POM to make Maven 3 happy.
  * Set source encoding to UTF-8 in POM.

## 1.0.13 (2010-10-13)

   * Fixed TT4J hanging indefinitely when writer thread crashes during processing.
   * Reader and writer threads are now monitored during processing.

## 1.0.12 (2009-09-19)

   * Fixed regression: Linux accidentially detected as Solaris.

## 1.0.11 (2009-09-18)

   * Fixed bug: DefaultModelResolver uses wrong file name.
   * Fixed bug: DefaultModelResolver fails on Windows when path contains a colon.

## 1.0.10 (2009-09-28)

   * Fixed bug: Resource not properly destroyed when an exception is thrown in reader/writer thread.
   * Improvement: Try harder to get to end-of-text mark.
   * Improvement: Added tracing of start and end marks.

## 1.0.9 (2009-08-27)

   * Improvement: Massively improved throughput when processing a large number of documents.
   * Improvement: Try to gracefully handle cases where TT does not produce a "token tag lemma" line. Return null for tag and lemma in these cases.

## 1.0.8 (2009-08-14)

   * Improvement: Ease integration of custom model resolvers.

## 1.0.7 (2009-08-07)

   * Improvement: Added tracing.
   * Improvement: Improved robustness ignoring illegal tokens (e.g. containing tabs or line breaks).
   * Improvement: Added performance mode which does not check for illegal tokens.

## 1.0.6 (2009-08-03)

   * Improvement: Allow setting the parameters -eps and -hyphen-heuristics needed to use TT4J with chunker models. Now a chunker can be build on top of TT4J.

[treetagger]: http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/ 