package io.github.salehjg.pocketzotero;

import android.content.Context;

import java.io.File;

public class AppDirs {
    public static File getPredefinedPrivateStorageBase(Context context){
        // getCacheDir : the data could be lost if the device is running low on storage.
        // getFilesDir : the data will only be lost if the user uninstalls the application.
        // getExternalFilesDir(null): the data will persist even after app's uninstallation.
        return context.getFilesDir();
    }

    public static File getPredefinedPrivateStorageLocalScoped(Context context){
        return new File(getPredefinedPrivateStorageBase(context), getResourceString(context, R.string.PrivateDirNameLocal));
    }

    public static File getPredefinedPrivateStorageSharedSmb(Context context){
        return new File(getPredefinedPrivateStorageBase(context), getResourceString(context, R.string.PrivateDirNameSmb));
    }

    public static File getPredefinedPrivateStoragePending(Context context){
        return new File(getPredefinedPrivateStorageBase(context), getResourceString(context, R.string.PrivateDirNamePending));
    }

    public static String getPredefinedPrivateStorageDirNameLocalScoped(Context context){
        return getResourceString(context, R.string.PrivateDirNameLocal);
    }

    public static String getPredefinedPrivateStorageDirNameSharedSmb(Context context){
        return getResourceString(context, R.string.PrivateDirNameSmb);
    }

    public static String getPredefinedPrivateStorageDirNamePending(Context context){
        return getResourceString(context, R.string.PrivateDirNamePending);
    }

    public static String getSharedSmbBase(Context context){
        AppMem appMem = ((AppMem)context.getApplicationContext());
        String dbPath = appMem.getStorageSmbServerSharedPath();
        return dbPath.substring(0, dbPath.lastIndexOf(File.separator));
    }

    private static String getResourceString(Context context, int id){
        return context.getResources().getString(id);
    }

    public static boolean makeDirAtPrivateBase(Context context, String dirName){
        File folder = new File(getPredefinedPrivateStorageBase(context), dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    public static boolean makeDirAtPrivateBase(Context context, String parentDir, String dirName){
        File folder = new File(getPredefinedPrivateStorageBase(context).getPath() + File.separator + parentDir + File.separator + dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    public static boolean deleteDirAndContentAtPrivateBase(Context context, String parentDir, String dirName) {
        File folder = new File(getPredefinedPrivateStorageBase(context).getPath() + File.separator + parentDir + File.separator + dirName);
        return _deleteDirAndContentAbsPath(folder);
    }

    private static boolean _deleteDirAndContentAbsPath(File folder){
        File[] files = folder.listFiles();
        boolean result = true;
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    result &= _deleteDirAndContentAbsPath(f);
                } else {
                    result &= f.delete();
                }
            }
        }
        result &= folder.delete();
        return result;
    }

    public static boolean makeDirAtPrivateBase(Context context, File dir){
        return makeDirAtPrivateBase(context, dir.getPath());
    }

}
