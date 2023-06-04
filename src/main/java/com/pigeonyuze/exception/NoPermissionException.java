package com.pigeonyuze.exception;

public class NoPermissionException extends Exception {

    public NoPermissionException(String explanation) {
        super(explanation);
    }

    /**
     * Constructs a new instance of NoPermissionException.
     * All fields are initialized to null.
     */
    public NoPermissionException() {
        super();
    }

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = -171450170595941775L;
}
