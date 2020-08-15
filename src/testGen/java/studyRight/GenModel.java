package studyRight;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.Clazz;

import java.util.function.Predicate;

public class GenModel implements ClassModelDecorator
{
   @Override
   public void decorate(ClassModelManager mm)
   {
      final Clazz studyRight = mm.haveClass("StudyRight", c -> {
         c.attribute("id", Type.STRING);
         c.attribute("description", Type.STRING);
      });

      final Clazz student = mm.haveClass("Student", c -> {
         c.attribute("name", Type.STRING);
         c.attribute("predicate", "Predicate<?>");
         c.imports(Predicate.class.getCanonicalName());
      });

      mm.associate(studyRight, "students", Type.MANY, student, "uni", Type.ONE);
   }
}
