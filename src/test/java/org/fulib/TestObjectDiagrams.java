package org.fulib;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fulib.tools.ObjectDiagrams;
import org.fulib.yaml.YamlIdMap;
import org.junit.Test;
import studyRight.Node;
import studyRight.Person;
import studyRight.Student;
import studyRight.StudyRight;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestObjectDiagrams
{
   @Test
   public void yamlObjects() throws IOException
   {
      final String fileName = "tmp/objectDiagrams/yamlObjects.svg";

      try (final InputStream input = getClass().getResourceAsStream("initStudentsSubscribeToModeling.yaml"))
      {
         final String yamlString = IOUtils.toString(input, StandardCharsets.UTF_8);
         final YamlIdMap idMap = new YamlIdMap();
         final Object root = idMap.decode(yamlString);

         FulibTools.objectDiagrams().dumpSVG(fileName, root);
      }

      final String svgText = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);

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
      assertThat(new File(fileName1).exists(), equalTo(true));

      final String fileName1Again = diagrams.dumpPng(studyRight1);
      assertThat(fileName1Again, equalTo("tmp/StudyRight.1.png"));

      final StudyRight studyRight2 = new StudyRight().setId("studyRight2");
      final String fileName2 = diagrams.dumpPng(studyRight2);
      assertThat(fileName2, equalTo("tmp/StudyRight.2.png"));
      assertThat(new File(fileName2).exists(), equalTo(true));

      Person student = new Student().setName("Alice");
      final String fileName3 = diagrams.dumpPng(student);
      assertThat(fileName3, equalTo("tmp/Student.3.png"));
      assertThat(new File(fileName3).exists(), equalTo(true));
   }

   @Test
   public void dumpSVG() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");
      new Student().setUni(studyRight).setName("Alice");
      new Student().setUni(studyRight).setName("Bob");
      Student carli = new Student();

      final String fileName = "tmp/objectDiagrams/dumpSVG.svg";
      FulibTools.objectDiagrams().dumpSVG(fileName, studyRight, carli);

      final String svgText = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);

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
      final String prefix = "tmp/objectDiagrams/dumpWithSpecialChars/studyRight";

      StudyRight studyRight = new StudyRight().setId("studyRight").setDescription("<i>Greatest Ever</i>");

      FulibTools.objectDiagrams().dumpSVG(prefix + ".svg", studyRight);
      FulibTools.objectDiagrams().dumpPng(prefix + ".png", studyRight);

      assertThat(new File(prefix + ".png").exists(), equalTo(true));

      final String svgText = FileUtils.readFileToString(new File(prefix + ".svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("&lt;i&gt;Greatest Ever&lt;/i&gt;"));
   }

   @Test
   public void dumpWithDotKeywords() throws IOException
   {
      final String prefix = "tmp/objectDiagrams/dumpWithDotKeywords/node";

      final Node node = new Node().setId("strict");

      FulibTools.objectDiagrams().dumpSVG(prefix + ".svg", node);
      FulibTools.objectDiagrams().dumpPng(prefix + ".png", node);

      assertThat(new File(prefix + ".png").exists(), equalTo(true));
      assertThat(new File(prefix + ".svg").exists(), equalTo(true));
   }

   @Test
   public void dumpLambdaExpr() throws IOException
   {
      Person student = new Student().setPredicate(x -> true).setName("Alice");

      final String fileName = "tmp/objectDiagrams/dumpLambdaExpr.svg";
      FulibTools.objectDiagrams().dumpSVG(fileName, student);

      final String svgText = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("&lt;lambda expression&gt;"));
      assertThat(svgText, not(containsString("$$Lambda$")));
   }

   @Test
   public void dumpCollection() throws IOException
   {
      Person student = new Student().withNotes("foo", "bar", "baz").withLuckyNumbers(2, 4, 6);

      final String fileName = "tmp/objectDiagrams/dumpCollection.svg";
      FulibTools.objectDiagrams().dumpSVG(fileName, student);

      final String svgText = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("notes = [foo, bar, baz]"));
      assertThat(svgText, containsString("luckyNumbers = [2, 4, 6]"));
   }

   @Test
   public void dumpYaml() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("StudyRight");

      final String fileName = "tmp/objectDiagrams/dumpYaml.yaml";
      FulibTools.objectDiagrams().dumpYaml(fileName, studyRight);

      final String yaml = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);

      // language=YAML
      assertThat(yaml, equalTo("- studyRight: \tstudyRight.StudyRight\n" + "  id: \tStudyRight\n\n"));

      final YamlIdMap idMap = new YamlIdMap(StudyRight.class.getPackage().getName());
      final Object decoded = idMap.decode(yaml);

      assertThat(decoded, instanceOf(StudyRight.class));
      assertThat(((StudyRight) decoded).getId(), equalTo("StudyRight"));
   }

   @Test
   public void dumpToString() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("StudyRight");
      Person alice = new Student().setUni(studyRight).setName("Alice");
      Person bob = new Student().setUni(studyRight).setName("Bob");
      Person carli = new Student().setUni(studyRight).setName("Carli");

      final String fileName = "tmp/objectDiagrams/dumpToString.txt";
      FulibTools.objectDiagrams().dumpToString(fileName, studyRight, alice, bob, carli);

      final String text = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);

      assertThat(text, equalTo("StudyRight null\n" +
         "Alice []\n" +
         "Bob []\n" +
         "Carli []\n"));
   }
}
