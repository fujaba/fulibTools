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
      assertThat(codeFragments_updateCodeFragments, is("   hello world" + System.lineSeparator()));

      final String actualContent = new String(Files.readAllBytes(Paths.get(FOLDER, "CodeFragmentExample.md")),
                                              StandardCharsets.UTF_8);
      final String expectedContent =
         "<!-- insert_code_fragment: CodeFragmentExample.java.hello -->\n" + "   hello world\n"
         + "<!-- end_code_fragment: -->\n";
      assertThat(actualContent, is(expectedContent));
   }
}
