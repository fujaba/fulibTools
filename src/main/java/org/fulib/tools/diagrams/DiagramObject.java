package org.fulib.tools.diagrams;

import java.util.Map;

public class DiagramObject
{
   private final String id;
   private final String name;
   private final String type;
   private final Map<String, Object> attributes;

   public DiagramObject(String id, String name, String type, Map<String, Object> attributes)
   {
      this.id = id;
      this.name = name;
      this.type = type;
      this.attributes = attributes;
   }

   public String getId()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }

   public String getType()
   {
      return type;
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      final DiagramObject that = (DiagramObject) o;

      return id.equals(that.id);
   }

   @Override
   public int hashCode()
   {
      return id.hashCode();
   }
}
