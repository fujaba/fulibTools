package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.YamlIdMap;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestObjectDiagrams
{
   @Test
   public void testObjectDiagrams() throws IOException
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
         FulibTools.objectDiagrams().dumpPng(model);

         Path diagramPath = Paths.get("tmp/ClassModel.1.png");

         assertThat(Files.exists(diagramPath), is(true));
      }
   }


}
