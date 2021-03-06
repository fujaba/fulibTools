package org.fulib.tools;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @deprecated since 1.2; use {@code HtmlRenderer} provided by FulibTables.
 */
@Deprecated
public class Tables
{
   /**
    * @deprecated since 1.2; use {@code HtmlRenderer} provided by FulibTables.
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   public String toHtml(Object table)
   {
      Objects.requireNonNull(table);

      StringBuilder buf = new StringBuilder();

      final Class<?> clazz = table.getClass();
      try
      {
         final Method getTable = clazz.getMethod("getTable");
         final Method getColumnMap = clazz.getMethod("getColumnMap");
         final List<? extends List<?>> baseTable = (List<? extends List<?>>) getTable.invoke(table);
         final Map<String, Integer> columnMap = (Map<String, Integer>) getColumnMap.invoke(table);

         this.genHeader(buf, columnMap);

         for (final List<?> row : baseTable)
         {
            this.genRow(buf, row);
         }
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException(e);
      }

      return buf.toString();
   }

   private void genHeader(StringBuilder buf, Map<String, Integer> columnMap)
   {
      buf.append("<table>\n");
      buf.append("<tr>");

      for (String key : columnMap.keySet())
      {
         buf.append("<th>");
         buf.append(key);
         buf.append("</th>");
      }

      buf.append("</tr>\n");
      buf.append("</table>\n");
   }

   private void genRow(StringBuilder buf, List<?> row)
   {
      buf.append("<tr>");

      for (Object value : row)
      {
         buf.append("<td>");
         buf.append(value);
         buf.append("</td>");
      }

      buf.append("</tr>\n");
   }
}
