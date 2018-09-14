package org.fulib;

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
               FulibTools.createCodeFragments().updateCodeFragments(".");
         <!-- end_code_fragment: -->
    * </pre>
    */
   @Test
   public void testCodeFragments()
   {
      String folder = ".";
      if (Files.exists(Paths.get(folder)))
      {
         Map<String, String> fragmentMap =

               // start_code_fragment: CodeFragments.updateCodeFragments
               FulibTools.createCodeFragments().updateCodeFragments(".");
         // end_code_fragment:

         String codeFragments_updateCodeFragments = fragmentMap.get("CodeFragments.updateCodeFragments");
         assertThat(codeFragments_updateCodeFragments, notNullValue());
      }
   }


   @Test
   public void testFulibCodeFragments()
   {
      String folder = "../fulib";
      if (Files.exists(Paths.get(folder)))
      {
         Map<String, String> fragmentMap =
               FulibTools.createCodeFragments().updateCodeFragments(".", folder);

         assertThat(fragmentMap.size(), not(equalTo(0)));
      }
   }

}
