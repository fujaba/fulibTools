package org.fulib.tools;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.fulib.yaml.Reflector;
import org.fulib.yaml.ReflectorMap;
import org.fulib.yaml.YamlIdMap;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ScenarioDiagrams
{
   public void dump(String fileName, Object root)
   {
      Objects.requireNonNull(root);

      try
      {
         String packageName = root.getClass().getPackage().getName();
         YamlIdMap idMap = new YamlIdMap(packageName);
         ReflectorMap reflectorMap = new ReflectorMap(packageName);
         LinkedHashSet<Object> diagramObjects = idMap.collectObjects(root);

         String dotString = "" +
               "digraph H {\n" +
               // "rankdir=BT\n" +
               "<nodes> \n" +
               "<messages> \n" +
               "<edges> \n" +
               "}\n";

         StringBuilder messages = new StringBuilder();
         StringBuilder edges  = new StringBuilder();
         String nodesString = makeServices(root, idMap, reflectorMap, messages, edges);

         ST st = new ST(dotString);
         st.add("nodes", nodesString);
         st.add("messages", messages);
         st.add("edges", edges);
         dotString = st.render();

         Files.write(Paths.get("tmp/scenario-diagram.txt"), dotString.getBytes());

         Graphviz.fromString(dotString.toString()).render(Format.SVG).toFile(new File(fileName));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   private int objCount = 0;

   private String makeServices(Object root, YamlIdMap idMap, ReflectorMap reflectorMap, StringBuilder messages, StringBuilder edges)
   {
      // make subgraphs for all services
      Reflector rootReflector = reflectorMap.getReflector(root);
      Collection services = toCollection(rootReflector.getValue(root, "services"));

      StringBuilder buf = new StringBuilder();

      for (Object service : services)
      {
         String oneService = "" +
               "   subgraph cluster_<name> {\n" +
               "      color=darkgrey;\n" +
               "      label = \"<description>\";\n\n" +
               "<states>\n" +
               "   }\n\n";

         Reflector serviceReflector = reflectorMap.getReflector(service);
         String id = idMap.getIdObjMap().get(service);
         Object description = serviceReflector.getValue(service, "description");
         String states = makeStates(service, idMap, reflectorMap, messages, edges);

         ST st = new ST(oneService);
         st.add("name", id);
         st.add("description", description);
         st.add("states", states);
         oneService = st.render();

         buf.append(oneService);
      }

      return buf.toString();
   }


   private String makeStates(Object service, YamlIdMap idMap, ReflectorMap reflectorMap, StringBuilder messages, StringBuilder edges)
   {
      Reflector reflector = reflectorMap.getReflector(service);
      Collection states = toCollection(reflector.getValue(service, "states"));
      StringBuilder buf = new StringBuilder();

      for (Object state : states)
      {
         StringBuilder nodeBuf = new StringBuilder();
         Reflector stateReflector = makeOneNode(idMap, reflectorMap, nodeBuf, state);
         nodeBuf.append(buf);
         buf = nodeBuf;
         Object stateId = idMap.getIdObjMap().get(state);
         if (stateId == null) {
            stateId = "state" + objCount++;
         }

         Collection sendMessages = toCollection(stateReflector.getValue(state, "sendMessages"));
         for (Object message : sendMessages)
         {
            Reflector messageReflector = reflectorMap.getReflector(message);
            Object messageId = idMap.getIdObjMap().get(message);
            makeOneNode(idMap, reflectorMap, messages, message);

            edges.append(String.format("   %s -> %s;\n", stateId, messageId));

            Collection targets = (Collection) messageReflector.getValue(message, "targets");
            for (Object target : targets)
            {
               Reflector targetReflector = reflectorMap.getReflector(target);
               Object targetId = idMap.getIdObjMap().get(target);
               edges.append(String.format("   %s -> %s;\n", messageId, targetId));
            }
         }
      }

      return buf.toString();
   }

   private Reflector makeOneNode(YamlIdMap idMap, ReflectorMap reflectorMap, StringBuilder buf, Object state)
   {
      String oneNode = "" +
            "      <id> [\n" +
            "           shape=plaintext\n" +
            "           fontsize=\"10\"\n" +
            "           label=\\<\n" +
            "             \\<table border='1' cellborder='0' cellspacing='0'>\n" +
            "<rows>" +
            "             \\</table>\n" +
            "          >];\n";

      Reflector stateReflector = reflectorMap.getReflector(state);
      Object id = idMap.getIdObjMap().get(state);
      Object time = stateReflector.getValue(state, "time");
      if (time == null) {
         time = "00:00:00";
      }

      StringBuilder descriptionTable = new StringBuilder();
      descriptionTable.append(String.format("                <tr><td>%s</td></tr>\n", time));

      makeOneDescription(state, stateReflector, descriptionTable);

      // add content
      Collection content = toCollection(stateReflector.getValue(state, "content"));

      for (Object c : content)
      {
         Reflector contentReflector = reflectorMap.getReflector(c);
         makeOneDescription(c, contentReflector, descriptionTable);
      }

      ST st = new ST(oneNode);
      st.add("id", id);
      st.add("rows", descriptionTable.toString());
      oneNode = st.render();

      buf.append(oneNode);
      return stateReflector;
   }

   private StringBuilder makeOneDescription(Object state, Reflector stateReflector, StringBuilder descriptionTable)
   {
      // add description
      String description = (String) stateReflector.getValue(state, "description");
      if (description == null) {
         description = "";
      }

      StringBuilder rows = new StringBuilder();

      String[] split = description.split("\n");

      for (String line : split)
      {
         StringBuilder cells = new StringBuilder();
         String[] words = line.split("\\|");

         for (String word : words)
         {
            if (word.startsWith("input")) {
               word = String.format("<u>%s</u>", word.substring("input ".length()));
            }
            else if (word.startsWith("button")) {
               word = String.format("[%s]", word.substring("button ".length()));
            }

            cells.append(String.format("<td>%s</td>", word));
         }

         rows.append(String.format("                <tr>%s</tr>\n", cells.toString()));
      }

      descriptionTable.append(String.format("" +
            "                <tr><td>\n" +
            "                   <table border='0' cellborder='0' cellspacing='0'>\n%s" +
            "                   </table>\n" +
            "                </td></tr>\n", rows.toString()));
      return descriptionTable;
   }

   private Collection toCollection(Object contentObj)
   {
      Collection content = new ArrayList();
      if (contentObj instanceof Collection) {
         content = (Collection) contentObj;
      }
      else if (contentObj != null) {
         content.add(contentObj);
      }
      return content;
   }

}
