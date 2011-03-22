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
package com.raverun.filebrowser;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.raverun.filebrowser.model.Directory;
import com.raverun.filebrowser.model.Entry;
import com.raverun.filebrowser.model.ScanningException;
import com.raverun.filebrowser.util.Either;

/**
 * @author Gavin Bong
 */
public class FilesystemAdapter extends BaseAdapter {

    private List<Entry> _directoryContents;
    private LayoutInflater _inflater;

    public FilesystemAdapter( Context context ) {
        _context = context;
        _inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        _directoryContents = new ArrayList<Entry>( 16 );
        //init0();
    }

    /**
     * Activity will invoke this method to indicate that the adapter should reload
     */
    public void reload() {
        Either<ScanningException,Directory> source = App.instance().directoryContents();
        if( source != null && source.first == null ) {
            Directory directory = source.second;
            _directoryContents.clear();
            _directoryContents.addAll( directory.contents() );
            notifyDataSetChanged();
            Log.i( LOG_TAG, "Reloaded" );
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public int getCount() {
        return _directoryContents.size();
    }

    public Object getItem( int position ) {
        return _directoryContents.get( position );
    }

    public long getItemId( int position ) {
        return position;
    }

    public View getView( int position, View convertView, ViewGroup parent ) {
        View rv;
        if( convertView == null )
            rv = _inflater.inflate( R.layout.file_item, parent, false );
        else
            rv = convertView;

        Entry aEntry = _directoryContents.get( position );

        ((TextView)rv.findViewById( R.id.item_name )).setText( aEntry.name() );
        Bitmap bitmap = null;
        switch( aEntry.type() )
        {
        default:
        case 0:
            bitmap = retrieveBitmap( R.drawable.directory );
            break;
        case 1:
            bitmap = retrieveBitmap( R.drawable.file );
            break;
        }
        ((ImageView)rv.findViewById(R.id.item_icon)).setImageBitmap( bitmap );

        return rv;
    }

    private Bitmap retrieveBitmap( int resId )
    {
        java.io.InputStream is = _context.getResources().openRawResource( resId );
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = 1;

        Bitmap bitmap = BitmapFactory.decodeStream( is, null, opts );
//        Log.i( LOG_TAG, opts.outHeight + " - h: " + opts.outWidth + " -w || " + opts.outMimeType );

        return bitmap;
    }

    @SuppressWarnings("unused")
    private void init0() {
        _directoryContents.add( new Entry() {
            public String name() {
                return "Action";
            }
            public int type() {
                return 0;
            }
        } );

        _directoryContents.add( new Entry() {
            public String name() {
                return "Romance";
            }
            public int type() {
                return 0;
            }
        } );

        _directoryContents.add( new Entry() {
            public String name() {
                return "The Adjustment Bureau";
            }
            public int type() {
                return 1;
            }
        } );

        _directoryContents.add( new Entry() {
            public String name() {
                return "Norwegian Wood";
            }
            public int type() {
                return 1;
            }
        } );
    }

    private final Context _context;
    private final static String LOG_TAG = "FilesystemAdapter";
}
