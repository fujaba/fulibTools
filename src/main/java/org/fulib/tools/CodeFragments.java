package org.fulib.tools;

import org.fulib.FulibTools;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;


/**
 * Example use:
 * <pre>
 * <!-- insert_code_fragment: CodeFragments.updateCodeFragments -->
               FulibTools.createCodeFragments().updateCodeFragments(".");
 * <!-- end_code_fragment: -->
 * </pre>
 */
public class CodeFragments
{

   private LinkedHashMap<String, String> fragmentMap = new LinkedHashMap<>();
   private String phase = "read";

   public Map<String, String> updateCodeFragments(String... folderList)
   {
      // collect code fragments from source files
      try
      {
         FileVisitor<Path> visitor = new FileVisitor<Path>()
         {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
               updateFile(file, attrs);
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
               return FileVisitResult.CONTINUE;
            }
         };

         // fetch
         for (String folder : folderList)
         {
            Path path = Paths.get(folder);
            if ( ! Files.exists(path)) continue;
            Files.walkFileTree(path, visitor);
         }

         // insert
         phase = "insert";
         for (String folder : folderList)
         {
            Path path = Paths.get(folder);
            if ( ! Files.exists(path)) continue;
            Files.walkFileTree(path, visitor);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file walk problem", e);
      }

      // inject code fragements

      return fragmentMap;
   }

   private void updateFile(Path file, BasicFileAttributes attrs)
   {
      if (phase.equals("read"))
      {
         fetchFromFile(file, attrs);
      }
      else
      {
         insertFragments(file, attrs);
      }
   }


   private void fetchFromFile(Path file, BasicFileAttributes attrs)
   {
      String fileName = file.toString();

      if ( ! (fileName.endsWith(".java")
            || fileName.endsWith(".md")
            || fileName.endsWith("build.gradle")))
         return;

      try
      {
         byte[] bytes = Files.readAllBytes(file);

         String content = new String(bytes);

         Pattern startPattern = Pattern.compile("" +
               "// start_code_fragment: ([\\w\\.]+)\\s*\n");
         Matcher startMatcher = startPattern.matcher(content);

         Pattern endPattern = Pattern.compile("" +
               "\r?\n\\s*// end_code_fragment:");
         Matcher endMatcher = endPattern.matcher(content);

         while (startMatcher.find())
         {
            int start = startMatcher.start();
            int end = startMatcher.end();
            String key = startMatcher.group(1);

            boolean found = endMatcher.find(end - 2);

            if ( ! found) throw new IllegalArgumentException("could not find <!-- end_code_fragment: in " + fileName);

            int endOfFragment = endMatcher.start();

            String fragmentText = content.substring(end, endOfFragment);

            fragmentMap.put(key, fragmentText);
         }
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file read problem", e);
      }
   }


   private void insertFragments(Path file, BasicFileAttributes attrs)
   {
      String fileName = file.toString();

      if (fileName.endsWith(".md"))
         System.out.println();

      if ( ! (fileName.endsWith(".java") || fileName.endsWith(".md"))) return;

      try
      {
         byte[] bytes = Files.readAllBytes(file);

         String content = new String(bytes);
         StringBuilder newContent = new StringBuilder();

         Pattern startPattern = Pattern.compile("" +
               "<!-- insert_code_fragment: ([\\w\\.]+)\\s*-->\\s*\n");
         Matcher startMatcher = startPattern.matcher(content);

         Pattern endPattern = Pattern.compile("" +
               "\r?\n[\\s|\\*]*<!-- end_code_fragment:");
         Matcher endMatcher = endPattern.matcher(content);

         int lastEnd = 0;

         while (startMatcher.find())
         {
            int start = startMatcher.start();
            int end = startMatcher.end();
            String key = startMatcher.group(1);

            boolean found = endMatcher.find(end-2);

            if ( ! found) throw new IllegalArgumentException("could not find <!-- end_code_fragment: in " + fileName);

            int endOfFragment = endMatcher.start();

            String fragmentText = fragmentMap.get(key);

            if (fragmentText != null)
            {
               newContent.append(content.substring(lastEnd, end))
                     .append(fragmentText);
               lastEnd = endOfFragment;
            }
         }

         if (lastEnd == 0) return;

         newContent.append(content.substring(lastEnd));

         Files.write(file, newContent.toString().getBytes(), TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.WARNING, "file read problem", e);
      }
   }

}
