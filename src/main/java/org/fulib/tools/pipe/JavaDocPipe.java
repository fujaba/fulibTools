package org.fulib.tools.pipe;

public class JavaDocPipe implements Pipe
{
   public static final String NAME = "javadoc";

   @Override
   public String apply(String content, String arg)
   {
      return "<pre>{@code\n" + content + "}</pre>\n";
   }
}
