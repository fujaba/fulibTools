package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.fulib.StrUtil;
import org.fulib.tools.diagrams.DiagramEdge;
import org.fulib.tools.diagrams.DiagramObject;
import org.fulib.yaml.Reflector;
import org.fulib.yaml.ReflectorMap;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Create object diagrams.
 * <pre>
 * <!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
 * FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
 * <!-- end_code_fragment: -->
 * </pre>
 * Example: <br>
 * <img src="doc-files/studyRightObjects.png" width="343" alt="StudyRight Objects">
 */
public class ObjectDiagrams
{
   private static final STGroup TEMPLATE_GROUP = new STGroupFile(
      ClassDiagrams.class.getResource("templates/objectDiagram.stg"));

   static {
      final StringRenderer stringRenderer = new StringRenderer();
      TEMPLATE_GROUP.registerRenderer(Object.class,
                                      (value, formatString, locale) -> stringRenderer.toString(Objects.toString(value),
                                                                                               formatString, locale));
   }

   private final Map<Object, String> diagramNames = new LinkedHashMap<>();

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
    * @param scale the scale factor for rendering
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
   public ObjectDiagrams withScale(double scale)
   {
      this.setScale(scale);
      return this;
   }

   /**
    * create an object diagram png in tmp/TheFirstObjectsClass.1.png <br>
    * Example: <br>
    * <img src="doc-files/studyRightObjects.png" width="343" alt="StudyRight Objects">
    *
    * @param objectList the list of objects to display
    *
    * @return the file name
    */
   public String dumpPng(Object... objectList)
   {
      if (objectList.length == 0)
      {
         throw new IllegalArgumentException("missing root object");
      }

      final Object firstRoot = objectList[0];
      final String diagramFileName = getDiagramFileName(firstRoot);
      return this.dumpPng(diagramFileName, objectList);
   }

   private String getDiagramFileName(Object firstRoot)
   {
      return this.diagramNames.computeIfAbsent(firstRoot, obj -> {
         final String className = obj.getClass().getSimpleName();
         final int uniqueNumber = this.diagramNames.size() + 1;
         return "tmp/" + className + "." + uniqueNumber + ".png";
      });
   }

   /**
    * Create object diagrams.
    * <pre>
    * <!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
    * FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
    * <!-- end_code_fragment: -->
    * </pre>
    * Example: <br>
    * <img src="doc-files/studyRightObjects.png" width="343" alt="StudyRight Objects">
    *
    * @param diagramFileName the file name in which the diagram should be saved
    * @param objectList the list of objects to display
    *
    * @return the file name (= {@code diagramFileName}), for compatibility with {@link #dumpPng(Object...)}
    */
   public String dumpPng(String diagramFileName, Object... objectList)
   {
      return this.dump(Format.PNG, diagramFileName, objectList);
   }

   /**
    * Create object diagrams.
    * <pre>
    * <!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
    * FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
    * <!-- end_code_fragment: -->
    * </pre>
    * Example: <br>
    * <img src="doc-files/studyRightObjects.png" width="343" alt="StudyRight Objects">
    *
    * @param diagramFileName the file name in which the diagram should be saved
    * @param objectList the list of objects to display
    *
    * @return the file name (= {@code diagramFileName}), for compatibility with {@link #dumpPng(Object...)}
    */
   public String dumpSVG(String diagramFileName, Object... objectList)
   {
      if (diagramFileName.endsWith(".scenario.svg"))
      {
         new ScenarioDiagrams().dump(diagramFileName, objectList[0]);
         return diagramFileName;
      }

      return this.dump(Format.SVG_STANDALONE, diagramFileName, objectList);
   }

