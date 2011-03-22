/*
 * Copyright (C) 2011 Gavin Bong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on the file manager application by http://www.openintents.org/en/
 */

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
