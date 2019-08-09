package warehouse;

import java.util.ArrayList;

public class WarehouseService
{
   private String id;

   public void setId(String id)
   {
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

   private String description;

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   private ArrayList<WarehouseState> states = new ArrayList<>();

   public ArrayList<WarehouseState> getStates()
   {
      return states;
   }
}
