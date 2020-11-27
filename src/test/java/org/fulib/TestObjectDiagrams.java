package org.fulib;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fulib.builder.ClassModelManager;
import org.fulib.classmodel.Clazz;
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

import static org.fulib.builder.Type.MANY;
import static org.fulib.builder.Type.STRING;
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

      Person student = new Student().setName("Alice");
      final String fileName3 = diagrams.dumpPng(student);
      assertThat(fileName3, equalTo("tmp/Student.3.png"));
      assertThat(new File("tmp/Student.3.png").exists(), equalTo(true));
   }

   @Test
   public void dumpSVG() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");
      new Student().setUni(studyRight).setName("Alice");
      new Student().setUni(studyRight).setName("Bob");
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
   public void dumpWithDotKeywords() throws IOException
   {
      final Node node = new Node().setId("strict");

      FulibTools.objectDiagrams().dumpSVG("tmp/dotKeywords.svg", node);
      FulibTools.objectDiagrams().dumpPng("tmp/dotKeywords.png", node);

      assertThat(new File("tmp/dotKeywords.png").exists(), equalTo(true));
      assertThat(new File("tmp/dotKeywords.svg").exists(), equalTo(true));
   }

   @Test
   public void dumpLambdaExpr() throws IOException
   {
      Person student = new Student().setPredicate(x -> true).setName("Alice");

      FulibTools.objectDiagrams().dumpSVG("tmp/lambdaExpr.svg", student);

      final String svgText = FileUtils.readFileToString(new File("tmp/lambdaExpr.svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("&lt;lambda expression&gt;"));
      assertThat(svgText, not(containsString("$$Lambda$")));
   }

   @Test
   public void dumpCollection() throws IOException
   {
      Person student = new Student().withNotes("foo", "bar", "baz").withLuckyNumbers(2, 4, 6);

      FulibTools.objectDiagrams().dumpSVG("tmp/valueCollection.svg", student);

      final String svgText = FileUtils.readFileToString(new File("tmp/valueCollection.svg"), StandardCharsets.UTF_8);

      assertThat(svgText, containsString("notes = [foo, bar, baz]"));
      assertThat(svgText, containsString("luckyNumbers = [2, 4, 6]"));
   }

   @Test
   public void dumpYaml() throws IOException
   {
      StudyRight studyRight = new StudyRight().setId("StudyRight");

      FulibTools.objectDiagrams().dumpYaml("tmp/studyRight.yaml", studyRight);

      assertThat(new File("tmp/studyRight.yaml").exists(), equalTo(true));

      final String yaml = FileUtils.readFileToString(new File("tmp/studyRight.yaml"), StandardCharsets.UTF_8);

      // language=YAML
      assertThat(yaml, equalTo("- studyRight: \tStudyRight\n" + "  id: \tStudyRight\n\n"));

      final YamlIdMap idMap = new YamlIdMap(StudyRight.class.getPackage().getName());
      final Object decoded = idMap.decode(yaml);

      assertThat(decoded, instanceOf(StudyRight.class));
      assertThat(((StudyRight) decoded).getId(), equalTo("StudyRight"));
   }

   @Test
   public void dumpYamlCreatesParentDir()
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");

      new File("dumpYaml/studyRight.yaml").delete();
      new File("dumpYaml").delete();

      FulibTools.objectDiagrams().dumpYaml("dumpYaml/studyRight.yaml", studyRight);

      assertThat(new File("dumpYaml/studyRight.yaml").exists(), equalTo(true));
   }

   @Test
   public void inheritance() throws IOException
   {
      ClassModelManager mm = new ClassModelManager();
      mm.setPackageName("studyRight.inherited").setMainJavaDir("");
      Clazz student = mm.haveClass("Student");
      Clazz prof = mm.haveClass("Prof");
      Clazz human = mm.haveClass("Human");
      mm.haveAttribute(human, "id", STRING);

      mm.associate(prof, "teaches", MANY, student, "profs", MANY);

      student.setSuperClass(human);
      prof.setSuperClass(human);

      FulibTools.classDiagrams().dumpSVG(mm.getClassModel(), "./tmp/StudIsHuman.svg");

      // do we have an isA edge?
      final String svgText = FileUtils.readFileToString(new File("tmp/StudIsHuman.svg"), StandardCharsets.UTF_8);
      assertThat(svgText, containsString("<!-- Student&#45;&gt;Human -->"));
   }
}
