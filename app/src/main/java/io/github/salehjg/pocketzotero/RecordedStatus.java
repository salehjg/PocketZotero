package io.github.salehjg.pocketzotero;

import androidx.annotation.NonNull;

public class RecordedStatus {
    static final int STATUS_BASE_STORAGE = 100;
    static final int STATUS_BASE_PREFERENCES = 200;
    static final int STATUS_BASE_PERMISSIONS = 300;

    private String mBaseErrorMessage, mDetailedErrorMessage;
    private int mBaseErrorCode, mDetailedErrorCode, mErrorCode;

    public RecordedStatus(int code){
        mBaseErrorMessage = mDetailedErrorMessage = "";
        mErrorCode = mBaseErrorCode = mDetailedErrorCode = -1;
        TranslateReturnCode(code);
    }

    public RecordedStatus(String customStatusMessage){
        mBaseErrorMessage = "";
        mDetailedErrorMessage = customStatusMessage;
        mErrorCode = mBaseErrorCode = mDetailedErrorCode = -1;
    }

    public RecordedStatus(RecordedStatus recordedStatus){
        mBaseErrorMessage = recordedStatus.getBaseErrorMessage();
        mDetailedErrorMessage = recordedStatus.getDetailedErrorMessage();
        mErrorCode = recordedStatus.getErrorCode();
        mBaseErrorCode = recordedStatus.getBaseErrorCode();
        mDetailedErrorCode = recordedStatus.getDetailedErrorCode();
    }

    public String getBaseErrorMessage() {
        return mBaseErrorMessage;
    }

    public void setBaseErrorMessage(String mBaseErrorMessage) {
        this.mBaseErrorMessage = mBaseErrorMessage;
    }

    public String getDetailedErrorMessage() {
        return mDetailedErrorMessage;
    }

    public void setDetailedErrorMessage(String mDetailedErrorMessage) {
        this.mDetailedErrorMessage = mDetailedErrorMessage;
    }

    public int getBaseErrorCode() {
        return mBaseErrorCode;
    }

    public void setBaseErrorCode(int mBaseErrorCode) {
        this.mBaseErrorCode = mBaseErrorCode;
    }

    public int getDetailedErrorCode() {
        return mDetailedErrorCode;
    }

    public void setDetailedErrorCode(int mDetailedErrorCode) {
        this.mDetailedErrorCode = mDetailedErrorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    private void TranslateReturnCode(int retVal){
        String msgBase, msgDetailed = "STATUS_OK";
        int baseError = retVal / 100;
        switch (baseError){
            case 0: {
                msgBase = "Others";
                break;
            }
            case STATUS_BASE_STORAGE /100: {
                msgBase = "External Storage";
                break;
            }
            case STATUS_BASE_PREFERENCES /100: {
                msgBase = "Preferences";
                break;
            }
            default:{
                msgBase = "Unknown Base.";
                break;
            }
        }

        if(baseError == STATUS_BASE_STORAGE /100)
            switch (retVal% STATUS_BASE_STORAGE){
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
                case 6: {
                    msgDetailed = "Failed to rename `DbFileNameSmbTemp` to `DbFileNameSmb`.";
                    break;
                }
                case 7: {
                    msgDetailed = "Failed to delete the old `DbFileNameSmb`.";
                    break;
                }
                case 8: {
                    msgDetailed = "Failed to find the newly downloaded `DbFileNameSmbTemp`.";
                    break;
                }
                case 9: {
                    msgDetailed = "Loaded the old cached database. The attachments will not be accessible.";
                    break;
                }
                case 10: {
                    msgDetailed = "Loaded the downloaded SMB database.";
                    break;
                }
                case 11: {
                    msgDetailed = "Loaded the local database.";
                    break;
                }
                default:{
                    msgDetailed = "Unknown Details.";
                    break;
                }
            }

        if(baseError == STATUS_BASE_PREFERENCES /100)
            switch (retVal% STATUS_BASE_PREFERENCES){
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
        if(baseError == STATUS_BASE_PERMISSIONS /100)
            switch (retVal% STATUS_BASE_PREFERENCES){
                case 0: {
                    msgDetailed = "Need ALL FILES permission (ANDROID 11).";
                    break;
                }
                default:{
                    msgDetailed = "Unknown Details.";
                    break;
                }
            }

        this.mErrorCode = retVal;
        this.mBaseErrorCode = baseError;
        this.mDetailedErrorCode = retVal - baseError;

        this.mBaseErrorMessage = msgBase;
        this.mDetailedErrorMessage = msgDetailed;
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + this.mBaseErrorMessage + "(" + this.mBaseErrorCode+"-"+this.mDetailedErrorCode+")]: " + this.mDetailedErrorMessage;
    }
}
