package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.apache.commons.text.StringEscapeUtils;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * To create a class diagram png for usage in java doc comments,
 * dumpPng(model) creates file .../doc-files/classDiagram.png within the model source folder
 * <pre>
 * <!-- insert_code_fragment: test4Readme.classmodel-->
 * ClassModelBuilder mb = Fulib.classModelBuilder("de.uniks.studyright");
 * ClassBuilder uni = mb.buildClass("University")
 * .buildAttribute("name", mb.STRING);
 * ClassBuilder student = mb.buildClass("Student")
 * .buildAttribute("name", mb.STRING)
 * .buildAttribute("studentId", mb.STRING)
 * .buildAttribute("matNo", mb.INT);
 * uni.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);
 * ClassBuilder room = mb.buildClass("Room")
 * .buildAttribute("roomNo", mb.STRING);
 * uni.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE)
 * .setAggregation();
 * room.buildAssociation(student, "students", mb.MANY, "in", mb.ONE);
 * ClassBuilder professor = mb.buildClass("Professor");
 * uni.buildAssociation(professor, "profs", mb.MANY, null, 1);
 *
 * ClassModel model = mb.getClassModel();
 * <!-- end_code_fragment: -->
 * </pre>
 */
public class ClassDiagrams
{
   /**
    * create a class diagram png in modelFolder/doc-files/classDiagram.png
    *
    * @param model
    *    the class model
    */
   public String dumpPng(ClassModel model)
   {
      String diagramFileName = model.getPackageSrcFolder() + "/doc-files/classDiagram.png";
      return this.dumpPng(model, diagramFileName);
   }

   /**
    * create a class diagram png in modelFolder/doc-files/classDiagram.png
    *
    * @param model
    *    the class model
    */
   public String dumpPng(ClassModel model, String diagramFileName)
   {
      return this.dump(model, diagramFileName, Format.PNG);
   }

   /**
    * create a class diagram png in modelFolder/doc-files/classDiagram.png
    *
    * @param model
    *    the class model
    */
   public String dumpSVG(ClassModel model, String diagramFileName)
   {
      return this.dump(model, diagramFileName, Format.SVG);
   }

   public String dump(ClassModel model, String diagramFileName, Format format)
   {
      try
      {
         final StringBuilder dot = new StringBuilder();
         dot.append("digraph \"").append(model.getPackageName()).append("\" {\n");
         dot.append("rankdir=BT\n");

         this.makeNodes(model, dot);
         dot.append('\n');
         this.makeEdges(model, dot);
         dot.append('\n');

         dot.append("}\n");

         Graphviz.fromString(dot.toString()).render(format).toFile(new File(diagramFileName));

         return diagramFileName;
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return null;
   }

   private void makeEdges(ClassModel model, StringBuilder buf)
   {
      final Set<AssocRole> done = Collections.newSetFromMap(new IdentityHashMap<>());

      for (Clazz clazz : model.getClasses())
      {
         for (AssocRole assoc : clazz.getRoles())
         {
            if (done.contains(assoc))
            {
               continue;
            }
            done.add(assoc);
            if (assoc.getOther() != null)
            {
               done.add(assoc.getOther());
            }

            this.makeEdge(assoc, buf);
         }
      }
   }

   private void makeEdge(AssocRole assoc, StringBuilder buf)
   {
      final String sourceId = assoc.getClazz().getName();
      final String targetId = assoc.getOther().getClazz().getName();

      buf.append(targetId).append(" -> ").append(sourceId).append(" [\n");
      buf.append("  arrowhead=none\n");

      buf.append("  taillabel=\"");
      this.appendLabel(assoc, buf);
      buf.append("\"\n");

      buf.append("  headlabel=\"");
      this.appendLabel(assoc.getOther(), buf);
      buf.append("\"\n");

      buf.append("];\n");
   }

   private void appendLabel(AssocRole assoc, StringBuilder buf)
   {
      if (assoc.getName() != null)
      {
         buf.append(assoc.getName());
      }
      if (assoc.getCardinality() != ClassModelBuilder.ONE)
      {
         buf.append(" *");
      }
   }

   private void makeNodes(ClassModel model, StringBuilder buf)
   {
      for (Clazz clazz : model.getClasses())
      {
         String objId = clazz.getName();

         buf.append(objId).append(" " + "[\n" + "   shape=plaintext\n" + "   label=<\n"
                                  + "     <table border='0' cellborder='1' cellspacing='0'>\n" + "       <tr><td>")
            .append(objId).append("</td></tr>\n" + "       <tr><td>");

         for (Attribute key : clazz.getAttributes())
         {
            buf.append(key.getName()).append(" :").append(StringEscapeUtils.escapeHtml4(key.getType()))
               .append("<br  align='left'/>");
         }

         buf.append("</td></tr>\n" + "     </table>\n" + "  >];\n");
      }
   }
}
