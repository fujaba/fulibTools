package org.fulib.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a data structure for mapping keys to code fragments,
 * which can be loaded from and inserted into files via special comments.
 * <p>
 * Fragments can be defined in {@code .java}, {@code .md} and {@code build.gradle} files,
 * by surrounding them with these comments:
 * <pre><code>
 *    // start_code_fragment: my.fragment.id
 *    hello world
 *    // end_code_fragment:
 * </code></pre>
 * Here, {@code my.fragment.id} is the fragment key. It can be any Java identifier including dots.
 * When reading the fragment, each line is stripped of indentation to the level the
 * {@code start_code_fragment} line was on.
 * This means the above fragment's content will be {@code hello world\n}, with {@code \n} being a newline symbol.
 * <p>
 * You can also define fragments manually using {@link #addFragment(String, String)}.
 * <p>
 * Fragment insertion happens in {@code .java} and {@code .md} files, using special HTML comments:
 * <pre><code>
 *    &lt;!-- insert_code_fragment: my.fragment.id -->
 *    &lt;!-- end_code_fragment: -->
 * </code></pre>
 * The inserted code fragment will use the same indentation as the insertion tag line, plus 4 extra spaces.
 * The characters {@code *} (asterisk) and {@code >} (greater than) also count as indentation.
 * This allows you to insert code fragments into JavaDoc or Markdown blockquotes:
 * <pre><code>
 *    /*
 *     * &lt;!-- insert_code_fragment: my.fragment.id -->
 *     * &lt;!-- end_code_fragment: -->
 *     * /
 *
 *    > &lt;!-- insert_code_fragment: my.fragment.id -->
 *    > &lt;!-- end_code_fragment: -->
 * </code></pre>
 * After processing, the above examples will look like this:
 * <pre><code>
 *    &lt;!-- insert_code_fragment: my.fragment.id -->
 *    hello world
 *    &lt;!-- end_code_fragment: -->
 *
 *    /*
 *     * &lt;!-- insert_code_fragment: my.fragment.id -->
 *     *     hello world
 *     * &lt;!-- end_code_fragment: -->
 *     * /
 *
 *    > &lt;!-- insert_code_fragment: my.fragment.id -->
 *    >     hello world
 *    > &lt;!-- end_code_fragment: -->
 * </code></pre>
 * <p>
 * To update all code fragments in the current project, put this line into a main() program and run it:
 * <pre><code>
 * <!-- insert_code_fragment: CodeFragments.updateCodeFragments -->
 * FulibTools.codeFragments().updateCodeFragments(".");
 * <!-- end_code_fragment: -->
 * </code></pre>
 */
public class CodeFragments
{
   // =============== Constants ===============

   private static final Pattern START_PATTERN = Pattern.compile("^(\\s*)// start_code_fragment: ([\\w.]+)\\s*$");
   private static final Pattern END_PATTERN = Pattern.compile("^\\s*// end_code_fragment:.*$");

   private static final Pattern INSERT_START_PATTERN = Pattern.compile(
      "^([\\s*>]*)<!-- insert_code_fragment: ([\\w.]+)\\s*(?:\\|\\s*(\\w+)\\s*)?-->\\s*$");
   private static final Pattern INSERT_END_PATTERN = Pattern.compile("^[\\s*>]*<!-- end_code_fragment:.*$");

   private static final Map<String, Pipe> DEFAULT_PIPES;

   static
   {
      final Map<String, Pipe> defaultPipes = new HashMap<>();
      defaultPipes.put(IndentPipe.NAME, new IndentPipe());
      defaultPipes.put(JavaDocPipe.NAME, new JavaDocPipe());
      defaultPipes.put(CodeFencePipe.NAME, new CodeFencePipe());
      defaultPipes.put(HtmlPipe.NAME, new HtmlPipe());
      DEFAULT_PIPES = Collections.unmodifiableMap(defaultPipes);
   }

   // =============== Fields ===============

   private LinkedHashMap<String, String> fragmentMap = new LinkedHashMap<>();
   private Map<String, Pipe> pipes = new HashMap<>(DEFAULT_PIPES);

   // =============== Properties ===============

   /**
    * @return the internal, modifiable fragment map
    *
    * @deprecated since 1.2; use {@link #getFragments()} instead
    */
   @Deprecated
   public LinkedHashMap<String, String> getFragmentMap()
   {
      return this.fragmentMap;
   }

   /**
    * @return an unmodifiable map from keys to fragments
    *
    * @since 1.2
    */
   public Map<String, String> getFragments()
   {
      return Collections.unmodifiableMap(this.fragmentMap);
   }

   /**
    * @param key
    *    the fragment key
    *
    * @return the fragment with the given {@code key}, or {@code null} if not found
    *
    * @since 1.2
    */
   public String getFragment(String key)
   {
      return this.fragmentMap.get(key);
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
   public void addFragment(String key, String content)
   {
      this.fragmentMap.put(key, content);
   }

   public Pipe getPipe(String name)
   {
      return this.pipes.get(name);
   }

   public void addPipe(String name, Pipe pipe)
   {
      this.pipes.put(name, pipe);
   }

   public void removePipe(String name)
   {
      this.pipes.remove(name);
   }

   // =============== Methods ===============

   /**
    * Loads code fragments from all files within the given folders.
    * If a folder path does not exist, it is ignored.
    *
    * @param folders
    *    the folders to search for fragments
    *
    * @since 1.2
    */
   public void load(String... folders)
   {
      try
      {
         for (String folder : folders)
         {
            this.walkFiles(Paths.get(folder), this::fetchFromFile);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file walk problem", e);
      }
   }

   /**
    * Inserts code fragments into all files within the given folders.
    * If a folder path does not exist, it is ignored.
    *
    * @param folders
    *    the folders to search for fragment insertion points
    *
    * @since 1.2
    */
   public void write(String... folders)
   {
      try
      {
         for (String folder : folders)
         {
            this.walkFiles(Paths.get(folder), this::insertFragments);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file walk problem", e);
      }
   }

   /**
    * Runs {@link #load(String...)} and {@link #write(String...)} in succession.
    *
    * @param folders
    *    the folders to search for fragments and fragment insertion points
    *
    * @since 1.2
    */
   public void update(String... folders)
   {
      this.load(folders);
      this.write(folders);
   }

   /**
    * Runs {@link #update(String...)} and then returns the {@linkplain #getFragments() map of fragments}.
    *
    * @param folders
    *    the folders to search for fragments and fragment insertion points
    *
    * @return the {@linkplain #getFragments() map of fragments}
    *
    * @deprecated since 1.2; use {@link #update(String...)} and {@link #getFragments()} instead
    */
   @Deprecated
   public Map<String, String> updateCodeFragments(String... folders)
   {
      this.update(folders);
      return this.getFragments();
   }

   // --------------- Helpers ---------------

   private void walkFiles(Path path, Consumer<? super Path> consumer) throws IOException
   {
      if (!Files.exists(path))
      {
         return;
      }
      Files.walkFileTree(path, new SimpleFileVisitor<Path>()
      {
         @Override
         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
         {
            consumer.accept(file);
            return FileVisitResult.CONTINUE;
         }
      });
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

      final boolean hadInserts;
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      final MessageDigest inputDigest = createDigest();
      try (final BufferedReader reader = newDigestReader(file, inputDigest);
           final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream)))
      {
         hadInserts = this.insertFragments(fileName, reader, writer);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file read problem", e);
         return;
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

   private boolean insertFragments(String fileName, BufferedReader reader, BufferedWriter writer) throws IOException
   {
      boolean hadInserts = false;
      String key = null;

      String line;
      while ((line = reader.readLine()) != null)
      {
         if (key != null) // inside fragment
         {
            if (INSERT_END_PATTERN.matcher(line).find())
            {
               // copy the <!-- end ... --> line
               writeLine(writer, line);
               key = null;
            }

            // don't copy anything before the <!-- end ... -->
            continue;
         }

         // outside fragments, copy the current line in any case (even the <!-- insert ... -->)
         writeLine(writer, line);

         final Matcher startMatcher = INSERT_START_PATTERN.matcher(line);
         if (!startMatcher.find())
         {
            continue;
         }

         key = startMatcher.group(2);
         String content = this.fragmentMap.get(key);
         if (content == null)
         {
            System.err.printf("%s: warning: undefined fragment '%s' was not inserted%n", fileName, key);
            key = null;
            continue;
         }

         // currently, this only matches one pipe because the regex uses ? instead of *.
         // the reason is that regex can only match a fixed number of groups, see
         // https://stackoverflow.com/questions/5018487/regular-expression-with-variable-number-of-groups
         for (int i = 3; i <= startMatcher.groupCount(); i++)
         {
            final String arg = startMatcher.group(i);
            if (arg == null)
            {
               continue;
            }

            final String pipeName = getPipeName(arg);
            final Pipe pipe = this.getPipe(arg);
            if (pipe != null)
            {
               content = pipe.apply(content, arg);
            }
            else
            {
               System.err.printf("%s: warning: unknown pipe '%s', skipping%n", fileName, pipeName);
            }
         }

         // insert the fragment right away
         hadInserts = true;
         final String indent = startMatcher.group(1);
         this.writeFragment(writer, indent, content);
      }

      if (key != null)
      {
         throw new IllegalArgumentException("could not find <!-- end_code_fragment: in " + fileName);
      }

      return hadInserts;
   }

   private static String getPipeName(String arg)
   {
      final int colonIndex = arg.indexOf(':');
      if (colonIndex >= 0)
      {
         arg = arg.substring(0, colonIndex);
      }
      return arg;
   }

   private static void writeLine(BufferedWriter writer, String line) throws IOException
   {
      writer.write(line);
      writer.write(System.lineSeparator());
   }

   private void writeFragment(BufferedWriter writer, String indent, String content) throws IOException
   {
      for (String contentLine : content.split(System.lineSeparator()))
      {
         writer.write(indent);
         writeLine(writer, contentLine);
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
