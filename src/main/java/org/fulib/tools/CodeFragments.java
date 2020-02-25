package org.fulib.tools;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Example use:
 * <pre>
 * <!-- insert_code_fragment: CodeFragments.updateCodeFragments -->
 * FulibTools.codeFragments().updateCodeFragments(".");
 * <!-- end_code_fragment: -->
 * </pre>
 */
public class CodeFragments
{
   private LinkedHashMap<String, String> fragmentMap = new LinkedHashMap<>();

   /**
    * @return the fragment map
    *
    * @deprecated since 1.2; use the result of {@link #updateCodeFragments(String...)} instead
    */
   @Deprecated
   public LinkedHashMap<String, String> getFragmentMap()
   {
      return this.fragmentMap;
   }

   /**
    * Allows specifying fragments that can be inserted but don't have to be present in source files.
    * This is useful for showing test output without having to write it to a file.
    * <p>
    * Example:
    * <p>
    * {@code Example.java}:
    * <pre><code>
    *    final CodeFragments fragments = new CodeFragments();
    *    // start_code_fragment: exampleCode
    *    final String foo = "bar" + 42 + "baz";
    *    // end_code_fragment:
    *    fragments.add("exampleOutput", foo);
    *    fragments.updateCodeFragments(".");
    * </code></pre>
    * <p>
    * {@code README.md}, before:
    * <pre><code>
    *    Use the following code:
    *    &lt;!-- insert_code_fragment: exampleCode -->
    *    &lt;!-- end_code_fragment: -->
    *    to get the output:
    *    &lt;!-- insert_code_fragment: exampleOutput -->
    *    &lt;!-- end_code_fragment: -->
    * </code></pre>
    * <p>
    * {@code README.md}, after:
    * <pre><code>
    *    Use the following code:
    *    &lt;!-- insert_code_fragment: exampleCode -->
    *    final String foo = "bar" + 42 + "baz";
    *    &lt;!-- end_code_fragment: -->
    *    to get the output:
    *    &lt;!-- insert_code_fragment: exampleOutput -->
    *    bar42baz
    *    &lt;!-- end_code_fragment: -->
    * </code></pre>
    *
    * @param key
    *    the fragment key
    * @param content
    *    the fragment content
    *
    * @since 1.2
    */
   public void add(String key, String content)
   {
      this.fragmentMap.put(key, content);
   }

   public Map<String, String> updateCodeFragments(String... folderList)
   {
      // collect code fragments from source files
      try
      {
         AtomicBoolean write = new AtomicBoolean(false);
         FileVisitor<Path> visitor = new FileVisitor<Path>()
         {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            {
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            {
               if (write.get())
               {
                  CodeFragments.this.insertFragments(file);
               }
               else
               {
                  CodeFragments.this.fetchFromFile(file);
               }
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
            {
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
            {
               return FileVisitResult.CONTINUE;
            }
         };

         // fetch
         for (String folder : folderList)
         {
            Path path = Paths.get(folder);
            if (!Files.exists(path))
            {
               continue;
            }
            Files.walkFileTree(path, visitor);
         }

         // insert
         write.set(true);
         for (String folder : folderList)
         {
            Path path = Paths.get(folder);
            if (!Files.exists(path))
            {
               continue;
            }
            Files.walkFileTree(path, visitor);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file walk problem", e);
      }

      // inject code fragements

      return this.fragmentMap;
   }

   private void fetchFromFile(Path file)
   {
      String fileName = file.toString();

      if (!fileName.endsWith(".java") && !fileName.endsWith(".md") && !fileName.endsWith("build.gradle"))
      {
         return;
      }

      try
      {
         byte[] bytes = Files.readAllBytes(file);

         String content = new String(bytes);

         Pattern startPattern = Pattern.compile("" + "// start_code_fragment: ([\\w.]+)\\s*\n");
         Matcher startMatcher = startPattern.matcher(content);

         Pattern endPattern = Pattern.compile("" + "\r?\n\\s*// end_code_fragment:");
         Matcher endMatcher = endPattern.matcher(content);

         while (startMatcher.find())
         {
            int end = startMatcher.end();
            String key = startMatcher.group(1);

            boolean found = endMatcher.find(end - 2);

            if (!found)
            {
               throw new IllegalArgumentException("could not find <!-- end_code_fragment: in " + fileName);
            }

            int endOfFragment = endMatcher.start();

            String fragmentText = content.substring(end, endOfFragment);

            this.fragmentMap.put(key, fragmentText);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file read problem", e);
      }
   }

   private void insertFragments(Path file)
   {
      String fileName = file.toString();
      if (!fileName.endsWith(".java") && !fileName.endsWith(".md"))
      {
         return;
      }

      try
      {
         byte[] bytes = Files.readAllBytes(file);

         String content = new String(bytes);
         StringBuilder newContent = new StringBuilder();

         Pattern startPattern = Pattern.compile("" + "<!-- insert_code_fragment: ([\\w.]+)\\s*-->\\s*\n");
         Matcher startMatcher = startPattern.matcher(content);

         Pattern endPattern = Pattern.compile("" + "\r?\n[\\s|*]*<!-- end_code_fragment:");
         Matcher endMatcher = endPattern.matcher(content);

         int lastEnd = 0;

         while (startMatcher.find())
         {
            int end = startMatcher.end();
            String key = startMatcher.group(1);

            boolean found = endMatcher.find(end - 2);

            if (!found)
            {
               throw new IllegalArgumentException("could not find <!-- end_code_fragment: in " + fileName);
            }

            int endOfFragment = endMatcher.start();

            String fragmentText = this.fragmentMap.get(key);

            if (fragmentText != null)
            {
               newContent.append(content, lastEnd, end).append(fragmentText);
               lastEnd = endOfFragment;
            }
         }

         if (lastEnd == 0)
         {
            return;
         }

         newContent.append(content.substring(lastEnd));

         Files.write(file, newContent.toString().getBytes(), TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file read problem", e);
      }
   }
}
