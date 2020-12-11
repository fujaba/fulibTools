package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizException;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides various methods for storing class diagrams as files.
 */
public class ClassDiagrams
{
   private static final STGroup TEMPLATE_GROUP = new STGroupFile(
      ClassDiagrams.class.getResource("templates/classDiagram.stg"));

   static
   {
      TEMPLATE_GROUP.registerRenderer(String.class, new StringRenderer());
   }

   private double scale = 1;

   /**
    * @return the scale factor for rendering
    *
    * @since 1.2
    */
   public double getScale()
   {
      return scale;
   }

   /**
    * @param scale
    *    the scale factor for rendering
    *
    * @since 1.2
    */
   public void setScale(double scale)
   {
      this.scale = scale;
   }

   /**
    * Sets the scaling factor to use when rendering (applies to PNG and SVG format).
    * Default is {@code 1}.
    *
    * @param scale
    *    the scaling factor
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public ClassDiagrams withScale(double scale)
   {
      this.setScale(scale);
      return this;
   }

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
      final ST classDiagram = TEMPLATE_GROUP.getInstanceOf("classDiagram");
      classDiagram.add("classModel", model);
      classDiagram.add("roles", getRolesWithoutOthers(model));
      classDiagram.add("subClasses", getClassesWithSuperClasses(model));
      final String dotString = classDiagram.render();

      try
      {
         Graphviz.fromString(dotString).scale(this.getScale()).render(format).toFile(new File(diagramFileName));

         return diagramFileName;
      }
      catch (GraphvizException graphvizException)
      {
         throw new RuntimeException("Graphviz rendering failed for dot string from class model:\n" + dotString,
                                    graphvizException);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return null;
   }

   private static Set<AssocRole> getRolesWithoutOthers(ClassModel model)
   {
      final Set<AssocRole> result = new HashSet<>();
      for (final Clazz clazz : model.getClasses())
      {
         for (final AssocRole role : clazz.getRoles())
         {
            if (!result.contains(role.getOther()))
            {
               result.add(role);
            }
         }
      }
      return result;
   }

   private static Set<Clazz> getClassesWithSuperClasses(ClassModel model)
   {
      return model.getClasses().stream().filter(c -> c.getSuperClass() != null).collect(Collectors.toSet());
   }
}
