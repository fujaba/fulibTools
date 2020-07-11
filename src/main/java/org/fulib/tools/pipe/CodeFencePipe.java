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
 *    ```java
 *    hello
 *    world
 *    ```
 * }</pre>
 * <p>
 * You can specify a language tag either with the pipe argument (e.g. {@code fenced:js}).
 * The default language tag is {@code java}, but you can override that using the {@link #CodeFencePipe(String)} constructor.
 * The empty string is allowed as a language tag.
 *
 * @since 1.2
 */
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
