package org.fulib;

import org.apache.commons.io.IOUtils;
import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.YamlIdMap;
import org.junit.Test;
import studyRight.StudyRight;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestClassDiagrams
{
   @Test
   public void testClassDiagrams() throws IOException
   {
      // load model
      final String packageName = StudyRight.class.getPackage().getName();

      try (final InputStream yamlInput = StudyRight.class.getResourceAsStream("classModel.yaml"))
      {
         final String yamlString = IOUtils.toString(yamlInput, StandardCharsets.UTF_8);
         YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
         ClassModel model = (ClassModel) idMap.decode(yamlString);

         model.setMainJavaDir("src/test/java");

         FulibTools.classDiagrams().dumpPng(model);

         Path diagramPath = Paths.get("src/test/java/" + packageName.replace('.', '/') + "/doc-files/classDiagram.png");

         assertThat(Files.exists(diagramPath), is(true));
      }
   }
}
