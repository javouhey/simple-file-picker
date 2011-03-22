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
package com.raverun.filebrowser.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Gavin Bong
 */
public class Directory {
    public static class Builder {
        public Builder() {
            builderDirectories = new ArrayList<Entry>(8);
            builderFiles = new ArrayList<Entry>(8);
        }

        public Builder add( final File file ) {
            if( file != null ) {
                Entry e = new Entry() {
                    public String name() { return file.getName(); }
                    public int type() {
                        if( file.isDirectory() )
                            return 0;

                        return 1;
                    }
                };

                if( file.isDirectory() )
                    builderDirectories.add( e );

                if( file.isFile() )
                    builderFiles.add( e );
            }

            return this;
        }

        public Directory build() {
            Directory retval = new Directory();
            retval._directories.addAll( builderDirectories );
            retval._files.addAll( builderFiles );
            return retval;
        }

        private final List<Entry> builderDirectories;
        private final List<Entry> builderFiles;
    }

    public Directory() {
        _directories = new ArrayList<Entry>(8);
        _files = new ArrayList<Entry>(8);
    }

    public List<Entry> contents() {
        Collections.sort( _directories, new Comparator<Entry>() {
            public int compare( Entry object1, Entry object2 ) {
                return object1.name().compareToIgnoreCase( object2.name() );
            }
        } );
        Collections.sort( _files, new Comparator<Entry>() {
            public int compare( Entry object1, Entry object2 ) {
                return object1.name().compareToIgnoreCase( object2.name() );
            }
        } );

        List<Entry> retval = new ArrayList<Entry>(16);
        retval.addAll( _directories );
        retval.addAll( _files );
        return retval;
    }

    private List<Entry> _directories;
    private List<Entry> _files;
}
