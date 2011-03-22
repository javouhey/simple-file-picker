package com.raverun.filebrowser;

import java.io.File;

import com.raverun.filebrowser.util.Either;
import com.raverun.filebrowser.util.IOUtilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {

    private Button button;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );

        button = (Button)findViewById( R.id.browse );
        button.setOnClickListener( new OnClickListener() {
            public void onClick( View v ) {
                Intent intent = new Intent( "com.raverun.action.PICK_FILE" );
                startActivityForResult( intent, 2 );
            }
        } );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == 2 ) {
            if( resultCode == RESULT_OK && data != null ) {
                Log.d( "Main", "onActivityResult" );
                Either<Boolean,File> either = IOUtilities.getFile( data.getData() );
                if( either.first ) {
                    Toast.makeText( 
                        this, 
                        "File " + either.second.getName() + " is selected", 
                        Toast.LENGTH_LONG ).show();
                }
            }
        }
    }
}