package studyRight;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class StudyRight
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_DESCRIPTION = "description";
   public static final String PROPERTY_STUDENTS = "students";
   public static final String PROPERTY_EMPLOYEES = "employees";

   private String id;
   private String description;
   private List<Student> students;

   protected PropertyChangeSupport listeners;
   private List<Person> employees;

   public String getId()
   {
      return this.id;
   }

   public StudyRight setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getDescription()
   {
      return this.description;
   }

   public StudyRight setDescription(String value)
   {
      if (Objects.equals(value, this.description))
      {
         return this;
      }

      final String oldValue = this.description;
      this.description = value;
      this.firePropertyChange(PROPERTY_DESCRIPTION, oldValue, value);
      return this;
   }

   public List<Student> getStudents()
   {
      return this.students != null ? Collections.unmodifiableList(this.students) : Collections.emptyList();
   }

   public StudyRight withStudents(Student value)
   {
      if (this.students == null)
      {
         this.students = new ArrayList<>();
      }
      if (!this.students.contains(value))
      {
         this.students.add(value);
         value.setUni(this);
         this.firePropertyChange(PROPERTY_STUDENTS, null, value);
      }
      return this;
   }

   public StudyRight withStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public StudyRight withStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public StudyRight withoutStudents(Student value)
   {
      if (this.students != null && this.students.remove(value))
      {
         value.setUni(null);
         this.firePropertyChange(PROPERTY_STUDENTS, value, null);
      }
      return this;
   }

   public StudyRight withoutStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public StudyRight withoutStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public List<Person> getEmployees()
   {
      return this.employees != null ? Collections.unmodifiableList(this.employees) : Collections.emptyList();
   }

   public StudyRight withEmployees(Person value)
   {
      if (this.employees == null)
      {
         this.employees = new ArrayList<>();
      }
      if (!this.employees.contains(value))
      {
         this.employees.add(value);
         this.firePropertyChange(PROPERTY_EMPLOYEES, null, value);
      }
      return this;
   }

   public StudyRight withEmployees(Person... value)
   {
      for (final Person item : value)
      {
         this.withEmployees(item);
      }
      return this;
   }

   public StudyRight withEmployees(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withEmployees(item);
      }
      return this;
   }

   public StudyRight withoutEmployees(Person value)
   {
      if (this.employees != null && this.employees.remove(value))
      {
         this.firePropertyChange(PROPERTY_EMPLOYEES, value, null);
      }
      return this;
   }

   public StudyRight withoutEmployees(Person... value)
   {
      for (final Person item : value)
      {
         this.withoutEmployees(item);
      }
      return this;
   }

   public StudyRight withoutEmployees(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withoutEmployees(item);
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

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getDescription());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutStudents(new ArrayList<>(this.getStudents()));
      this.withoutEmployees(new ArrayList<>(this.getEmployees()));
   }
}
