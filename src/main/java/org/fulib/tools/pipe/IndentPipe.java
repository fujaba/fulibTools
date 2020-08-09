package org.fulib.tools.pipe;

/**
 * A pipe that indents each line of the input text with 4 spaces.
 * Useful in markdown contexts when fenced code blocks are not desired.
 *
 * @since 1.2
 */
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
