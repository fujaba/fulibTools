package org.fulib;

import org.apache.commons.io.IOUtils;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
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

import static org.fulib.builder.ClassModelBuilder.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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

   @Test
   public void test4Readme()
   {
      // @formatter:off
      // start_code_fragment: test4Readme.classmodel
      ClassModelBuilder mb = Fulib.classModelBuilder("de.uniks.studyright");

      ClassBuilder uni = mb.buildClass("University")
                           .buildAttribute("name", STRING);

      ClassBuilder student = mb.buildClass("Student")
                               .buildAttribute("name", STRING)
                               .buildAttribute("studentId", STRING)
                               .buildAttribute("matNo", INT);

      ClassBuilder room = mb.buildClass("Room")
                            .buildAttribute("roomNo", STRING);

      ClassBuilder professor = mb.buildClass("Professor");

      uni.buildAssociation(student, "students", MANY, "uni", ONE);
      uni.buildAssociation(room, "rooms", MANY, "uni", ONE).setAggregation();
      room.buildAssociation(student, "students", MANY, "in", ONE);
      uni.buildAssociation(professor, "profs", MANY, null, 1);

      ClassModel model = mb.getClassModel();
      // end_code_fragment:
      // @formatter:on

      // for usage in java doc
      FulibTools.classDiagrams().dumpPng(model);
      // is equivalent to
      FulibTools.classDiagrams().dumpPng(model, "src/main/java/org/fulib/studyright/doc-files/classDiagram.png");
      // for usage in e.g. readme.md
      FulibTools.classDiagrams().dumpSVG(model, "doc/images/StudyRightClassDiagram.svg");
      FulibTools.classDiagrams().dumpPng(model, "doc/images/StudyRightClassDiagram.png");

      FulibTools.classDiagrams().dumpPng(model, "../fulib/doc/images/SimpleClassDiagram.png");
   }
}
