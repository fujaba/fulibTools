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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
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

   private static final Comparator<AssocRole> ASSOC_ROLE_COMPARATOR = Comparator.nullsFirst(
      Comparator.comparing(AssocRole::getName));

   private static final Comparator<Clazz> CLAZZ_COMPARATOR = Comparator.comparing(Clazz::getName);

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
      final List<Clazz> sortedClasses = getSortedClasses(model);
      final ST classDiagram = TEMPLATE_GROUP.getInstanceOf("classDiagram");
      classDiagram.add("classModel", model);
      classDiagram.add("classes", sortedClasses);
      classDiagram.add("roles", getRolesWithoutOthers(sortedClasses));
      classDiagram.add("subClasses", getClassesWithSuperClasses(sortedClasses));
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

   private static List<Clazz> getSortedClasses(ClassModel model)
   {
      return model.getClasses().stream().sorted(CLAZZ_COMPARATOR).collect(Collectors.toList());
   }

   private static Set<AssocRole> getRolesWithoutOthers(List<Clazz> classes)
   {
      final Set<AssocRole> result = new LinkedHashSet<>();
      classes.stream().map(Clazz::getRoles).flatMap(List::stream).sorted(ASSOC_ROLE_COMPARATOR).forEach(role -> {
         if (!result.contains(role.getOther()))
         {
            result.add(role);
         }
      });
      return result;
   }

   private static List<Clazz> getClassesWithSuperClasses(List<Clazz> classes)
   {
      return classes.stream().filter(c -> c.getSuperClass() != null).collect(Collectors.toList());
   }
}
