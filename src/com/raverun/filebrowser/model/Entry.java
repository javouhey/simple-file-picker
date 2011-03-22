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