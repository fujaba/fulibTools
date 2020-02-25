package org.fulib.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
   private static final Pattern START_PATTERN = Pattern.compile("^(\\s*)// start_code_fragment: ([\\w.]+)\\s*$");
   private static final Pattern END_PATTERN = Pattern.compile("^\\s*// end_code_fragment:.*$");

   private static final Pattern INSERT_START_PATTERN = Pattern.compile(
      "^([\\s*]*)<!-- insert_code_fragment: ([\\w.]+)\\s*-->\\s*$");
   private static final Pattern INSERT_END_PATTERN = Pattern.compile("^[\\s*]*<!-- end_code_fragment:.*$");

   private static final String INSERT_INDENT = "    ";

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

      try (final BufferedReader reader = Files.newBufferedReader(file))
      {
         String key = null;
         String indent = null;
         final StringBuilder contentBuf = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null)
         {
            if (key == null) // outside fragment
            {
               final Matcher startMatcher = START_PATTERN.matcher(line);
               if (startMatcher.find())
               {
                  indent = startMatcher.group(1);
                  key = startMatcher.group(2);
               }
               // ordinary text, ignore
               continue;
            }

            if (END_PATTERN.matcher(line).find())
            {
               if (this.fragmentMap.containsKey(key))
               {
                  System.out.printf("%s: warning: fragment '%s' was already defined, using content from this file%n",
                                    fileName, key);
               }
               final String fragmentText = contentBuf.toString();
               this.fragmentMap.put(key, fragmentText);
               key = null;
               contentBuf.setLength(0);
            }
            else
            {
               final int start = commonPrefixLength(line, indent);
               contentBuf.append(line, start, line.length()).append(System.lineSeparator());
            }
         }

         if (key != null)
         {
            throw new IllegalArgumentException("could not find <!-- end_code_fragment: in " + fileName);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file read problem", e);
      }
   }

   private static int commonPrefixLength(String a, String b)
   {
      int i = 0;
      while (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i))
      {
         i++;
      }
      return i;
   }

   private void insertFragments(Path file)
   {
      String fileName = file.toString();
      if (!fileName.endsWith(".java") && !fileName.endsWith(".md"))
      {
         return;
      }

      boolean hadInserts = false;
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      final MessageDigest inputDigest = createDigest();
      try (final BufferedReader reader = newDigestReader(file, inputDigest);
           final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream)))
      {
         String key = null;

         String line;
         while ((line = reader.readLine()) != null)
         {
            if (key != null) // inside fragment
            {
               if (INSERT_END_PATTERN.matcher(line).find())
               {
                  // copy the <!-- end ... --> line
                  writer.write(line);
                  writer.write(System.lineSeparator());
                  key = null;
               }

               // don't copy anything before the <!-- end ... -->
               continue;
            }

            final Matcher startMatcher = INSERT_START_PATTERN.matcher(line);
            final String content;
            final String indent;
            if (startMatcher.find())
            {
               indent = startMatcher.group(1);
               key = startMatcher.group(2);
               content = this.fragmentMap.get(key);
               if (content == null)
               {
                  System.err.printf("%s: warning: undefined fragment '%s' was not inserted%n", fileName, key);
                  key = null;
               }
            }
            else
            {
               content = null;
               indent = null;
            }

            // in any case, copy the current line (even the <!-- insert ... -->)
            writer.write(line);
            writer.write(System.lineSeparator());

            // copy fragment content if the line was indeed <!-- insert ... -->
            if (content != null)
            {
               hadInserts = true;
               for (String contentLine : content.split(System.lineSeparator()))
               {
                  writer.write(indent);
                  writer.write(INSERT_INDENT);
                  writer.write(contentLine);
                  writer.write(System.lineSeparator());
               }
            }
         }

         if (key != null)
         {
            throw new IllegalArgumentException("could not find <!-- end_code_fragment: in " + fileName);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file read problem", e);
      }

      if (!hadInserts)
      {
         // no inserts at all; skip writing file
         return;
      }

      final byte[] bytes = outputStream.toByteArray();
      final MessageDigest outputDigest = createDigest();
      outputDigest.update(bytes);

      if (Arrays.equals(inputDigest.digest(), outputDigest.digest()))
      {
         // input and output content equal; skip writing file
         return;
      }

      try
      {
         Files.write(file, bytes);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file write problem", e);
      }
   }

   private static BufferedReader newDigestReader(Path file, MessageDigest inputDigest) throws IOException
   {
      return new BufferedReader(
         new InputStreamReader(new DigestInputStream(Files.newInputStream(file), inputDigest), StandardCharsets.UTF_8));
   }

   private static MessageDigest createDigest()
   {
      final MessageDigest inputDigest;
      try
      {
         inputDigest = MessageDigest.getInstance("SHA-1");
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
      return inputDigest;
   }
}
