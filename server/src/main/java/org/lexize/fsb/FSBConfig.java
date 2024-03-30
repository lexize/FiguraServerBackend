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
    @SerializedName("crash_if_db_errors")
    private boolean crashIfDBErrors = false;

    @SerializedName("avatar_upload_and_download")
    private boolean avatarUploadAndDownload;
    @SerializedName("max_avatar_size")
    private int maxAvatarSize;
    @SerializedName("avatar_size_limit_message")
    private String avatarSizeLimitMessage;
    @SerializedName("avatar_gc_ticks")
    private int avatarGCTicks = 5000;


    @SerializedName("pings")
    private boolean pings;
    @SerializedName("pings_rate_limit")
    private int pingsRateLimit;
    @SerializedName("ping_rate_limit_message")
    private String pingRateLimitMessage;
    @SerializedName("pings_size_limit")
    private int pingsSizeLimit;
    @SerializedName("ping_size_limit_message")
    private String pingSizeLimitMessage;
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

    public boolean avatarFeaturesAllowed() {
        return avatarUploadAndDownload;
    }

    public int getMaxAvatarSize() {
        return maxAvatarSize;
    }

    public boolean crashIfDBErrors() {
        return crashIfDBErrors;
    }

    public int getAvatarGCTicks() {
        return avatarGCTicks;
    }

    public boolean pingsAllowed() {
        return pings;
    }

    public int getPingsRateLimit() {
        return pingsRateLimit;
    }

    public int getPingsSizeLimit() {
        return pingsSizeLimit;
    }

    public String getPingRateLimitMessage() {
        return pingRateLimitMessage;
    }

    public String getPingSizeLimitMessage() {
        return pingSizeLimitMessage;
    }
}
