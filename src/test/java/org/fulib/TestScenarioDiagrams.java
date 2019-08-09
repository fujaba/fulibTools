package org.fulib;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.Test;
import warehouse.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestScenarioDiagrams
{
   @Test
   public void testManual() throws IOException
   {
      String diagramText = "";
      String diagramFileName = "tmp/scenario.svg";

      byte[] bytes = Files.readAllBytes(Paths.get("tmp/example.txt"));
      diagramText = new String(bytes);

      Graphviz.fromString(diagramText.toString()).render(Format.SVG).toFile(new File("tmp/scenario.svg"));
      assertThat(Files.exists(Paths.get(diagramFileName)), is(true));
   }



   @Test
   public void testGenerated()
   {
      WarehouseState firstDialog = new WarehouseState();
      firstDialog.setId("firstDialog");
      firstDialog.setTime("8:05");
      firstDialog.setDescription("" +
            "New Supply\n" +
            "input eu30\n" +
            "input Pumps\n" +
            "input 50\n" +
            "button OK\n");

      WarehouseState secondDialog = new WarehouseState();
      secondDialog.setId("dialog02");
      secondDialog.setTime("8:06");
      secondDialog.setDescription("" +
            "New Supply\n" +
            "input eu40\n" +
            "input Pumps\n" +
            "input 50\n" +
            "button OK\n");

      Content palTable = new Content()
            .setDescription(""+
                  "button eu30 | button del\n" +
                  "button eu40 | button del\n");

      WarehouseState atRampList03 = new WarehouseState();
      atRampList03.setId("atRampList03");
      atRampList03.setTime("8:06:02");
      atRampList03.setDescription("" +
            "Palettes at Ramp\n" );
      atRampList03.getContent().add(palTable);

      WarehouseService aliceApp = new WarehouseService();
      aliceApp.setId("aliceApp");
      aliceApp.setDescription("Fork Lift App 4 Alice");
      aliceApp.getStates().add(firstDialog);
      aliceApp.getStates().add(secondDialog);
      aliceApp.getStates().add(atRampList03);


      Message m1 = new Message()
            .setId("m1")
            .setTime("8:06")
            .setDescription("" +
                  "paletteAtRamp\n" +
                  "eu30  Pumps  50\n" +
                  "eu40  Pumps  50\n");

      secondDialog.getSendMessages().add(m1);


      WarehouseState eu30AtRamp = new WarehouseState();
      eu30AtRamp.setId("eu30AtRamp");
      eu30AtRamp.setTime("8:06:01");
      eu30AtRamp.setDescription("eu30 | Pumps | 50 | atRamp\neu40 | Pumps | 50 | atRamp");

      m1.getTargets().add(eu30AtRamp);

      WarehouseService backend = new WarehouseService();
      backend.setId("warehouse");
      backend.setDescription("Warehouse Backend Server");
      backend.getStates().add(eu30AtRamp);

      Message m2 = new Message()
            .setId("m2")
            .setTime("8:06:01")
            .setDescription("" +
                  "paletteAtRamp\n" +
                  "eu30  Pumps  50 server\n" +
                  "eu40  Pumps  50 server\n");

      eu30AtRamp.getSendMessages().add(m2);
      m2.getTargets().add(atRampList03);

      WarehouseScenario scenario = new WarehouseScenario();
      scenario.getServices().add(aliceApp);
      scenario.getServices().add(backend);

      String diagramFileName = "tmp/scenario-diagram.svg";
      FulibTools.scenarioDiagrams().dump(diagramFileName, scenario);
   }
}
