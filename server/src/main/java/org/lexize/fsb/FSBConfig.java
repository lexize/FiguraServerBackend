package org.lexize.fsb;

import com.google.gson.annotations.SerializedName;

public class FSBConfig {
    @SerializedName("database_connection_string")
    private String databaseConnectionString;
    @SerializedName("database_login")
    private String databaseLogin;
    @SerializedName("database_password")
    private String databasePassword;
    @SerializedName("database_table_prefix")
    private String databaseTablePrefix;

    @SerializedName("avatar_upload_and_download")
    private boolean avatarUploadAndDownload;
    @SerializedName("max_avatar_size")
    private int maxAvatarSize;

    @SerializedName("pings")
    private boolean pings;
    @SerializedName("pings_rate_limit")
    private int pingsRateLimit;
    @SerializedName("pings_size_limit")
    private int pingsSizeLimit;

    public String getDatabaseConnectionString() {
        return databaseConnectionString;
    }

    public String getDatabaseLogin() {
        return databaseLogin;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getDatabaseTablePrefix() {
        return databaseTablePrefix;
    }

    public boolean isAvatarUploadAndDownload() {
        return avatarUploadAndDownload;
    }

    public int getMaxAvatarSize() {
        return maxAvatarSize;
    }

    public boolean isPings() {
        return pings;
    }

    public int getPingsRateLimit() {
        return pingsRateLimit;
    }

    public int getPingsSizeLimit() {
        return pingsSizeLimit;
    }
}
