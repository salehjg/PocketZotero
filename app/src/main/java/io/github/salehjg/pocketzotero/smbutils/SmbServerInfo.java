package io.github.salehjg.pocketzotero.smbutils;

public class SmbServerInfo {
    private String mName, mUsername, mPassword, mIp;

    public SmbServerInfo(
            String name,
            String username,
            String password,
            String ip
    ){
        mName = name;
        mUsername = username;
        mPassword = password;
        mIp = ip;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String mIp) {
        this.mIp = mIp;
    }
}
