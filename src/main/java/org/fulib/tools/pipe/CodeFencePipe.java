package org.fulib.tools.pipe;

/**
 * A pipe that puts the input text into a markdown code fence.
 * E.g.,
 *
 * <pre>{@code
 *    hello
 *    world
 * }</pre>
 * <p>
 * Is transformed to
 *
 * <pre>{@code
 *    ```
 *    hello
 *    world
 *    ```
 * }</pre>
 * <p>
 * You can specify a language tag with the pipe argument (e.g. {@code fenced:js}).
 * In this case, the output will be:
 *
 * <pre>{@code
 *    ```js
 *    hello
 *    world
 *    ```
 * }</pre>
 * <p>
 * Using the {@link #CodeFencePipe(String)} constructor, you can specify a default language tag that will be used when
 * none is specified via the argument.
 *
 * @since 1.2
 */
public class CodeFencePipe implements Pipe
{
   public static final String NAME = "fenced";

   private final String defaultLanguage;

   public CodeFencePipe()
   {
      this.defaultLanguage = null;
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
      return this.defaultLanguage != null ? this.defaultLanguage : "";
   }
}
