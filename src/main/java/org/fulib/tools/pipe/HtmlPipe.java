package org.fulib.tools.pipe;

import org.apache.commons.text.StringEscapeUtils;

/**
 * A pipe that escapes any HTML4 special characters using HTML entities.
 * Useful in plain Javadoc contexts.
 *
 * @since 1.2
 */
public class HtmlPipe implements Pipe
{
   public static final String NAME = "html";

   @Override
   public String apply(String content, String arg)
   {
      return StringEscapeUtils.escapeHtml4(content);
   }
}
