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

import java.io.File;

import com.raverun.filebrowser.util.Constants;
import com.raverun.filebrowser.util.Either;
import com.raverun.filebrowser.util.IOUtilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity {

    private Button button;
    private EditText editText;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );

        button = (Button)findViewById( R.id.browse );
        button.setOnClickListener( new OnClickListener() {
            public void onClick( View v ) {
                Intent intent = new Intent( Constants.ACTION_PICK_FILE );
                intent.putExtra( Intent.EXTRA_TITLE, "Select bookmarks file" );
                startActivityForResult( intent, 2 );
            }
        } );

        editText = (EditText)findViewById( R.id.file_picked );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == 2 ) {
            if( resultCode == RESULT_OK && data != null ) {
                Log.d( "Main", "onActivityResult" );
                Either<Boolean,File> either = IOUtilities.getFile( data.getData() );
                if( either.first ) {
                    editText.setText( either.second.getAbsolutePath() );
                    Toast.makeText( 
                        this, 
                        "File " + either.second.getName() + " is selected", 
                        Toast.LENGTH_LONG ).show();
                }
            }
        }
    }
}