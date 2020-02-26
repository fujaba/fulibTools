package org.fulib.tools;

public class IndentPipe implements Pipe
{
   public static final String NAME = "indent";
   private static final String INSERT_INDENT = "    ";

   @Override
   public String apply(String content, String arg)
   {
      final StringBuilder result = new StringBuilder();
      for (String s : content.split(System.lineSeparator()))
      {
         result.append(INSERT_INDENT).append(s).append(System.lineSeparator());
      }
      return result.toString();
   }
}
