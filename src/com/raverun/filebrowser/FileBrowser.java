package com.raverun.filebrowser;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raverun.filebrowser.model.Directory;
import com.raverun.filebrowser.model.Entry;
import com.raverun.filebrowser.model.ScanningException;
import com.raverun.filebrowser.util.Constants;
import com.raverun.filebrowser.util.Either;
import com.raverun.filebrowser.util.IOUtilities;

/**
 * @author Gavin Bong
 */
public class FileBrowser extends ListActivity {

    String _sdcardPath = Constants.EMPTY;
    File _currentDirectory = new File( Constants.EMPTY );
    FilesystemAdapter _listAdapter;
    DirectoryScanner _scanner;

    private Handler _handler;
    private int _stepsBack;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
        setContentView( R.layout.filelist );

        _emptyText = (TextView) findViewById( R.id.empty_text );
        _progressBar = (ProgressBar) findViewById( R.id.scan_progress );
        _handler = new Handler() {
            @Override
            public void handleMessage( Message msg ) {
                FileBrowser.this._emptyText.setVisibility( View.VISIBLE );
                FileBrowser.this._listAdapter.reload();
                Log.d( LOG_TAG, "message handled . what=" + msg.what );
            }
        };

        getListView().setEmptyView( findViewById(R.id.empty) );
        getListView().setTextFilterEnabled( true );
        getListView().requestFocus();
        getListView().requestFocusFromTouch();

    // ----- initialize the external storage absolute path -----
        ExternalStorageStatus exstate = obtainStatus();
        if( !exstate.available() || !exstate.writable() ) {
            Log.d( LOG_TAG, "onCreate - sdcard not available or writable" );
            Toast.makeText( this, "No writable sd-card mounted", Toast.LENGTH_LONG ).show();
            finishActivityFromChild( this, 2 );
            return;
        } else {
            _sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.d( LOG_TAG, "onCreate - _sdcardPath = " + _sdcardPath );
        }

        _stepsBack = 0;

    // ---- Decide target directory  ----
        File targetDir = new File( Constants.DIR_ROOT );
        if( !IOUtilities.isEmpty( _sdcardPath ) )
            targetDir = new File(_sdcardPath);

        Either<Boolean, File> fileParam  = IOUtilities.getFile( getIntent().getData() );
        if( fileParam.first ) {
            Either<Boolean, File> parent = IOUtilities.getPathPrefixFor( fileParam.second );
            if( parent.first && parent.second.isDirectory() ) {
                Log.d( LOG_TAG, "****** onCreate *****" );
                targetDir = parent.second;
            }
        }

    // Don't show the "folder empty" text since we're scanning.
        _emptyText.setVisibility( View.GONE );

    // Also DON'T show the progress bar - it's kind of lame to show that
    // for less than a second.
        _progressBar.setVisibility( View.GONE );

    // ----- restore saved data -----
        if( savedInstanceState != null ) {
            targetDir = new File( savedInstanceState.getString( Constants.KEY_CURRENT_DIRECTORY ) );
            _stepsBack = savedInstanceState.getInt( Constants.KEY_STEPBACK );
            Log.d( LOG_TAG, "Restoring1 " + savedInstanceState.getString( Constants.KEY_CURRENT_DIRECTORY ) );
            Log.d( LOG_TAG, "Restoring2 " + savedInstanceState.getInt( Constants.KEY_STEPBACK ) );
        }

        _listAdapter = new FilesystemAdapter( this );
        setListAdapter( _listAdapter );

