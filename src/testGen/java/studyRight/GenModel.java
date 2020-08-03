package studyRight;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.Clazz;

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
      });

      mm.associate(studyRight, "students", Type.MANY, student, "uni", Type.ONE);
   }
}
