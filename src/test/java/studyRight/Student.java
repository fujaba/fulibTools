package studyRight;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;

public class Student extends Person
{
   public static final String PROPERTY_UNI = "uni";
   public static final String PROPERTY_PREDICATE = "predicate";
   public static final String PROPERTY_NOTES = "notes";

   private StudyRight uni;
   private Predicate<?> predicate;
   private List<String> notes;

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
      this.firePropertyChange(PROPERTY_UNI, oldValue, value);
      return this;
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
      this.firePropertyChange(PROPERTY_PREDICATE, oldValue, value);
      return this;
   }

   public List<String> getNotes()
   {
      return this.notes != null ? Collections.unmodifiableList(this.notes) : Collections.emptyList();
   }

   public Student withNotes(String value)
   {
      if (this.notes == null)
      {
         this.notes = new ArrayList<>();
      }
      if (this.notes.add(value))
      {
         this.firePropertyChange(PROPERTY_NOTES, null, value);
      }
      return this;
   }

   public Student withNotes(String... value)
   {
      for (final String item : value)
      {
         this.withNotes(item);
      }
      return this;
   }

   public Student withNotes(Collection<? extends String> value)
   {
      for (final String item : value)
      {
         this.withNotes(item);
      }
      return this;
   }

   public Student withoutNotes(String value)
   {
      if (this.notes != null && this.notes.removeAll(Collections.singleton(value)))
      {
         this.firePropertyChange(PROPERTY_NOTES, value, null);
      }
      return this;
   }

   public Student withoutNotes(String... value)
   {
      for (final String item : value)
      {
         this.withoutNotes(item);
      }
      return this;
   }

   public Student withoutNotes(Collection<? extends String> value)
   {
      for (final String item : value)
      {
         this.withoutNotes(item);
      }
      return this;
   }

   public void removeYou()
   {
      this.setUni(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getNotes());
      return result.toString();
   }
}
