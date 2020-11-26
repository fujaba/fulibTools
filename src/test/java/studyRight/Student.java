package studyRight;

import java.util.Objects;
import java.util.function.Predicate;

public class Student extends Person
{
   public static final String PROPERTY_UNI = "uni";
   public static final String PROPERTY_PREDICATE = "predicate";

   private StudyRight uni;
   private Predicate<?> predicate;

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

   public void removeYou()
   {
      this.setUni(null);
   }
}
