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
package com.raverun.filebrowser.util;

public final class Constants {

    /**
     * Activity Action: Pick a file through the file manager.
     * Data is the current file name or file name suggestion.
     * Returns a new file name as file URI in data.
     *
     * <p>Constant Value: "com.raverun.action.PICK_FILE"</p>
     */
    public static final String ACTION_PICK_FILE = "com.raverun.action.PICK_FILE";

    public static final String EMPTY = "";

    public static final String DIR_ROOT = "/";

    public static final String KEY_CURRENT_DIRECTORY = "com.raverun.filebrowser.currentdir";
    public static final String KEY_STEPBACK = "com.raverun.filebrowser.stepback";
}
