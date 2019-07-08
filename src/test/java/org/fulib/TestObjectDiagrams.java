package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;
import org.junit.Test;
import studyRight.Student;
import studyRight.StudyRight;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.is;
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

      YamlObject alice = (YamlObject) root;

      FulibTools.objectDiagrams().dumpPng(root);

      FulibTools.objectDiagrams().dumpYaml("tmp/tmpStudis.yaml", root);

      System.out.println(alice);
   }

   @Test
   public void prepareNamingConventions()
   {
      ClassModelBuilder mb = Fulib.classModelBuilder("studyRight", "src/test/java");

      ClassBuilder studyRight = mb.buildClass("StudyRight");
      studyRight.buildAttribute("id", ClassModelBuilder.STRING);

      ClassBuilder student = mb.buildClass("Student");
      student.buildAttribute("name", ClassModelBuilder.STRING);

      studyRight.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);

      Fulib.generator().generate(mb.getClassModel());

   }


   @Test
   public void testNamingConventions()
   {
      StudyRight studyRight = new StudyRight().setId("studyRight");
      new Student().setName("Alice").setUni(studyRight);
      new Student().setName("Bob").setUni(studyRight);

      FulibTools.objectDiagrams().dumpSVG("tmp/studyRight.svg", studyRight);
      FulibTools.objectDiagrams().dumpYaml("tmp/studyRight.yaml", studyRight);
   }
}
