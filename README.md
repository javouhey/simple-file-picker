# Summary

This project provides an embeddable **sdcard** browser with the purpose
of picking a file. It doesn't support directory picking. Currently the browser is rooted at `/sdcard` (i.e. user cannot browse to `/`)

__@last-updated:__ 2011 april 12th 15h15 GMT+8


# External dependencies & inspiration

This software uses the following opensource artifacts:

* Icons at [devianart](http://cloudif.deviantart.com/art/Android-Developer-Icons-138072676)
 
* The class `Pair.java` from [Google Web Toolkit](http://code.google.com/intl/fr/webtoolkit/terms.html "GWT license")

* The code is based on [OpenIntent's filemanager](http://www.openintents.org/en/)


# How to use 

From your Activity, you can request for the file browser by broadcasting this intent:

        import com.raverun.filebrowser.util.Constants;
            
        Intent intent = new Intent( Constants.ACTION_PICK_FILE );
        intent.putExtra( Intent.EXTRA_TITLE, "Select bookmarks file" );
        startActivityForResult( intent, 2 );

Then in your `Activity#onActivityResult`, you will receive the an instance of `java.io.File` that refers to the selected file. Please consult the code in `com.raverun.filebrowser.Main`.

# History

* First release 2011 march 22
