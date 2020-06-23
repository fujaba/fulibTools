package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.fulib.StrUtil;
import org.fulib.yaml.Reflector;
import org.fulib.yaml.ReflectorMap;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
   private LinkedHashMap<Object, String> diagramNames = new LinkedHashMap<>();

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

      Object firstRoot = objectList[0];
      String diagramFileName = this.diagramNames.get(firstRoot);
      if (diagramFileName == null)
      {
         diagramFileName =
            "tmp/" + firstRoot.getClass().getSimpleName() + "." + (this.diagramNames.size() + 1) + ".png";
         this.diagramNames.put(firstRoot, diagramFileName);
      }
      return this.dumpPng(diagramFileName, objectList);
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

      Object firstObject = objectList[0];
      String packageName = firstObject.getClass().getPackage().getName();
      YamlIdMap idMap = new YamlIdMap(packageName);
      String yaml = idMap.encode(objectList);

      try
      {
         Files.write(Paths.get(diagramFileName), yaml.getBytes(StandardCharsets.UTF_8));
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

      Object firstRoot = objectList[0];

      String packageName = firstRoot.getClass().getPackage().getName();
      YamlIdMap idMap = new YamlIdMap(packageName);
      ReflectorMap reflectorMap = new ReflectorMap(packageName);
      LinkedHashSet<Object> diagramObjects = idMap.collectObjects(objectList);
      LinkedHashMap<String, LinkedHashMap<String, String>> edgesMap = new LinkedHashMap<>();

      String nodesString = this.makeNodes(diagramObjects, idMap, reflectorMap, edgesMap);
      String edgesString = this.makeEdges(edgesMap);

      String dotString = "digraph H {\n" + nodesString + "\n" + edgesString + "\n" + "}\n";

      try
      {
         Graphviz.fromString(dotString).render(format).toFile(new File(diagramFileName));

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

   private String makeEdges(LinkedHashMap<String, LinkedHashMap<String, String>> edgesMap)
   {
      StringBuilder buf = new StringBuilder();

      for (LinkedHashMap<String, String> edge : edgesMap.values())
      {

         String sourceId = edge.get("src");
         String targetId = edge.get("tgt");

         String sourceLabel = edge.get("tail");
         sourceLabel = sourceLabel == null ? " " : sourceLabel;

         String targetLabel = edge.get("head");

         buf.append(sourceId).append(" -> ").append(targetId)
            .append(" [arrowhead=none fontsize=\"10\" " + "taillabel=\"").append(sourceLabel).append("\" ")
            .append("headlabel=\"").append(targetLabel).append("\"];\n");
      }

      return buf.toString();
   }

   private String makeNodes(LinkedHashSet<Object> diagramObjects, YamlIdMap idMap, ReflectorMap reflectorMap,
      LinkedHashMap<String, LinkedHashMap<String, String>> edgesMap)
   {
      StringBuilder buf = new StringBuilder();

      for (Map.Entry<String, Object> entry : idMap.getObjIdMap().entrySet())
      {
         String key = entry.getKey();
         Object obj = entry.getValue();

         if (!diagramObjects.contains(obj))
         {
            continue;
         }

         String className = obj.getClass().getSimpleName();

         if (obj instanceof YamlObject)
         {
            YamlObject yamlObj = (YamlObject) obj;
            Object type = yamlObj.getType();
            if (type != null)
            {
               className = type.toString();
            }
         }

         // attrs
         Reflector creator = reflectorMap.getReflector(obj);
         String userKey = key;
         Object tmp = creator.getValue(obj, "id");
         if (tmp != null)
         {
            userKey = StrUtil.downFirstChar(tmp.toString());
         }
         else
         {
            tmp = creator.getValue(obj, "name");
            if (tmp != null)
            {
               userKey = StrUtil.downFirstChar(tmp.toString());
            }
         }

         buf.append(key).append(" " + "[\n" + "   shape=plaintext\n" + "   fontsize=\"10\"\n" + "   label=<\n"
                                + "     <table border='0' cellborder='1' cellspacing='0'>\n" + "       <tr><td>")
            .append("<u>").append(userKey).append(" :").append(className).append("</u>")
            .append("</td></tr>\n" + "       <tr><td>");

         for (String prop : creator.getOwnProperties())
         {
            if (obj instanceof YamlObject && ".id".equals(prop))
            {
               continue;
            }
            if (obj instanceof YamlObject && "type".equals(prop))
            {
               continue;
            }

            Object value = creator.getValue(obj, prop);

            if (value == null)
            {
               try
               {
                  Method method = obj.getClass().getMethod("get" + StrUtil.cap(prop));
                  Class<?> fieldType = method.getReturnType();
                  if (fieldType == String.class)
                  {
                     buf.append("  ").append(prop).append(" = null").append("<br  align='left'/>");
                  }
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
               continue;
            }

            if (value instanceof Collection)
            {
               for (Object elem : (Collection<?>) value)
               {
                  if (diagramObjects.contains(elem))
                  {
                     String targetKey = idMap.getId(elem);
                     this.addEdge(edgesMap, key, targetKey, prop);
                  }
               }
            }
            else
            {
               String valueKey = idMap.getId(value);

               if (valueKey != null)
               {
                  if (diagramObjects.contains(value))
                  {
                     String targetKey = idMap.getId(value);
                     this.addEdge(edgesMap, key, targetKey, prop);
                  }
               }
               else
               {
                  if (value instanceof String)
                  {
                     value = encodeDotString((String) value);
                  }
                  else if (isLambdaClass(value.getClass()))
                  {
                     value = "&lt;lambda expression&gt;";
                  }
                  buf.append("  ").append(prop).append(" = ").append(value.toString()).append("<br  align='left'/>");
               }
            }
         }

         buf.append("</td></tr>\n" + "     </table>\n" + "  >];\n");
      }

      return buf.toString();
   }

   private static boolean isLambdaClass(Class<?> aClass)
   {
      final String className = aClass.getName();
      final int lambdaIndex = className.indexOf("$$Lambda$");
      return 0 <= lambdaIndex && lambdaIndex <= className.indexOf('/');
   }

   private void addEdge(LinkedHashMap<String, LinkedHashMap<String, String>> edgesMap, String key, String targetKey,
      String prop)
   {
      String fwdKey = key + ">" + targetKey;
      String reverseKey = targetKey + ">" + key;
      LinkedHashMap<String, String> reverseEdge = edgesMap.get(reverseKey);
      if (reverseEdge != null)
      {
         // add prop to tail label
         String tailLabel = reverseEdge.get("tail");
         tailLabel = (tailLabel == null) ? prop : tailLabel + "\\n" + prop;
         reverseEdge.put("tail", tailLabel);
      }
      else
      {
         LinkedHashMap<String, String> edge = edgesMap.get(fwdKey);
         if (edge == null)
         {
            edge = new LinkedHashMap<>();
            edgesMap.put(fwdKey, edge);
            edge.put("src", key);
            edge.put("tgt", targetKey);
         }
         String label = edge.get("head");
         label = (label == null) ? prop : label + "\\n" + prop;
         edge.put("head", label);
      }
   }

   private static String encodeDotString(String value)
   {
      // value = value.replace("%", "");
      value = value.replace("&", "&amp;");
      value = value.replace("<", "&lt;");
      value = value.replace(">", "&gt;");
      value = "\"" + value.replace("\"", "\\\"") + "\"";
      return value;
   }
}
