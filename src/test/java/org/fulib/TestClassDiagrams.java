package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.tools.ClassDiagrams;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestClassDiagrams
{
   @Test
   public void testClassDiagrams() throws IOException
   {
       // load model
      String packagName = ClassModel.class.getPackage().getName();
      YamlIdMap idMap = new YamlIdMap(packagName);

      String srcFolder = "../fulib/src/main/java/";
      Path packageFolder = Paths.get(srcFolder + packagName.replaceAll("\\.", "/"));
      Path yamlPath = packageFolder.resolve("classModel.yaml");
      if (Files.exists(yamlPath))
      {
         byte[] bytes = Files.readAllBytes(yamlPath);
         String yamlString = new String(bytes);
         ClassModel model = (ClassModel) idMap.decode(yamlString);

         model.setMainJavaDir(srcFolder);

         // generate class diagram
         ClassDiagrams.dumpPng(model);

         Path diagramPath = packageFolder.resolve("doc-files/classDiagram.png");

         assertThat(Files.exists(diagramPath), is(true));
      }
   }
}
