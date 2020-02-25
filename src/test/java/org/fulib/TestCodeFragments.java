package org.fulib;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
            Files.copy(input, Paths.get(FOLDER, file));
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
      final Map<String, String> fragmentMap = FulibTools.codeFragments().updateCodeFragments(FOLDER);

      String codeFragments_updateCodeFragments = fragmentMap.get("CodeFragmentExample.java.hello");
      assertThat(codeFragments_updateCodeFragments,
                 is("System.out.println(\"Hello World\");" + System.lineSeparator()));

      final String actualMd = new String(Files.readAllBytes(Paths.get(FOLDER, "CodeFragmentExample.md")),
                                         StandardCharsets.UTF_8);

      // language=markdown
      final String expectedMd = "Indentation level increased by 4 spaces:\n" + "\n"
                                + "<!-- insert_code_fragment: CodeFragmentExample.java.hello -->\n"
                                + "    System.out.println(\"Hello World\");\n" + "<!-- end_code_fragment: -->\n" + "\n"
                                + "Blockquotes:\n" + "\n"
                                + "> <!-- insert_code_fragment: CodeFragmentExample.java.hello -->\n"
                                + ">     System.out.println(\"Hello World\");\n" + "> <!-- end_code_fragment: -->\n";
      assertThat(actualMd, is(expectedMd));

      final String actualJava = new String(Files.readAllBytes(Paths.get(FOLDER, "CodeFragmentExample.java")),
                                           StandardCharsets.UTF_8);

      // language=java
      final String expectedJava =
         "/**\n" + " * <pre><code>\n" + " * <!-- insert_code_fragment: CodeFragmentExample.java.hello -->\n"
         + " *     System.out.println(\"Hello World\");\n" + " * <!-- end_code_fragment: -->\n" + " * </code></pre>\n"
         + " */\n" + "class CodeFragmentExample {\n" + "   void foo() {\n"
         + "      // start_code_fragment: CodeFragmentExample.java.hello\n"
         + "      System.out.println(\"Hello World\");\n" + "      // end_code_fragment:\n" + "   }\n" + "}\n";

      assertThat(actualJava, is(expectedJava));
   }
}
