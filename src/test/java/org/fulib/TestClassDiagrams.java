package org.fulib;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.YamlIdMap;
import org.junit.Before;
import org.junit.Test;
import studyRight.StudyRight;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestClassDiagrams
{
   private ClassModel model;

   @Before
   public void setup() throws IOException
   {
      try (final InputStream yamlInput = StudyRight.class.getResourceAsStream("classModel.yaml"))
      {
         final String yamlString = IOUtils.toString(yamlInput, StandardCharsets.UTF_8);
         final YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
         this.model = (ClassModel) idMap.decode(yamlString);
         this.model.setMainJavaDir("src/test/java");
      }
   }

   @Test
   public void dumpPng()
   {
      FulibTools.classDiagrams().dumpPng(model);

      final File classDiagramPng = new File("src/test/java/studyRight/doc-files/classDiagram.png");
      assertThat(classDiagramPng.exists(), equalTo(true));
   }

   @Test
   public void dumpPngWithFileName()
   {
      final String fileName = "tmp/classDiagrams/classModel.png";
      FulibTools.classDiagrams().dumpPng(model, fileName);

      assertThat(new File(fileName).exists(), equalTo(true));
   }

   @Test
   public void dumpSVG() throws IOException
   {
      final String fileName = "tmp/classDiagrams/classModel.svg";
      FulibTools.classDiagrams().dumpSVG(model, fileName);

      final String svgText = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);
      assertThat(svgText, containsString("StudyRight"));
      assertThat(svgText, containsString("id :String"));
      assertThat(svgText, containsString("description :String"));
      assertThat(svgText, containsString("uni"));
      assertThat(svgText, containsString("students *"));
      assertThat(svgText, containsString("Person"));
      assertThat(svgText, containsString("name :String"));
      assertThat(svgText, containsString("luckyNumbers :Set&lt;Integer&gt;"));
      assertThat(svgText, containsString("Student"));
      assertThat(svgText, containsString("<!-- Student&#45;&gt;Person -->"));
      assertThat(svgText, containsString("predicate :Predicate&lt;?&gt;"));
      assertThat(svgText, containsString("notes :List&lt;String&gt;"));
      assertThat(svgText, containsString("Node"));
      assertThat(svgText, containsString("Node&#45;&gt;Node"));
   }
}
