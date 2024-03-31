package org.lexize.fsb;

import com.google.gson.annotations.SerializedName;

public class FSBConfig {
    @SerializedName("database_connection_string")
    private String databaseConnectionString = "jdbc:sqlite:fsb_database.db";
    @SerializedName("database_login")
    private String databaseLogin = null;
    @SerializedName("database_password")
    private String databasePassword = null;
    @SerializedName("database_table_prefix")
    private String databaseTablePrefix = "FSB_";

    @SerializedName("avatar_upload_and_download")
    private boolean avatarUploadAndDownload = true;
    @SerializedName("max_avatar_size")
    private int maxAvatarSize = 100000;
    @SerializedName("max_avatars")
    private int maxAvatars = 1;
    @SerializedName("avatar_size_limit_message")
    private String avatarSizeLimitMessage = "Avatar size can't be more than 100KB.";
    @SerializedName("avatar_count_limit_message")
    private String avatarCountLimitMessage = "You can't have more than 1 avatar.";
    @SerializedName("avatar_gc_ticks")
    private int avatarGCTicks = 5000;


    @SerializedName("pings")
    private boolean pings = true;
    @SerializedName("pings_rate_limit")
    private int pingsRateLimit = 20;
    @SerializedName("ping_rate_limit_message")
    private String pingRateLimitMessage = "You can't send more than 20 pings per second.";
    @SerializedName("pings_size_limit")
    private int pingsSizeLimit = 10000;
    @SerializedName("ping_size_limit_message")
    private String pingSizeLimitMessage = "You can't send more than 10KB of pings per second.";
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

    public int getMaxAvatars() {
        return maxAvatars;
    }

    public String getAvatarSizeLimitMessage() {
        return avatarSizeLimitMessage;
    }

    public String getAvatarCountLimitMessage() {
        return avatarCountLimitMessage;
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
