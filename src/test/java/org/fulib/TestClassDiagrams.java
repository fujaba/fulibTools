package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
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
         FulibTools.createClassDiagrams().dumpPng(model);

         Path diagramPath = packageFolder.resolve("doc-files/classDiagram.png");

         assertThat(Files.exists(diagramPath), is(true));
      }
   }

   @Test
   public void test4Readme()
   {
      // start_code_fragment: test4Readme.classmodel
      ClassModelBuilder mb = ClassModelBuilder.get("org.fulib.studyright");
      ClassBuilder university = mb.buildClass("University").buildAttribute("name", mb.STRING);
      ClassBuilder student = mb.buildClass("Student").buildAttribute("studentId", mb.STRING);
      university.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);

      ClassModel model = mb.getClassModel();

      // for usage in java doc
      FulibTools.createClassDiagrams().dumpPng(model);
      // is equivalent to
      FulibTools.createClassDiagrams().dumpPng(model, "src/main/java/org/fulib/studyright/doc-files/classDiagram.png");
      // for usage in e.g. readme.md
      FulibTools.createClassDiagrams().dumpPng(model, "doc/images/StudyRightClassDiagram.png");
      // end_code_fragment:
   }
}
