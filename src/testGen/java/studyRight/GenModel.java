package studyRight;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class GenModel implements ClassModelDecorator
{
   class StudyRight
   {
      String id;
      String description;

      @Link("uni")
      List<Student> students;
   }

   class Person
   {
      String name;

      Set<Integer> luckyNumbers;
   }

   class Student extends Person
   {
      Predicate<?> predicate;

      @Link("students")
      StudyRight uni;

      List<String> notes;
   }

   class Node
   {
      String id;

      @Link("children")
      Node parent;

      @Link("parent")
      List<Node> children;
   }

   @Override
   public void decorate(ClassModelManager mm)
   {
      mm.haveNestedClasses(GenModel.class);
   }
}