        Log.d( LOG_TAG, "onCreate - targetDir = " + targetDir.getAbsolutePath() );
        openOrSelect( targetDir );
    }

    private String _dialogFileSize = Constants.EMPTY;
    private String _dialogFileName = Constants.EMPTY;
    private File _dialogFile = null;
    private static final int DIALOG_PICK_FILE = 2;

    @Override
    protected Dialog onCreateDialog( int id ) {
        switch( id ) {
        case DIALOG_PICK_FILE:
            return new AlertDialog.Builder( FileBrowser.this )
                .setIcon( R.drawable.alert_dialog_icon )
                .setTitle( "Pick this file ?" )
                .setMessage( _dialogFileName + " (" + _dialogFileSize + ")" )
                .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int whichButton ) {
                        Intent intent = getIntent();
                        intent.setData( IOUtilities.getUri( _dialogFile ) );
                        setResult( RESULT_OK, intent );
                        dialog.dismiss();
                        FileBrowser.this.finish();
                        Log.d( LOG_TAG, "finishing dialog" );
                    }
                })
                .setNegativeButton( "No", new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int whichButton ) {
                        dialog.dismiss();
                    }
                })
                .create();
        }
        return null;
    }

    /**
     * Browse to some location by clicking on a list item.
     * @param aFile
     */
    private final void openOrSelect( final File aFile ) {
        if( aFile.isDirectory() ) {
            if( ! aFile.equals( _currentDirectory ) ) {
//                mPreviousDirectory = _currentDirectory;
                _currentDirectory = aFile;
                rescan( aFile );
            }
        } else {
            final String fileSize = displayedFileSize( this, aFile );
            _dialogFileSize = fileSize;
            _dialogFile = aFile;
            _dialogFileName = aFile.getName();
            showDialog( DIALOG_PICK_FILE );
//            Toast.makeText( this, "File " + aFile.getName() + " is selected", Toast.LENGTH_LONG ).show();
        }
    }

    private String displayedFileSize( Context context, File aFile ) {
        final long fileSize = aFile.length();
        return Formatter.formatFileSize( context, fileSize );
    }

    private final void rescan( final File aFile ) {
        _scanner = (DirectoryScanner)new DirectoryScanner().execute( aFile );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onCancelScanning();
        Log.d( LOG_TAG, "onDestroy" );
    }

    private void onCancelScanning() {
        if( _scanner != null && _scanner.getStatus() == AsyncTask.Status.RUNNING ) {
            _scanner.cancel(true);
            _scanner = null;
            Log.d( LOG_TAG, "Cancelled directory scanner task" );
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );
        Log.d( LOG_TAG, "Saving: _stepsBack=" + _stepsBack );
        outState.putInt( Constants.KEY_STEPBACK, _stepsBack );
        Log.d( LOG_TAG, "Saving: _currentDirectory=" + _currentDirectory.getAbsolutePath() );
        outState.putString( Constants.KEY_CURRENT_DIRECTORY, _currentDirectory.getAbsolutePath() );
    }

    private class DirectoryScanner extends AsyncTask<File,Void,Void> {
        private final Object _lock = new Object(); 
        private File _targetDirectory;

        @Override
        protected void onPreExecute() {
            Log.d( LOG_TAG, "onPreExecute" );
            super.onPreExecute();
            FileBrowser.this.setProgressBarIndeterminateVisibility( true );
            _progressBar.setVisibility( View.VISIBLE );
        }

        @Override
        protected void onPostExecute( Void result ) {
            Log.d( LOG_TAG, "onPostExecute" );
            super.onPostExecute(result);
            _progressBar.setVisibility( View.GONE );
            FileBrowser.this.setProgressBarIndeterminateVisibility( false );
        }

        @Override
        protected Void doInBackground( File... directory ) {
            if( directory[0] == null )
                return null;

            synchronized( _lock ) {
                _targetDirectory = directory[0];
            }

            Log.d( LOG_TAG, "Scanning directory .. " + _targetDirectory.getName() + " | isDirectory=" + _targetDirectory.isDirectory() );

            if( isCancelled() )
                return null;

            try {
                File[] files = _targetDirectory.listFiles();
                if( files == null ) {
                    Log.d( LOG_TAG, "null return value from File#listFiles for -> " + _targetDirectory.getName() );
                    if( _stepsBack > 0 )
                        _stepsBack--;
                    return null;
                }

                Directory.Builder builder = new Directory.Builder();
                for( File f : files ) {
                    builder = builder.add( f );
                 //   Log.d( LOG_TAG, "Added " + f.getName() );

                 // test: simulate large directory
                 //   Thread.sleep( 10 ); 
                    if( isCancelled() )
                        return null;
                }
                App.instance().setDirectoryContents(
                    new Either<ScanningException,Directory>( null, builder.build() ) 
                );

            } catch( Exception e ) {
                Log.e( LOG_TAG, "scanning failed", e );
                App.instance().setDirectoryContents(
                    Either.of( new ScanningException( e ), new Directory() ) 
                );
            }

            Message msg = FileBrowser.this._handler.obtainMessage( 999 );
            msg.sendToTarget();

            return null;
        }

        @SuppressWarnings("unused")
        public File getTargetDirectory() {
            synchronized (_lock) {
                return _targetDirectory;
            }
        }
    }

    

    @Override
    protected void onListItemClick( ListView l, View v, int position, long id ) {
        super.onListItemClick(l, v, position, id);

        Log.d( LOG_TAG, "position = " + position + " | currentDirectory=" + _currentDirectory.getAbsolutePath() );
        Entry entry = (Entry)getListAdapter().getItem(position);

        File clicked = IOUtilities.getFile( _currentDirectory.getAbsolutePath(), entry.name() );
        if( clicked != null ) {
            if( clicked.isDirectory() ) {
                _stepsBack++;
                Log.d( LOG_TAG, "Incremented _stepsBack to " + _stepsBack );
                vibrate();
            }
            openOrSelect( clicked );
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator)getSystemService( Context.VIBRATOR_SERVICE );
        vibrator.vibrate( 130 );
    }

    @Override
    public void onBackPressed() {
        if( _stepsBack == 0 ) {
            Log.d( LOG_TAG, "default onBackPressed" );
            super.onBackPressed();
            return;
        }

        if( _stepsBack > 0 )
            upOneLevel();
    }

    /**
     * This function browses up one level
     * according to the field: {@code _currentDirectory}
     */
    private void upOneLevel(){
        if( _stepsBack > 0 )
            _stepsBack--;

        if( _currentDirectory.getParent() != null )
            openOrSelect( _currentDirectory.getParentFile());
    }

    private interface ExternalStorageStatus {
        boolean writable();
        boolean available();
    }

    private final ExternalStorageStatus obtainStatus() {
        String state = Environment.getExternalStorageState();
        if( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return new ExternalStorageStatus() {
                public boolean available() { return true; }
                public boolean writable() { return true; }
            };
        } else if( Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return new ExternalStorageStatus() {
                public boolean available() { return true; }
                public boolean writable() { return false; }
            };
        } else {
            return new ExternalStorageStatus() {
                public boolean available() { return false; }
                public boolean writable() { return false; }
            };
        }
    }

    private TextView _emptyText;
    private ProgressBar _progressBar;

    private String LOG_TAG = "FileBrowser";
}
