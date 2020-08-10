package org.fulib;

import org.fulib.yaml.YamlIdMap;
import org.junit.Test;
import studyRight.Student;
import studyRight.StudyRight;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class TestObjectDiagrams
{
   @Test
   public void testObjectDiagrams() throws IOException, URISyntaxException
   {
      URL url = ClassLoader.getSystemResource("initStudentsSubscribeToModeling.yaml");
      Path initData = Paths.get(url.toURI());
      byte[] bytes = Files.readAllBytes(initData);
      String yamlString = new String(bytes);

      YamlIdMap idMap = new YamlIdMap();

      Object root = idMap.decode(yamlString);

      FulibTools.objectDiagrams().dumpPng(root);

      FulibTools.objectDiagrams().dumpYaml("tmp/tmpStudis.yaml", root);
   }

   @Test
   public void testNamingConventions()
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");
      new Student().setName("Alice").setUni(studyRight);
      new Student().setName("Bob").setUni(studyRight);
      Student carli = new Student();

      FulibTools.objectDiagrams().dumpSVG("tmp/studyRight.svg", studyRight, carli);
      FulibTools.objectDiagrams().dumpYaml("tmp/studyRight.yaml", studyRight);
   }

   @Test
   public void testSpecialCharsInStrings() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("studyRight").setDescription("<i>Greatest Ever</i>");

      FulibTools.objectDiagrams().dumpSVG("tmp/specialChars.svg", studyRight);
      FulibTools.objectDiagrams().dumpPng("tmp/specialChars.png", studyRight);

      byte[] bytes = Files.readAllBytes(Paths.get("tmp/specialChars.svg"));
      String svgText = new String(bytes);

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
