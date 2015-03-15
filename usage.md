---
layout: default
title: How to use TT4J
---

# How to use TT4J

The main class is `TreeTaggerWrapper`. One TreeTagger process will be created and maintained for each instance of this class. The associated process will be terminated and restarted automatically if the model is changed (`setModel(String)`). Otherwise the process remains running, in the background once it is started which saves a lot of time. The process remains dormant while not used and only consumes some memory, but no CPU while it is not used.

During analysis, two threads are used to communicate with the TreeTagger. One process writes tokens to the TreeTagger process, while the other receives the analyzed tokens.

## Analyzing tokens

For easy integration into application, this class takes any object containing token information and either uses its `toString()` method or a `TokenAdapter` set using `setAdapter(TokenAdapter)` to extract the actual token. To receive the an analyzed token, set a custom `TokenHandler` using `setHandler(TokenHandler)`.

{% highlight groovy %}
 TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
 try {
     tt.setModel("/treetagger/models/english.par:iso8859-1");
     tt.setHandler(new TokenHandler<String>() {
         public void token(String token, String pos, String lemma) {
             System.out.println(token+"\t"+pos+"\t"+lemma);
         }
     });
     tt.process(asList(new String[] {"This", "is", "a", "test", "."}));
 }
 finally {
     tt.destroy();
 }
{% endhighlight %}

## Getting probabilities

Since version 1.1.0, TT4J allows to fetch probabilities from TreeTagger. To make use of this feature, a `TokenHandler` implementing the `ProbabilityHandler` interface must be passed to `setHandler(TokenHandler)` and a probability threshold must be set using `setProbabilityThreshold(Double)`. This corresponds to specifying the arguments `-prob -threshold <value>` of TreeTagger. During processing, TT4J first invokes `TokenHandler.token()` with the token, best tag and best lemma. Afterwards `ProbabilityHandler.probability()` is invoked for each tag/lemma/probability returned by TreeTagger.

*Note:* This feature requires a TreeTagger binary newer than 2012-04-25. When used with previous version, it will just hang. At the time of writing, the TreeTagger versions for OS X (Intel), Windows and Linux support this feature. It is possible that the versions for Solaris and OS X (PPC) may not be updated to support this feature.

## Locating executables and models

Per default the TreeTagger executable is searched for in the directories indicated by the system property `treetagger.home`, the environment variables `TREETAGGER_HOME` and `TAGDIR` in this order. A full path to a model file optionally appended by a : and the model encoding is expected by the `setModel(String)` method.

For additional flexibility, register a custom `ExecutableResolver` using `setExecutableProvider(ExecutableResolver)` or a custom `ModelResolver` using `setModelProvider(ModelResolver)`. Custom providers may extract models and executable from archives or download them from some location and temporarily or permanently install them in the file system. A custom model resolver may also be used to resolve a language code (e.g. en) to a particular model.