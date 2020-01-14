package fulib;

import org.fulib.FulibTools;

/**
 * @deprecated since 1.2; use {@link FulibTools#objectDiagrams()} instead
 */
@Deprecated
public class od
{
   /**
    * @deprecated since 1.2; use {@link FulibTools#objectDiagrams()}.{@link
    * org.fulib.tools.ObjectDiagrams#dumpPng(Object...) dumpPng(objects...)} instead
    */
   @Deprecated
   public static String dump(Object... objects)
   {
      return FulibTools.objectDiagrams().dumpPng(objects);
   }
}
