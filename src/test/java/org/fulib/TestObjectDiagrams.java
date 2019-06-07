package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestObjectDiagrams
{
   @Test
   public void testObjectDiagrams() throws IOException, URISyntaxException
   {
      URL url = ClassLoader.getSystemResource("initStudentsSubscribeToModeling.yaml");
      Path initData = Paths.get(url.toURI());
      byte[] bytes = Files.readAllBytes(initData);
      String yamlString = new String(bytes);

      YamlIdMap idMap = new YamlIdMap();

      Object root = idMap.decode(yamlString);

      YamlObject alice = (YamlObject) root;

      FulibTools.objectDiagrams().dumpPng(root);

      FulibTools.objectDiagrams().dumpYaml("tmp/tmpStudis.yaml", root);

      System.out.println(alice);
   }


}
