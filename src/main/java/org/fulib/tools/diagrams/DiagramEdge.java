package org.fulib.tools.diagrams;

public class DiagramEdge
{
   private final String source;
   private final String target;
   private String sourceLabel;
   private String targetLabel;

   public DiagramEdge(String source, String target, String sourceLabel, String targetLabel)
   {
      this.source = source;
      this.target = target;
      this.sourceLabel = sourceLabel;
      this.targetLabel = targetLabel;
   }

   public String getSource()
   {
      return source;
   }

   public String getTarget()
   {
      return target;
   }

   public String getSourceLabel()
   {
      return sourceLabel;
   }

   public void setSourceLabel(String sourceLabel)
   {
      this.sourceLabel = sourceLabel;
   }

   public String getTargetLabel()
   {
      return targetLabel;
   }

   public void setTargetLabel(String targetLabel)
   {
      this.targetLabel = targetLabel;
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

      final DiagramEdge that = (DiagramEdge) o;

      if (source.equals(that.source) && target.equals(that.target))
      {
         return true;
      }

      return source.equals(that.target) && target.equals(that.source);
   }

   @Override
   public int hashCode()
   {
      return source.hashCode() + target.hashCode();
   }
}
