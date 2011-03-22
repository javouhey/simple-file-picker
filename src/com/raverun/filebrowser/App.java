package com.raverun.filebrowser;

import android.app.Application;
import android.util.Log;

import com.raverun.filebrowser.model.Directory;
import com.raverun.filebrowser.model.ScanningException;
import com.raverun.filebrowser.util.Either;
import com.raverun.filebrowser.util.Nullable;
import com.raverun.filebrowser.util.ThreadSafe;

/**
 * Place to store mutable shared data
 *
 * @author Gavin Bong
 */
@ThreadSafe
public class App extends Application {

    public App() {
        Log.d( LOG_TAG, "constructor" );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _application = this;
        Log.d( LOG_TAG, "onCreate" );
    }

    @Nullable
    public synchronized Either<ScanningException,Directory> directoryContents() {
        return _directoryContents;
    }

    public synchronized void setDirectoryContents( Either<ScanningException,Directory> theContents ) {
        _directoryContents = theContents;
    }

    public static App instance()
    {
        return _application;
    }

    private Either<ScanningException,Directory> _directoryContents;

    private static App _application;
    private String LOG_TAG = "App";
}
