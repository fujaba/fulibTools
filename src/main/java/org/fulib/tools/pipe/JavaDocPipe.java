package org.fulib.tools.pipe;

import org.fulib.tools.CodeFragments;

/**
 * A pipe that wraps the input text for use in Javadoc comments.
 * E.g.,
 * <pre>{@code
 *    hello
 *    world
 * }</pre>
 * <p>
 * becomes
 *
 * <pre>{@code
 *    <pre>{@code
 *    hello
 *    world
 *    }</pre>
 * }</pre>
 * <p>
 * Note that leading asterisks ({@code *}) are added by {@link CodeFragments}.
 *
 * @since 1.2
 */
public class JavaDocPipe implements Pipe
{
   public static final String NAME = "javadoc";

   @Override
   public String apply(String content, String arg)
   {
      return "<pre>{@code" + System.lineSeparator() + content + "}</pre>" + System.lineSeparator();
   }
}
