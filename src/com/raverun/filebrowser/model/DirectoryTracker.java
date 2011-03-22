package com.raverun.filebrowser.model;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import com.raverun.filebrowser.util.ThreadSafe;

/**
 * Centralize state tracking of directory navigation
 *
 * @author Gavin Bong
 */
@ThreadSafe
public class DirectoryTracker {

    public static DirectoryTracker of( int stepsBack, File directory ) {
        return new DirectoryTracker( stepsBack, directory );
    }

    private DirectoryTracker( int stepsBack, File directory ) {
        _stepsBack = stepsBack;
        _currentDirectory = directory;
        Log.i( TAG, "ctor: " + this.toString() );
    }

    public void deserializeFrom( int s, File directory ) {
        // only perform this action once (per instance)
        if( ! onetime.compareAndSet( false, true ) )
            return;

        synchronized( _lock ) {
            _stepsBack = s;
            _currentDirectory = directory;
            Log.i( TAG, "init: " + this.toString() );
        }
    }

    public void up() {
        synchronized( _lock ) {
            if( _stepsBack > 0 ) {
                _stepsBack--;
    
                if( _currentDirectory.getParent() != null )
                    _currentDirectory = _currentDirectory.getParentFile();
            }
        }
        Log.i( TAG, "up: " + this.toString() );
    }

    public final int stepsBack() {
        synchronized( _lock ) {
            return _stepsBack;
        }
    }

    public final File currentDirectory() {
        synchronized( _lock ) {
            return _currentDirectory;
        }
    }

    public void down( File newDirectory ) {
        if( newDirectory == null || !newDirectory.isDirectory() )
            return;

        synchronized( _lock ) {
            _stepsBack++;
            _currentDirectory = newDirectory;
        }
        Log.i( TAG, "up: " + this.toString() );
    }

    public String toString() {
        final int copyStepsBack;
        final File copyCurrentDirectory;

        synchronized( _lock ) {
            copyStepsBack = _stepsBack;
            copyCurrentDirectory = _currentDirectory;
        }

        StringBuilder sb = new StringBuilder();
        sb.append( "[stepsBack=" ).append( copyStepsBack );
        sb.append( " | " );
        sb.append( "currentDir=" )
          .append( (copyCurrentDirectory != null) ? copyCurrentDirectory.getAbsolutePath() : "null" );
        sb.append( "]" );
        return sb.toString();
    }

    private int _stepsBack;
    private File _currentDirectory;

    private Object _lock = new Object();

    private final AtomicBoolean onetime = new AtomicBoolean( false );
    private final String TAG = "DirectoryTracker";
}
