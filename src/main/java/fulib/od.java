package fulib;

import org.fulib.FulibTools;

public class od
{
   public static String dump(Object... objects)
   {
      return FulibTools.objectDiagrams().dumpPng(objects);
   }
}
