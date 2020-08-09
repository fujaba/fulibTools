package org.fulib.tools.pipe;

/**
 * A pipe can transform the input text of a code fragment to a different output format.
 * <p>
 * This is a {@linkplain FunctionalInterface functional interface} and can be implemented using lambda expressions.
 *
 * @see org.fulib.tools.CodeFragments
 * @since 1.2
 */
@FunctionalInterface
public interface Pipe
{
   /**
    * Transforms the input text of the code fragment to the output format specified by this pipe.
    *
    * @param content
    *    the input text
    * @param arg
    *    the pipe argument of the form {@code <pipeName>} or {@code <pipeName>:<args>}
    *
    * @return the transformed text
    */
   String apply(String content, String arg);
}