   /**
    * Create yaml description.
    *
    * @param diagramFileName the file name in which the diagram should be saved
    * @param objectList the list of objects to display
    *
    * @return the file name (= {@code diagramFileName}), for compatibility with {@link #dumpPng(Object...)}
    */
   public String dumpYaml(String diagramFileName, Object... objectList)
   {
      objectList = flatten(objectList);
      if (objectList.length == 0)
      {
         throw new IllegalArgumentException("empty objectList");
      }

      final Object firstObject = objectList[0];
      final String packageName = firstObject.getClass().getPackage().getName();
      final YamlIdMap idMap = new YamlIdMap(packageName);
      final String yaml = idMap.encode(objectList);

      try
      {
         final Path path = Paths.get(diagramFileName);
         Files.createDirectories(path.getParent());
         Files.write(path, yaml.getBytes(StandardCharsets.UTF_8));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return diagramFileName;
   }

   private String dump(Format format, String diagramFileName, Object... objectList)
   {
      objectList = flatten(objectList);
      if (objectList.length == 0)
      {
         throw new IllegalArgumentException("empty objectList");
      }

      final Object firstRoot = objectList[0];

      final String packageName = firstRoot.getClass().getPackage().getName();
      final YamlIdMap idMap = new YamlIdMap(packageName);
      final ReflectorMap reflectorMap = new ReflectorMap(packageName);
      final Set<Object> relevantObjects = idMap.collectObjects(objectList);
      final Set<DiagramObject> diagramObjects = new LinkedHashSet<>();
      final Set<DiagramEdge> edges = new LinkedHashSet<>();

      this.makeNodes(relevantObjects, idMap, reflectorMap, diagramObjects, edges);

      final ST st = TEMPLATE_GROUP.getInstanceOf("objectDiagram");
      st.add("title", packageName);
      st.add("objects", diagramObjects);
      st.add("edges", edges);
      final String dotString = st.render();

      try
      {
         Graphviz.fromString(dotString).scale(this.scale).render(format).toFile(new File(diagramFileName));

         return diagramFileName;
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return null;
   }

   private static Object[] flatten(Object... objectList)
   {
      final List<Object> flatList = new ArrayList<>();
      for (Object obj : objectList)
      {
         if (obj instanceof Collection)
         {
            flatList.addAll((Collection<?>) obj);
         }
         else
         {
            flatList.add(obj);
         }
      }

      return flatList.toArray();
   }

   private void makeNodes(Set<Object> relevantObjects, YamlIdMap idMap, ReflectorMap reflectorMap,
      Set<DiagramObject> objects, Set<DiagramEdge> edges)
   {
      for (Map.Entry<String, Object> entry : idMap.getObjIdMap().entrySet())
      {
         final String key = entry.getKey();
         final Object obj = entry.getValue();

         if (!relevantObjects.contains(obj))
         {
            continue;
         }

         final String className = this.getClassName(obj);

         final Reflector creator = reflectorMap.getReflector(obj);
         final String userKey = this.getUserKey(key, obj, creator);

         final Map<String, Object> attributes = new LinkedHashMap<>();
         final DiagramObject diagramObject = new DiagramObject(key, userKey, className, attributes);
         objects.add(diagramObject);

         for (String prop : creator.getAllProperties())
         {
            if (obj instanceof YamlObject && (".id".equals(prop) || "type".equals(prop)))
            {
               continue;
            }

            Object value = creator.getValue(obj, prop);

            if (value == null)
            {
               if (this.getPropertyType(obj, prop) == String.class)
               {
                  attributes.put(prop, "null");
               }
               continue;
            }

            if (value instanceof Collection)
            {
               boolean hasValues = false;
               for (Object elem : (Collection<?>) value)
               {
                  if (relevantObjects.contains(elem))
                  {
                     String targetKey = idMap.getId(elem);
                     this.addEdge(edges, key, targetKey, prop);
                  }
                  else
                  {
                     hasValues = true;
                  }
               }
               if (hasValues)
               {
                  attributes.put(prop, value.toString());
               }
            }
            else if (relevantObjects.contains(value))
            {
               final String targetKey = idMap.getId(value);
               this.addEdge(edges, key, targetKey, prop);
            }
            else
            {
               attributes.put(prop, this.renderValue(value));
            }
         }
      }
   }

   private String getClassName(Object obj)
   {
      if (obj instanceof YamlObject)
      {
         final YamlObject yamlObj = (YamlObject) obj;
         final Object type = yamlObj.getType();
         if (type != null)
         {
            return type.toString();
         }
      }

      return obj.getClass().getSimpleName();
   }

   private String getUserKey(String key, Object obj, Reflector reflector)
   {
      final Object id = reflector.getValue(obj, "id");
      if (id != null)
      {
         return StrUtil.downFirstChar(id.toString());
      }

      final Object name = reflector.getValue(obj, "name");
      if (name != null)
      {
         return StrUtil.downFirstChar(name.toString());
      }

      return key;
   }

   private Class<?> getPropertyType(Object obj, String property)
   {
      try
      {
         final Method method = obj.getClass().getMethod("get" + StrUtil.cap(property));
         return method.getReturnType();
      }
      catch (Exception e)
      {
         return null;
      }
   }

   private Object renderValue(Object value)
   {
      if (value instanceof String)
      {
         return "\"" + ((String) value).replace("\"", "\\\"") + "\"";
      }
      else if (isLambdaClass(value.getClass()))
      {
         return "<lambda expression>";
      }
      return value;
   }

   private static boolean isLambdaClass(Class<?> aClass)
   {
      final String className = aClass.getName();
      final int lambdaIndex = className.indexOf("$$Lambda$");
      return 0 <= lambdaIndex && lambdaIndex <= className.indexOf('/');
   }

   private void addEdge(Set<DiagramEdge> edges, String key, String targetKey, String prop)
   {
      // TODO this is inefficient
      for (DiagramEdge existing : edges)
      {
         if (key.equals(existing.getSource()) && targetKey.equals(existing.getTarget()))
         {
            existing.setSourceLabel(prop);
            return;
         }
         else if (key.equals(existing.getTarget()) && targetKey.equals(existing.getSource()))
         {
            existing.setTargetLabel(prop);
            return;
         }
      }
      final DiagramEdge edge = new DiagramEdge(key, targetKey, prop, null);
      edges.add(edge);
   }
}
