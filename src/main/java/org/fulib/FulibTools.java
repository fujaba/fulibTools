package org.fulib;

import org.fulib.tools.ClassDiagrams;
import org.fulib.tools.CodeFragments;

public class FulibTools
{

   /**
    * To create a class diagram png for usage in java doc comments,
    * dumpPng(model) creates file .../doc-files/classDiagram.png within the model source folder
    * <pre>
    * <!-- insert_code_fragment: test4Readme.classmodel-->
      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright");
      ClassBuilder university = mb.buildClass("University").buildAttribute("name", mb.STRING);
      ClassBuilder student = mb.buildClass("Student").buildAttribute("studentId", mb.STRING);
      university.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);

      ClassModel model = mb.getClassModel();

      // for usage in java doc
      FulibTools.classDiagrams().dumpPng(model);
      // is equivalent to
      FulibTools.classDiagrams().dumpPng(model, "src/main/java/org/fulib/studyright/doc-files/classDiagram.png");
      // for usage in e.g. readme.md
      FulibTools.classDiagrams().dumpPng(model, "doc/images/StudyRightClassDiagram.png");
    * <!-- end_code_fragment: -->
    * </pre>
    * @return
    */
   public static ClassDiagrams classDiagrams()
   {
      return new ClassDiagrams();
   }


   /**
    * Example use:
    * <pre>
    * <!-- insert_code_fragment: CodeFragments.updateCodeFragments -->
               FulibTools.codeFragments().updateCodeFragments(".");
    * <!-- end_code_fragment: -->
    * </pre>
    */
   public static CodeFragments codeFragments()
   {
      return new CodeFragments();
   }

}