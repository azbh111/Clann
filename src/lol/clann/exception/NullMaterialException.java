
package lol.clann.exception;


public class NullMaterialException extends NullPointerException{
    /**
     * Constructs a {@code NullPointerException} with no detail message.
     */
    public NullMaterialException() {
        super();
    }

    /**
     * Constructs a {@code NullPointerException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public NullMaterialException(String s) {
        super(s);
    }
}
