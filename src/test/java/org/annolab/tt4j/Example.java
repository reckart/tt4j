package org.annolab.tt4j;

public class Example
{
	public static void main(String[] args) throws Exception
	{
		// Point TT4J to the TreeTagger installation directory. The executable is expected
		// in the "bin" subdirectory - in this example at "/opt/treetagger/bin/tree-tagger"
		System.setProperty("treetagger.home", "/opt/treetagger");
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		try {
			tt.setModel("/opt/treetagger/models/english.par:iso8859-1");
			tt.setHandler(new TokenHandler<String>()
			{
				public void token(String token, String pos, String lemma)
				{
					System.out.println(token + "\t" + pos + "\t" + lemma);
				}
			});
			tt.process(new String[] { "This", "is", "a", "test", "." });
		}
		finally {
			tt.destroy();
		}
	}
}
