package io.github.salehjg.pocketzotero;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.Vector;

public class Preparation {
    static final int ERR_BASE_STORAGE = 100;
    static final int ERR_BASE_PREFERENCES = 200;

    private AppMem appMem;
    private Context context;

    public Preparation(
            Application application
    ){
        appMem = (AppMem) application;
        context = application.getApplicationContext();
    }

    public int StartupSequence(){
        int state;

        state = CheckTheAppDirectories();
        if(state!=0) return state;

        state = CheckTheDatabases();
        if(state!=0) return state;

        state = CheckPreferencesSmb();
        if(state!=0) return state;


        return 0;
    }

    private int CheckPreferencesSmb(){
        String serverIp = appMem.getStorageSmbServerIp();
        String serverUser = appMem.getStorageSmbServerUsername();
        String serverPass = appMem.getStorageSmbServerPassword();

        if(serverUser.isEmpty()) return ERR_BASE_PREFERENCES;
        if(serverPass.isEmpty()) return ERR_BASE_PREFERENCES+1;
        if(serverIp.isEmpty()) return ERR_BASE_PREFERENCES+2;
        if(serverIp.split("\\.").length!=3) return ERR_BASE_PREFERENCES+3;

        return 0;
    }

    private int CheckTheDatabases(){
        boolean isLocal = appMem.getStorageModeIsLocal();
        if(isLocal){
            File localDb = new File(
                    getExternalStorage() + File.separator +
                            getResourceString(R.string.DirNameApp) + File.separator +
                            getResourceString(R.string.DirNameLocal) + File.separator +
                            getResourceString(R.string.DbFileNameLocal)
            );
            if(!localDb.exists()) return ERR_BASE_STORAGE + 5;
        } else {

        }
        return 0;
    }

    private int CheckTheAppDirectories(){
        if(!isStorageReadyLocal()) return ERR_BASE_STORAGE;
        File baseStorage = getExternalStorage();

        File appDir = new File(baseStorage, getResourceString(R.string.DirNameApp));
        if(!CreateDirectory(appDir)) return ERR_BASE_STORAGE+1;

        File localDbDir = new File(appDir, getResourceString(R.string.DirNameLocal));
        if(!CreateDirectory(localDbDir)) return ERR_BASE_STORAGE+2;

        File pendingDir = new File(appDir, getResourceString(R.string.DirNamePending));
        if(!CreateDirectory(pendingDir)) return ERR_BASE_STORAGE+3;

        File SmbDbDir = new File(appDir, getResourceString(R.string.DirNameSmb));
        if(!CreateDirectory(SmbDbDir)) return ERR_BASE_STORAGE+4;

        return 0;
    }

    private String getResourceString(int id){
        return context.getResources().getString(id);
    }

    private boolean isStorageReadyLocal(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);

    }

    private File getExternalStorage(){
        return Environment.getExternalStorageDirectory();
    }

    private boolean CreateDirectory(File baseDir, String dirName){
        File folder = new File(
                Environment.getExternalStorageDirectory() +
                File.separator + dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    private boolean CreateDirectory(String baseDir, String dirName){
        File folder = new File(new File(baseDir) + File.separator + dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    private boolean CreateDirectory(File dir){
        boolean retVal = true;
        if (!dir.exists()) {
            retVal = dir.mkdirs();
        }
        return retVal;
    }

    public static Vector<String> TranslateReturnCode(int retVal){
        String msgBase, msgDetailed = "";
        int baseError = retVal / 100;
        switch (baseError){
            case 0: {
                msgBase = "Others";
                break;
            }
            case ERR_BASE_STORAGE/100: {
                msgBase = "External Storage";
                break;
            }
            case ERR_BASE_PREFERENCES/100: {
                msgBase = "Preferences";
                break;
            }
            default:{
                msgBase = "Unknown Base.";
                break;
            }
        }

        if(baseError == ERR_BASE_STORAGE/100)
            switch (retVal%ERR_BASE_STORAGE){
                case 0: {
                    msgDetailed = "The external storage has invalid state (not mounted).";
                    break;
                }
                case 1: {
                    msgDetailed = "Failed to create the app directory.";
                    break;
                }
                case 2: {
                    msgDetailed = "Failed to create the app/local directory.";
                    break;
                }
                case 3: {
                    msgDetailed = "Failed to create the app/pending directory.";
                    break;
                }
                case 4: {
                    msgDetailed = "Failed to create the app/smb directory.";
                    break;
                }
                case 5: {
                    msgDetailed = "The operation mode is set to Local but the database does not exist at app/local.";
                    break;
                }
                default:{
                    msgDetailed = "Unknown Details.";
                    break;
                }
            }

        if(baseError == ERR_BASE_PREFERENCES/100)
            switch (retVal%ERR_BASE_PREFERENCES){
                case 0: {
                    msgDetailed = "Empty username for the SMB server.";
                    break;
                }
                case 1: {
                    msgDetailed = "Empty password for the SMB server.";
                    break;
                }
                case 2: {
                    msgDetailed = "Empty IP address for the SMB server.";
                    break;
                }
                case 3: {
                    msgDetailed = "Malformed IP address for the SMB server.";
                    break;
                }
                default:{
                    msgDetailed = "Unknown Details.";
                    break;
                }
            }


        Vector<String> vec = new Vector<String>();
        vec.add(msgBase);
        vec.add(msgDetailed);
        return vec;
    }
}
