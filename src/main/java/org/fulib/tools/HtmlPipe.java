package org.fulib.tools;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlPipe implements Pipe
{
   public static final String NAME = "html";

   @Override
   public String apply(String content, String arg)
   {
      return StringEscapeUtils.escapeHtml4(content);
   }
}
