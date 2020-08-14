package studyRight;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.function.Predicate;

public class Student
{

   public static final String PROPERTY_name = "name";

   private String name;

   public static final String PROPERTY_uni = "uni";

   private StudyRight uni;

   protected PropertyChangeSupport listeners;

   public static final String PROPERTY_predicate = "predicate";

   private Predicate<?> predicate;

   public String getName()
   {
      return this.name;
   }

   public Student setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_name, oldValue, value);
      return this;
   }

   public StudyRight getUni()
   {
      return this.uni;
   }

   public Student setUni(StudyRight value)
   {
      if (this.uni == value)
      {
         return this;
      }

      final StudyRight oldValue = this.uni;
      if (this.uni != null)
      {
         this.uni = null;
         oldValue.withoutStudents(this);
      }
      this.uni = value;
      if (value != null)
      {
         value.withStudents(this);
      }
      this.firePropertyChange(PROPERTY_uni, oldValue, value);
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setUni(null);
   }

   public Predicate<?> getPredicate()
   {
      return this.predicate;
   }

   public Student setPredicate(Predicate<?> value)
   {
      if (Objects.equals(value, this.predicate))
      {
         return this;
      }

      final Predicate<?> oldValue = this.predicate;
      this.predicate = value;
      this.firePropertyChange(PROPERTY_predicate, oldValue, value);
      return this;
   }
}
