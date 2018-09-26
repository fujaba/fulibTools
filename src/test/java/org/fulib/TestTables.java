package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestTables
{
   @Test
   public void testClassDiagrams() throws IOException
   {
      // simple class model
      ClassModelBuilder mb = Fulib.classModelBuilder("org.group");

      // generate

      // compile

      // dump html

   }


}
