package org.fulib;

import org.fulib.tools.CodeFragments;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TestCodeFragments
{
   /**
    * Example use:
    * <pre>
    * <!-- insert_code_fragment: CodeFragments.updateCodeFragments -->
         Map<String, String> fragmentMap = CodeFragments.updateCodeFragments(".");
         
         
         
         
         <!-- end_code_fragment: -->
    * </pre>
    */
   @Test
   public void testCodeFragments()
   {
      String folder = ".";
      if (Files.exists(Paths.get(folder)))
      {
         // start_code_fragment: CodeFragments.updateCodeFragments
         Map<String, String> fragmentMap = CodeFragments.updateCodeFragments(".");
         // end_code_fragment:

         String codeFragments_updateCodeFragments = fragmentMap.get("CodeFragments.updateCodeFragments");
         assertThat(codeFragments_updateCodeFragments, notNullValue());
      }

   }
}
