package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.apache.commons.text.StringEscapeUtils;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;

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
         String dotString = "" + "digraph H {\n" + "rankdir=BT\n" + "<nodes> \n" + "<edges> \n" + "}\n";

         String nodesString = this.makeNodes(model);
         String edgesString = this.makeEdges(model);

         ST st = new ST(dotString);
         st.add("nodes", nodesString);
         st.add("edges", edgesString);
         dotString = st.render();

         Graphviz.fromString(dotString).render(format).toFile(new File(diagramFileName));

         return diagramFileName;
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return null;
   }

   private String makeEdges(ClassModel model)
   {
      StringBuilder buf = new StringBuilder();

      LinkedHashSet<AssocRole> roles = new LinkedHashSet<>();

      for (Clazz c : model.getClasses())
      {
         roles.addAll(c.getRoles());
      }

      LinkedHashSet<AssocRole> done = new LinkedHashSet<>();
      for (AssocRole assoc : roles)
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

         String sourceId = assoc.getClazz().getName();
         String targetId = assoc.getOther().getClazz().getName();

         String sourceLabel = assoc.getName();
         if (sourceLabel == null)
         {
            sourceLabel = "";
         }
         if (assoc.getCardinality() != ClassModelBuilder.ONE)
         {
            sourceLabel += " *";
         }

         String targetLabel = assoc.getOther().getName();
         if (targetLabel == null)
         {
            targetLabel = "";
         }
         if (assoc.getOther().getCardinality() != ClassModelBuilder.ONE)
         {
            targetLabel += " *";
         }

         buf.append(targetId).append(" -> ").append(sourceId).append(" [arrowhead=none fontsize=\"10\" taillabel=\"")
            .append(sourceLabel).append("\" headlabel=\"").append(targetLabel).append("\"];\n");
      }

      return buf.toString();
   }

   private String makeNodes(ClassModel model)
   {
      StringBuilder buf = new StringBuilder();

      for (Clazz clazz : model.getClasses())
      {
         String objId = clazz.getName();

         buf.append(objId).append(" " + "[\n" + "   shape=plaintext\n" + "   fontsize=\"10\"\n" + "   label=<\n"
                                  + "     <table border='0' cellborder='1' cellspacing='0'>\n" + "       <tr><td>")
            .append(objId).append("</td></tr>\n" + "       <tr><td>");

         for (Attribute key : clazz.getAttributes())
         {
            buf.append(key.getName()).append(" :").append(StringEscapeUtils.escapeHtml4(key.getType()))
               .append("<br  align='left'/>");
         }

         buf.append("</td></tr>\n" + "     </table>\n" + "  >];\n");
      }

      return buf.toString();
   }
}

