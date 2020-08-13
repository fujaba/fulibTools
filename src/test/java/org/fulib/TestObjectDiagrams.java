package org.fulib;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fulib.yaml.YamlIdMap;
import org.junit.Test;
import studyRight.Student;
import studyRight.StudyRight;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestObjectDiagrams
{
   @Test
   public void testObjectDiagrams() throws IOException
   {
      try (final InputStream input = getClass().getResourceAsStream("initStudentsSubscribeToModeling.yaml"))
      {
         final String yamlString = IOUtils.toString(input, StandardCharsets.UTF_8);
         final YamlIdMap idMap = new YamlIdMap();
         final Object root = idMap.decode(yamlString);

         FulibTools.objectDiagrams().dumpPng(root);
         FulibTools.objectDiagrams().dumpYaml("tmp/tmpStudis.yaml", root);
      }
   }

   @Test
   public void testNamingConventions() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");
      new Student().setName("Alice").setUni(studyRight);
      new Student().setName("Bob").setUni(studyRight);
      Student carli = new Student();

      FulibTools.objectDiagrams().dumpSVG("tmp/studyRight.svg", studyRight, carli);
      FulibTools.objectDiagrams().dumpYaml("tmp/studyRight.yaml", studyRight);

      final String svgText = FileUtils.readFileToString(new File("tmp/studyRight.svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("studyRight :StudyRight"));
      assertThat(svgText, containsString("id = &quot;studyRight&quot;"));
      assertThat(svgText, containsString("description = null"));

      assertThat(svgText, containsString("alice :Student"));
      assertThat(svgText, containsString("name = &quot;Alice&quot;"));

      assertThat(svgText, containsString("bob :Student"));
      assertThat(svgText, containsString("name = &quot;Bob&quot;"));

      assertThat(svgText, containsString("s :Student"));
      assertThat(svgText, containsString("name = null"));

      assertThat(svgText, containsString("uni"));
      assertThat(svgText, containsString("students"));
   }

   @Test
   public void testSpecialCharsInStrings() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("studyRight").setDescription("<i>Greatest Ever</i>");

      FulibTools.objectDiagrams().dumpSVG("tmp/specialChars.svg", studyRight);
      FulibTools.objectDiagrams().dumpPng("tmp/specialChars.png", studyRight);

      assertThat(new File("tmp/specialChars.png").exists(), equalTo(true));

      final String svgText = FileUtils.readFileToString(new File("tmp/specialChars.svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("&lt;i&gt;Greatest Ever&lt;/i&gt;"));
   }

   @Test
   public void testListsOfObjects()
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");
      Student alice = new Student().setName("Alice").setUni(studyRight);
      Student bob = new Student().setName("Bob").setUni(studyRight);
      Student carli = new Student();
      ArrayList<Student> students = new ArrayList<>();
      students.add(alice);
      students.add(bob);
      students.add(carli);

      FulibTools.objectDiagrams().dumpSVG("tmp/students.svg", students);
      FulibTools.objectDiagrams().dumpYaml("tmp/students.yaml", students);
   }
}
