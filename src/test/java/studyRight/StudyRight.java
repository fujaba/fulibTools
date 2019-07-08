package studyRight;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class StudyRight  
{

   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public StudyRight setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }


   public static final java.util.ArrayList<Student> EMPTY_students = new java.util.ArrayList<Student>()
   { @Override public boolean add(Student value){ throw new UnsupportedOperationException("No direct add! Use xy.withStudents(obj)"); }};


   public static final String PROPERTY_students = "students";

   private java.util.ArrayList<Student> students = null;

   public java.util.ArrayList<Student> getStudents()
   {
      if (this.students == null)
      {
         return EMPTY_students;
      }

      return this.students;
   }

   public StudyRight withStudents(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withStudents(i);
            }
         }
         else if (item instanceof Student)
         {
            if (this.students == null)
            {
               this.students = new java.util.ArrayList<Student>();
            }
            if ( ! this.students.contains(item))
            {
               this.students.add((Student)item);
               ((Student)item).setUni(this);
               firePropertyChange("students", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public StudyRight withoutStudents(Object... value)
   {
      if (this.students == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutStudents(i);
            }
         }
         else if (item instanceof Student)
         {
            if (this.students.contains(item))
            {
               this.students.remove((Student)item);
               ((Student)item).setUni(null);
               firePropertyChange("students", item, null);
            }
         }
      }
      return this;
   }


   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public void removeYou()
   {
      this.withoutStudents(this.getStudents().clone());


   }


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getId());


      return result.substring(1);
   }

}