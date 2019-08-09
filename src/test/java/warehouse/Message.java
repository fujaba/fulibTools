package warehouse;

import java.util.ArrayList;

public class Message
{
   private String id;

   public String getId()
   {
      return id;
   }

   public Message setId(String id)
   {
      this.id = id;
      return this;
   }

   private String time;

   public String getTime()
   {
      return time;
   }

   public Message setTime(String time)
   {
      this.time = time;
      return this;
   }

   private String description;

   public String getDescription()
   {
      return description;
   }

   public Message setDescription(String description)
   {
      this.description = description;
      return this;
   }

   private ArrayList<WarehouseState> targets = new ArrayList<>();

   public ArrayList<WarehouseState> getTargets()
   {
      return targets;
   }
}
