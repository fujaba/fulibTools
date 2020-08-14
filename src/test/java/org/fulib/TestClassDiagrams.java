package org.fulib;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.YamlIdMap;
import org.junit.Test;
import studyRight.StudyRight;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestClassDiagrams
{
   @Test
   public void testClassDiagrams() throws IOException
   {
      final String packageName = StudyRight.class.getPackage().getName();
      try (final InputStream yamlInput = StudyRight.class.getResourceAsStream("classModel.yaml"))
      {
         final String yamlString = IOUtils.toString(yamlInput, StandardCharsets.UTF_8);
         final YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
         final ClassModel model = (ClassModel) idMap.decode(yamlString);

         model.setMainJavaDir("src/test/java");

         FulibTools.classDiagrams().dumpPng(model);
         FulibTools.classDiagrams().dumpPng(model, "tmp/classModel.png");
         FulibTools.classDiagrams().dumpSVG(model, "tmp/classModel.svg");
      }

      final File classDiagramPng = new File("src/test/java/" + packageName.replace('.', '/') + "/doc-files/classDiagram.png");
      assertThat(classDiagramPng.exists(), equalTo(true));

      assertThat(new File("tmp/classModel.png").exists(), equalTo(true));

      final String svgText = FileUtils.readFileToString(new File("tmp/classModel.svg"), StandardCharsets.UTF_8);
      assertThat(svgText, containsString("StudyRight"));
      assertThat(svgText, containsString("id :String"));
      assertThat(svgText, containsString("description :String"));
      assertThat(svgText, containsString("uni"));
      assertThat(svgText, containsString("students *"));
      assertThat(svgText, containsString("Student"));
      assertThat(svgText, containsString("name :String"));
      assertThat(svgText, containsString("predicate :Predicate&lt;?&gt;"));
   }
}
