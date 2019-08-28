package pl.dg.batch.Exceptions;

public class FileNotReadableException extends Exception {

    /**
     * Custom exception for not readable files
     *
     * @param message
     */
    public FileNotReadableException(String message) {
        super(message);
    }
}
