package org.fulib;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fulib.tools.ObjectDiagrams;
import org.fulib.yaml.YamlIdMap;
import org.junit.Test;
import studyRight.Student;
import studyRight.StudyRight;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestObjectDiagrams
{
   @Test
   public void yamlObjects() throws IOException
   {
      try (final InputStream input = getClass().getResourceAsStream("initStudentsSubscribeToModeling.yaml"))
      {
         final String yamlString = IOUtils.toString(input, StandardCharsets.UTF_8);
         final YamlIdMap idMap = new YamlIdMap();
         final Object root = idMap.decode(yamlString);

         FulibTools.objectDiagrams().dumpSVG("tmp/yamlObjects.svg", root);
      }

      final String svgText = FileUtils.readFileToString(new File("tmp/yamlObjects.svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("Alice :UniStudent"));
      assertThat(svgText, containsString("studentId = &quot;m4242&quot;"));

      assertThat(svgText, containsString("enrol1 :Enrollment"));
      assertThat(svgText, containsString("result = &quot;open&quot;"));

      assertThat(svgText, containsString("CS :Program"));
      assertThat(svgText, containsString("math :Course"));
      assertThat(svgText, containsString("modeling :Course"));
      assertThat(svgText, containsString("modelingWT1819Exam :Exam"));

      assertThat(svgText, containsString("majorSubject"));
      assertThat(svgText, containsString("enrollments"));
      assertThat(svgText, containsString("exam"));
      assertThat(svgText, containsString("courses"));
      assertThat(svgText, containsString("topic"));
   }

   @Test
   public void dumpPngFileName()
   {
      final ObjectDiagrams diagrams = FulibTools.objectDiagrams();

      final StudyRight studyRight1 = new StudyRight().setId("studyRight1");
      final String fileName1 = diagrams.dumpPng(studyRight1);
      assertThat(fileName1, equalTo("tmp/StudyRight.1.png"));
      assertThat(new File("tmp/StudyRight.1.png").exists(), equalTo(true));

      final String fileName1_ = diagrams.dumpPng(studyRight1);
      assertThat(fileName1_, equalTo("tmp/StudyRight.1.png"));

      final StudyRight studyRight2 = new StudyRight().setId("studyRight2");
      final String fileName2 = diagrams.dumpPng(studyRight2);
      assertThat(fileName2, equalTo("tmp/StudyRight.2.png"));
      assertThat(new File("tmp/StudyRight.2.png").exists(), equalTo(true));

      Student student = new Student().setName("Alice");
      final String fileName3 = diagrams.dumpPng(student);
      assertThat(fileName3, equalTo("tmp/Student.3.png"));
      assertThat(new File("tmp/Student.3.png").exists(), equalTo(true));
   }

   @Test
   public void dumpSVG() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");
      new Student().setName("Alice").setUni(studyRight);
      new Student().setName("Bob").setUni(studyRight);
      Student carli = new Student();

      FulibTools.objectDiagrams().dumpSVG("tmp/studyRight.svg", studyRight, carli);

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
   public void dumpWithSpecialChars() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("studyRight").setDescription("<i>Greatest Ever</i>");

      FulibTools.objectDiagrams().dumpSVG("tmp/specialChars.svg", studyRight);
      FulibTools.objectDiagrams().dumpPng("tmp/specialChars.png", studyRight);

      assertThat(new File("tmp/specialChars.png").exists(), equalTo(true));

      final String svgText = FileUtils.readFileToString(new File("tmp/specialChars.svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("&lt;i&gt;Greatest Ever&lt;/i&gt;"));
   }

   @Test
   public void dumpLambdaExpr() throws IOException
   {
      Student student = new Student().setName("Alice").setPredicate(x -> true);

      FulibTools.objectDiagrams().dumpSVG("tmp/lambdaExpr.svg", student);

      final String svgText = FileUtils.readFileToString(new File("tmp/lambdaExpr.svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("&lt;lambda expression&gt;"));
      assertThat(svgText, not(containsString("$$Lambda$")));
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
