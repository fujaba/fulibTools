package org.fulib;

import org.fulib.tools.*;

public class FulibTools
{

   /**
    * To create a class diagram png for usage in java doc comments,
    * dumpPng(model) creates file .../doc-files/classDiagram.png within the model source folder
    * <pre>
    * <!-- insert_code_fragment: test4Readme.classmodel-->
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
      ClassBuilder professor = mb.buildClass("Professor");
      uni.buildAssociation(professor, "profs", mb.MANY, null, 1);

      ClassModel model = mb.getClassModel();
    * <!-- end_code_fragment: -->
    * </pre>
    * @return
    */
   public static ClassDiagrams classDiagrams()
   {
      return new ClassDiagrams();
   }

   /**
    * Create object diagrams.
    * <pre>
    * <!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
    FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
    * <!-- end_code_fragment: -->
    * </pre>
    * <img src="tools/doc-files/studyRightObjects.png" alt="StudyRight Objects">
    * @return the object diagram tool
    */
   public static ObjectDiagrams objectDiagrams()
   {
      return new ObjectDiagrams();
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

   /**
    * Table tool to generate html tables or line charts from fulib tables
    * @return a table tool
    *
    * @deprecated since 1.2; use {@code HtmlRenderer} provided by FulibTables.
    */
   @Deprecated
   public static Tables tables()
   {
      return new Tables();
   }

   public static ScenarioDiagrams scenarioDiagrams()
   {
      return new ScenarioDiagrams();
   }
}
