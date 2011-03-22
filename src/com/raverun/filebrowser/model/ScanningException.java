package com.raverun.filebrowser.model;

public class ScanningException extends RuntimeException {

    public ScanningException( Throwable throwable ) {
       super( throwable );
    }

    private static final long serialVersionUID = 1L;
}
