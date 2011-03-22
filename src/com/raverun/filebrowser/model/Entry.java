package com.raverun.filebrowser.model;

/**
 * Represents an object in a file system.
 * <p>
 * <b>Design note:</b> the {@link #type()} returns an integer
 * because java enums are not efficient in Android. 
 *
 * @author Gavin Bong
 */
public interface Entry {

    /**
     * Display name for the file or directory, without the path
     *
     * @return the filename
     */
    String name();

    /**
     * @return 0=directory, 1=file
     */
    int type();
}