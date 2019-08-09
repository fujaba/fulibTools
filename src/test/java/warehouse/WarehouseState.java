package warehouse;

import java.util.ArrayList;

public class WarehouseState
{
   private String id;
   private String time;
   private String description;
   private ArrayList<Content> content = new ArrayList<>();

   private ArrayList<Message> sendMessages = new ArrayList<>();

   public ArrayList<Message> getSendMessages()
   {
      return sendMessages;
   }

   public String getTime()
   {
      return time;
   }

   public void setTime(String time)
   {
      this.time = time;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public String getDescription()
   {
      return description;
   }

   public ArrayList<Content> getContent()
   {
      return content;
   }
}
