package org.fulib;

import org.apache.commons.io.IOUtils;
import org.fulib.tools.CodeFragments;
import org.fulib.tools.pipe.CodeFencePipe;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestCodeFragments
{
   private static final String FOLDER = "code-fragments";

   private static String[] FILES = { "CodeFragmentExample.java", "CodeFragmentExample.md" };

   @Before
   public void setup() throws IOException
   {
      Files.createDirectories(Paths.get(FOLDER));

      for (String file : FILES)
      {
         try (final InputStream input = this.getClass().getResourceAsStream(file))
         {
            Files.copy(input, Paths.get(FOLDER, file), StandardCopyOption.REPLACE_EXISTING);
         }
      }
   }

   /**
    * Example use:
    * <pre>
    * <!-- insert_code_fragment: CodeFragments.updateCodeFragments -->
    * FulibTools.codeFragments().updateCodeFragments(".");
    * <!-- end_code_fragment: -->
    * </pre>
    */
   @Test
   public void testCodeFragments() throws IOException
   {
      final CodeFragments fragments = FulibTools.codeFragments();
      fragments.addPipe("fencedCustom", new CodeFencePipe("custom"));
      fragments.update(FOLDER);
      final Map<String, String> fragmentMap = fragments.getFragments();

      String codeFragmentExampleJavaHello = fragmentMap.get("CodeFragmentExample.java.hello");
      assertThat(codeFragmentExampleJavaHello, is("System.out.println(\"Hello World\");" + System.lineSeparator()));

      final String actualMd = new String(Files.readAllBytes(Paths.get(FOLDER, "CodeFragmentExample.md")),
                                         StandardCharsets.UTF_8);

      final String expectedMd = IOUtils.toString(this.getClass().getResource("CodeFragmentExample.md.txt"),
                                                 StandardCharsets.UTF_8);
      assertThat(actualMd, is(expectedMd));

      final String actualJava = new String(Files.readAllBytes(Paths.get(FOLDER, "CodeFragmentExample.java")),
                                           StandardCharsets.UTF_8);

      final String expectedJava = IOUtils.toString(this.getClass().getResource("CodeFragmentExample.java.txt"),
                                                   StandardCharsets.UTF_8);

      assertThat(actualJava, is(expectedJava));
   }
}
