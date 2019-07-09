package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.fulib.StrUtil;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.yaml.Reflector;
import org.fulib.yaml.ReflectorMap;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * Create object diagrams.
 * <pre>
 * <!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
 FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
 * <!-- end_code_fragment: -->
 * </pre>
 * Example: <br>
 * <img src="doc-files/studyRightObjects.png" width="343">
 */
public class ObjectDiagrams
{
   private LinkedHashMap<Object,String> diagramNames = new LinkedHashMap<>();


   /**
    * create an object diagram png in tmp/TheFirstObjectsClass.1.png <br>
    * Example: <br>
    * <img src="doc-files/studyRightObjects.png" width="343">
    * @param objectList
    */
   public String dumpPng(Object... objectList)
   {
      Objects.requireNonNull(objectList);
      if (objectList.length < 1) throw new IllegalArgumentException("missing root object");

      Object firstRoot = objectList[0];
      String diagramFileName = diagramNames.get(firstRoot);
      if (diagramFileName == null)
      {
         diagramFileName = "tmp/" + lastDotSplit(firstRoot.getClass().getName()) + "." + (diagramNames.size()+1) + ".png";
         diagramNames.put(firstRoot, diagramFileName);
      }
      return dumpPng(diagramFileName, objectList);
   }


   private String lastDotSplit(String name)
   {
      String[] split = name.split("\\.");
      return split[split.length-1];
   }


   /**
    * Create object diagrams.
    * <pre>
    * <!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
    FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
    * <!-- end_code_fragment: -->
    * </pre>
    * Example: <br>
    * <img src="doc-files/studyRightObjects.png" width="343">
    * @param diagramFileName
    * @param objectList
    * @return file name
    */
   public String dumpPng(String diagramFileName, Object... objectList)
   {
      return dump(Format.PNG, diagramFileName, objectList);
   }


   /**
    * Create object diagrams.
    * <pre>
    * <!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
    FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
    * <!-- end_code_fragment: -->
    * </pre>
    * Example: <br>
    * <img src="doc-files/studyRightObjects.png" width="343">
    * @param diagramFileName
    * @param objectList
    * @return file name
    */
   public String dumpSVG(String diagramFileName, Object... objectList)
   {
      return dump(Format.SVG_STANDALONE, diagramFileName, objectList);
   }


