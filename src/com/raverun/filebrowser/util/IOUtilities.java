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
package com.raverun.filebrowser.util;

import java.io.File;

import android.net.Uri;
import android.util.Log;

public abstract class IOUtilities {

    public static Uri getUri( File file ) {
        if( file != null )
            return Uri.fromFile( file );

        return null;
    }

    public static File getFile( String curdir, String file ) {
        String separator = "/";
        if( curdir.endsWith( "/" ) )
            separator = Constants.EMPTY;

        File clickedFile = new File( curdir + separator + file );
        return clickedFile;
    }

    public static Either<Boolean, File> getFile( Uri uri ) {
        if( uri != null ) {
            String filepath = uri.getPath();
            Log.d( LOG_TAG, "getFile() uri.getPath = " + filepath );
            Log.d( LOG_TAG, "getFile() uri.getLastPathSegment = " + uri.getLastPathSegment() );
            if( filepath != null )
                return new Either<Boolean, File>( true, new File(filepath) );
        }
        return new Either<Boolean, File>( false, null );
    }

    public static Either<Boolean, File> getPathPrefixFor( final File file ) {
        if( file != null ) {
            if( file.isDirectory() )
                return new Either<Boolean, File>( true, file );

            String filename = file.getName();
            String filepath = file.getAbsolutePath();

            // Construct path without file name.
            String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
            if( pathwithoutname.endsWith( "/" ) ) {
                pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
            }
            return new Either<Boolean, File>( true, new File(pathwithoutname) );
        }
        return new Either<Boolean, File>( false, null );
    }

    public static boolean isEmpty( String in ) {
        return( in == null || in.trim().length() == 0 );
    }

    private static String LOG_TAG = "IOUtilities";
}
