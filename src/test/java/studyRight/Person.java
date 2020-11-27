package studyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;

public class Person
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_LUCKY_NUMBERS = "luckyNumbers";
   private String name;
   private Set<Integer> luckyNumbers;
   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public Person setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public Set<Integer> getLuckyNumbers()
   {
      return this.luckyNumbers != null ? Collections.unmodifiableSet(this.luckyNumbers) : Collections.emptySet();
   }

   public Person withLuckyNumbers(Integer value)
   {
      if (this.luckyNumbers == null)
      {
         this.luckyNumbers = new LinkedHashSet<>();
      }
      if (this.luckyNumbers.add(value))
      {
         this.firePropertyChange(PROPERTY_LUCKY_NUMBERS, null, value);
      }
      return this;
   }

   public Person withLuckyNumbers(Integer... value)
   {
      for (final Integer item : value)
      {
         this.withLuckyNumbers(item);
      }
      return this;
   }

   public Person withLuckyNumbers(Collection<? extends Integer> value)
   {
      for (final Integer item : value)
      {
         this.withLuckyNumbers(item);
      }
      return this;
   }

   public Person withoutLuckyNumbers(Integer value)
   {
      if (this.luckyNumbers != null && this.luckyNumbers.removeAll(Collections.singleton(value)))
      {
         this.firePropertyChange(PROPERTY_LUCKY_NUMBERS, value, null);
      }
      return this;
   }

   public Person withoutLuckyNumbers(Integer... value)
   {
      for (final Integer item : value)
      {
         this.withoutLuckyNumbers(item);
      }
      return this;
   }

   public Person withoutLuckyNumbers(Collection<? extends Integer> value)
   {
      for (final Integer item : value)
      {
         this.withoutLuckyNumbers(item);
      }
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
}