   /**
    * Create yaml description.
    *
    * @param diagramFileName
    * @param objectList
    * @return file name
    */
   public String dumpYaml(String diagramFileName, Object... objectList)
   {
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
      try
      {
         Objects.requireNonNull(objectList);
         if (objectList.length < 1) throw new IllegalArgumentException("empty objectList");

         Object firstRoot = objectList[0];

         String packageName = firstRoot.getClass().getPackage().getName();
         YamlIdMap idMap = new YamlIdMap(packageName);
         ReflectorMap reflectorMap = new ReflectorMap(packageName);
         LinkedHashSet<Object> diagramObjects = idMap.collectObjects(objectList);
         LinkedHashMap<String,LinkedHashMap<String,String>> edgesMap = new LinkedHashMap<>();


         String dotString = "" +
               "digraph H {\n" +
               // "rankdir=BT\n" +
               "<nodes> \n" +
               "<edges> \n" +
               "}\n";

         String nodesString = makeNodes(diagramObjects, idMap, reflectorMap, edgesMap);
         String edgesString = makeEdges(diagramObjects, idMap, reflectorMap, edgesMap);

         ST st = new ST(dotString);
         st.add("nodes", nodesString);
         st.add("edges", edgesString);
         dotString = st.render();

         Graphviz.fromString(dotString.toString()).render(format).toFile(new File(diagramFileName));

         return diagramFileName;
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return null;
   }

   private String makeEdges(LinkedHashSet<Object> diagramObjects, YamlIdMap idMap, ReflectorMap reflectorMap,
                            LinkedHashMap<String,LinkedHashMap<String,String>> edgesMap)
   {
      StringBuilder buf = new StringBuilder();

      for (LinkedHashMap<String,String> edge: edgesMap.values())
      {

         String sourceId = edge.get("src");
         String targetId = edge.get("tgt");

         String sourceLabel = edge.get("tail");
         sourceLabel = sourceLabel == null ? " " : sourceLabel;

         String targetLabel = edge.get("head");

         buf.append(sourceId).append(" -> ").append(targetId)
               .append(" [arrowhead=none fontsize=\"10\" " +
                     "taillabel=\"" + sourceLabel + "\" " +
                     "headlabel=\"" + targetLabel + "\"];\n");
      }

      return buf.toString();
   }

   private String makeNodes(LinkedHashSet<Object> diagramObjects, YamlIdMap idMap, ReflectorMap reflectorMap,
                            LinkedHashMap<String,LinkedHashMap<String,String>> edgesMap)
   {
      StringBuilder buf = new StringBuilder();

      for (Map.Entry<String, Object> entry : idMap.getObjIdMap().entrySet())
      {
         String key = entry.getKey();
         Object obj = entry.getValue();

         if ( ! diagramObjects.contains(obj)) continue;

         String className = obj.getClass().getSimpleName();

         if (obj instanceof YamlObject)
         {
            YamlObject yamlObj = (YamlObject) obj;
            Object type = yamlObj.getMap().get("type");
            if (type != null)
            {
               className = type.toString();
            }
         }

         // attrs
         Reflector creator = reflectorMap.getReflector(obj);
         String userKey = key;
         Object tmp = creator.getValue(obj, "id");
         if (tmp != null) {
            userKey = StrUtil.downFirstChar(tmp.toString());

         }
         else {
            tmp = creator.getValue(obj, "name");
            if (tmp != null) {
               userKey = StrUtil.downFirstChar(tmp.toString());
            }
         }

         buf.append(key).append(" " +
               "[\n" +
               "   shape=plaintext\n" +
               "   fontsize=\"10\"\n" +
               "   label=<\n"  +
               "     <table border='0' cellborder='1' cellspacing='0'>\n" +
               "       <tr><td>")
               .append("<u>").append(userKey).append(" :").append(className).append("</u>")
               .append("</td></tr>\n"  +
                     "       <tr><td>");


         for (String prop : creator.getProperties())
         {
            if (obj instanceof YamlObject && ".id".equals(prop)) continue;
            if (obj instanceof YamlObject && "type".equals(prop)) continue;

            Object value = creator.getValue(obj, prop);

            if (value == null)
            {
               buf.append("  ").append(prop).append(" = null").append("<br  align='left'/>");
               continue;
            }

            if (value instanceof Collection)
            {
               for (Object elem : (Collection) value)
               {
                  if (diagramObjects.contains(elem))
                  {
                     String targetKey = idMap.getIdObjMap().get(elem);
                     addEdge(edgesMap, key, targetKey, prop);
                  }
               }
            }
            else
            {
               String valueKey = idMap.getIdObjMap().get(value);

               if (valueKey != null)
               {
                  if (diagramObjects.contains(value))
                  {
                     String targetKey = idMap.getIdObjMap().get(value);
                     addEdge(edgesMap, key, targetKey, prop);
                  }
               }
               else
               {
                  if (value instanceof String)
                  {
                     value = encodeDotString(value);
                  }
                  buf.append("  ").append(prop).append(" = ").append(value.toString()).append("<br  align='left'/>");
               }
            }
         }

         buf.append("</td></tr>\n" +
               "     </table>\n" +
               "  >];\n");
      }

      return buf.toString();
   }

   private void addEdge(LinkedHashMap<String, LinkedHashMap<String, String>> edgesMap, String key, String targetKey, String prop)
   {
      String fwdKey = key + ">" + targetKey;
      String reverseKey = targetKey + ">" + key;
      LinkedHashMap<String, String> reverseEdge = edgesMap.get(reverseKey);
      if (reverseEdge != null)
      {
         // add prop to tail label
         String tailLabel = reverseEdge.get("tail");
         tailLabel = (tailLabel == null) ? prop : tailLabel + "\\n" +  prop;
         reverseEdge.put("tail", tailLabel);
      }
      else
      {
         LinkedHashMap<String, String> edge = edgesMap.get(fwdKey);
         if (edge == null)
         {
            edge = new LinkedHashMap<String, String>();
            edgesMap.put(fwdKey, edge);
            edge.put("src", key);
            edge.put("tgt", targetKey);
         }
         String label = edge.get("head");
         label = (label == null) ? prop  : label + "\\n" + prop;
         edge.put("head", label);
      }
   }

   private Object encodeDotString(Object value)
   {
      String newValue = (String) value;
      newValue = "\"" + newValue.replaceAll("\"", "\\\"") + "\"";
      // newValue = newValue.replaceAll("%", "");
      newValue = newValue.replaceAll("<", "&lt;");
      newValue = newValue.replaceAll(">", "&gt;");
      newValue = newValue.replaceAll("&", "&amp;");
      return newValue;
   }
}
