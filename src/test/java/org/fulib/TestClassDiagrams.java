package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.fulib.yaml.YamlIdMap;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestClassDiagrams
{
   @Test
   public void testClassDiagrams() throws IOException
   {
       // load model
      String packagName = ClassModel.class.getPackage().getName();
      YamlIdMap idMap = new YamlIdMap(packagName);

      String srcFolder = "../fulib/src/main/java/";
      Path packageFolder = Paths.get(srcFolder + packagName.replaceAll("\\.", "/"));
      Path yamlPath = packageFolder.resolve("classModel.yaml");
      if (Files.exists(yamlPath))
      {
         byte[] bytes = Files.readAllBytes(yamlPath);
         String yamlString = new String(bytes);
         ClassModel model = (ClassModel) idMap.decode(yamlString);

         model.setMainJavaDir(srcFolder);

         // generate class diagram
         FulibTools.classDiagrams().dumpPng(model);

         Path diagramPath = packageFolder.resolve("doc-files/classDiagram.png");

         assertThat(Files.exists(diagramPath), is(true));
      }
   }

   @Test
   public void test4Readme()
   {
      // start_code_fragment: test4Readme.classmodel
      ClassModelBuilder mb = Fulib.classModelBuilder("de.uniks.studyright");
      ClassBuilder uni = mb.buildClass("University")
            .buildAttribute("name", mb.STRING);
      ClassBuilder student = mb.buildClass("Student")
            .buildAttribute("name", mb.STRING)
            .buildAttribute("studentId", mb.STRING)
            .buildAttribute("matNo", mb.INT);
      uni.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);
      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("roomNo", mb.STRING);
      uni.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE)
            .setAggregation();
      room.buildAssociation(student, "students", mb.MANY, "in", mb.ONE);

      ClassModel model = mb.getClassModel();
      // end_code_fragment:

      // for usage in java doc
      FulibTools.classDiagrams().dumpPng(model);
      // is equivalent to
      FulibTools.classDiagrams().dumpPng(model, "src/main/java/org/fulib/studyright/doc-files/classDiagram.png");
      // for usage in e.g. readme.md
      FulibTools.classDiagrams().dumpPng(model, "doc/images/StudyRightClassDiagram.png");

      FulibTools.classDiagrams().dumpPng(model, "../fulib/doc/images/SimpleClassDiagram.png");

   }
}
