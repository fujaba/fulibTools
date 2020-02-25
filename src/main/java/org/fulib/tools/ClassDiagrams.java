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
 * Provides various methods for storing class diagrams as files.
 */
public class ClassDiagrams
{
   /**
    * Create a class diagram of the given class model at the path
    * {@link ClassModel#getPackageSrcFolder() modelFolder}{@code /doc-files/classDiagram.png}.
    * in png format.
    *
    * @param model
    *    the class model
    *
    * @return the diagram file name
    */
   public String dumpPng(ClassModel model)
   {
      String diagramFileName = model.getPackageSrcFolder() + "/doc-files/classDiagram.png";
      return this.dumpPng(model, diagramFileName);
   }

   /**
    * Creates a class diagram of the given class model at the path specified by {@code diagramFileName}
    * in png format.
    *
    * @param model
    *    the class model
    * @param diagramFileName
    *    the diagram file name
    *
    * @return the diagram file name
    */
   public String dumpPng(ClassModel model, String diagramFileName)
   {
      return this.dump(model, diagramFileName, Format.PNG);
   }

   /**
    * Creates a class diagram of the given class model at the path specified by {@code diagramFileName}
    * in svg format.
    *
    * @param model
    *    the class model
    * @param diagramFileName
    *    the diagram file name
    *
    * @return the diagram file name
    */
   public String dumpSVG(ClassModel model, String diagramFileName)
   {
      return this.dump(model, diagramFileName, Format.SVG);
   }

   /**
    * Creates a class diagram of the given class model at the path specified by {@code diagramFileName}
    * in the given Graphviz {@code format}.
    *
    * @param model
    *    the class model
    * @param diagramFileName
    *    the diagram file name
    * @param format
    *    the file format
    *
    * @return the diagram file name
    */
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

         buf
            .append(objId)
            .append(" " + "[\n" + "   shape=plaintext\n" + "   label=<\n"
                    + "     <table border='0' cellborder='1' cellspacing='0'>\n" + "       <tr><td>")
            .append(objId)
            .append("</td></tr>\n" + "       <tr><td>");

         for (Attribute key : clazz.getAttributes())
         {
            buf
               .append(key.getName())
               .append(" :")
               .append(StringEscapeUtils.escapeHtml4(key.getType()))
               .append("<br  align='left'/>");
         }

         buf.append("</td></tr>\n" + "     </table>\n" + "  >];\n");
      }
   }
}
