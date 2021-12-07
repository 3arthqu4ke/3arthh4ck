package me.earth.earthhack.api.command;

public class PossibleInputs
{
    private String completion;
    private String rest;

    public PossibleInputs(String completion, String rest)
    {
        this.completion = completion;
        this.rest = rest;
    }

    public PossibleInputs setCompletion(String completion)
    {
        this.completion = completion;
        return this;
    }

    public PossibleInputs setRest(String rest)
    {
        this.rest = rest;
        return this;
    }

    public String getFullText()
    {
        return completion + rest;
    }

    public String getCompletion()
    {
        return completion;
    }

    public String getRest()
    {
        return rest;
    }

    public static PossibleInputs empty()
    {
        return new PossibleInputs("", "");
    }

}
