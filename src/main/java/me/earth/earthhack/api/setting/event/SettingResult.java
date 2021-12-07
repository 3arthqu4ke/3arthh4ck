package me.earth.earthhack.api.setting.event;

public class SettingResult
{
    public static final SettingResult SUCCESSFUL = new SettingResult(true, "");

    private final boolean successful;
    private final String message;

    public SettingResult(boolean successful, String message)
    {
        this.successful = successful;
        this.message = message;
    }

    public boolean wasSuccessful()
    {
        return successful;
    }

    public String getMessage()
    {
        return message;
    }

}
