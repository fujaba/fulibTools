package org.fulib.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Tables
{
   public String toHtml(Object table)
   {
      Objects.requireNonNull(table);

      StringBuilder buf = new StringBuilder();

      Class clazz = table.getClass();
      try
      {
         Method getTable = clazz.getMethod("getTable");
         Method getColumnMap = clazz.getMethod("getColumnMap");
         Object object = getTable.invoke(table);
         ArrayList<ArrayList<Object>> baseTable = (ArrayList<ArrayList<Object>>) object;
         object = getColumnMap.invoke(table);
         LinkedHashMap<String, Integer> columnMap = (LinkedHashMap<String, Integer>) object;

         genHeader(buf, columnMap, baseTable.get(0));

         for (ArrayList<Object> row : baseTable)
         {
            genRow(buf, row);
         }
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException(e);
      }

      return buf.toString();
   }

   private void genHeader(StringBuilder buf, LinkedHashMap<String, Integer> columnMap, ArrayList<Object> row)
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


   private void genRow(StringBuilder buf, ArrayList<Object> row)
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