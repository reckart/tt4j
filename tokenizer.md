---
layout: default
title: Tokenizer
---

# Simple tokenizer using the Java BreakIterator

Sometimes users ask for TT4J to include a tokenizer. I will not include a ready-to-use tokenizer with TT4J, since there are other libraries that do a much better job here. A good tokenizer for English for example is included with the [Stanford Parser][stanford-parser]. 

If you do not wish to look for a good tokenizer for your task, you may find this method useful. It uses a simple tokenizer called !BreakIterator which ships with Java.

{% highlight java %}
	public 
	List<String> tokenize(
			final String aString)
	{
		List<String> tokens = new ArrayList<String>();
		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(aString);
		int begin = bi.first();
		int end;
		for (end = bi.next(); end != BreakIterator.DONE; end = bi.next()) {
			String t = aString.substring(begin, end);
			if (t.trim().length() > 0) {
				tokens.add(aString.substring(begin, end));
			}
			begin = end;
		}
		if (end != -1) {
			tokens.add(aString.substring(end));
		}
		return tokens;
	}
{% endhighlight %}

[stanford-parser]: http://nlp.stanford.edu/software/lex-parser.shtml