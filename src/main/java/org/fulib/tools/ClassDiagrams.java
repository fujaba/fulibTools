package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;

public class ClassDiagrams
{

   /**
    * create a class diagram png in modelFolder/doc-files/classDiagram.png
    * @param model
    */
   public static String dumpPng(ClassModel model)
   {
      return new ClassDiagrams().doClassDiag(model);
   }

   /**
    * create a class diagram png in modelFolder/doc-files/classDiagram.png
    * @param model
    */
   public static String dumpPng(ClassModel model, String diagramFileName)
   {
      return new ClassDiagrams().doClassDiag(model, diagramFileName);
   }


   public String doClassDiag(ClassModel model)
   {
      String diagramFileName = model.getPackageSrcFolder() + "/doc-files/classDiagram.png";
      return doClassDiag(model, diagramFileName);
   }

   public String doClassDiag(ClassModel model, String diagramFileName)
   {
      try
      {
         String dotString = "" +
               "digraph H {\n" +
               "rankdir=BT\n" +
               "<nodes> \n" +
               "<edges> \n" +
               "}\n";


         String nodesString = makeNodes(model);
         String edgesString = makeEdges(model);

         ST st = new ST(dotString);
         st.add("nodes", nodesString);
         st.add("edges", edgesString);
         dotString = st.render();

         Graphviz.fromString(dotString.toString()).render(Format.PNG).toFile(new File(diagramFileName));

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
         if (assoc.getCardinality() == ClassModelBuilder.MANY)
         {
            sourceLabel += " *";
         }

         String targetLabel = assoc.getOther().getName();
         if (assoc.getOther().getCardinality() == ClassModelBuilder.MANY)
         {
            targetLabel += " *";
         }

         buf.append(targetId).append(" -> ").append(sourceId)
               .append(" [arrowhead=none fontsize=\"10\" " +
                     "taillabel=\"" + sourceLabel + "\" " +
                     "headlabel=\"" + targetLabel + "\"];\n");
      }

      return buf.toString();
   }

   private String makeNodes(ClassModel model)
   {
      StringBuilder buf = new StringBuilder();

      for (Clazz clazz : model.getClasses())
      {
         String objId = clazz.getName();

         buf.append(objId).append(" " +
               "[\n" +
               "   shape=plaintext\n" +
               "   fontsize=\"10\"\n" +
               "   label=<\n"  +
               "     <table border='0' cellborder='1' cellspacing='0'>\n" +
               "       <tr><td>")
               .append(objId)
               .append("</td></tr>\n"  +
                     "       <tr><td>");

         for (Attribute key : clazz.getAttributes())
         {
            buf.append(key).append("<br  align='left'/>");
         }

         buf.append("</td></tr>\n" +
               "     </table>\n" +
               "  >];\n");
      }

      return buf.toString();
   }
}

