package org.fulib.tools;

public class CodeFencePipe implements Pipe
{
   public static final String NAME = "fenced";

   private static final String DEFAULT_LANGUAGE = "java";

   private final String defaultLanguage;

   public CodeFencePipe()
   {
      this(DEFAULT_LANGUAGE);
   }

   public CodeFencePipe(String defaultLanguage)
   {
      this.defaultLanguage = defaultLanguage;
   }

   @Override
   public String apply(String content, String arg)
   {
      final String lang = this.getLanguage(arg);
      return "```" + lang + "\n" + content + "```\n";
   }

   private String getLanguage(String arg)
   {
      final int index = arg.indexOf(':');
      if (index >= 0)
      {
         return arg.substring(index + 1);
      }
      return this.defaultLanguage;
   }
}
