package me.earth.earthhack.impl.modules.misc.announcer.util;

public enum AnnouncementType
{
    Distance()
    {
        @Override
        public String getDefaultMessage()
        {
            return "I just walked <NUMBER> Blocks!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Distance.txt";
        }
    },
    Mine()
    {
        @Override
        public String getDefaultMessage()
        {
            return "I just mined <NUMBER> <NAME>!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Mine.txt";
        }
    },
    Place()
    {
        @Override
        public String getDefaultMessage()
        {
            return "I just placed <NUMBER> <NAME>!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Place.txt";
        }
    },
    Eat()
    {
        @Override
        public String getDefaultMessage()
        {
            return "I just ate <NUMBER> <NAME>!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Eat.txt";
        }
    },
    Join()
    {
        @Override
        public String getDefaultMessage()
        {
            return "Hey <NAME>!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Join.txt";
        }
    },
    Leave()
    {
        @Override
        public String getDefaultMessage()
        {
            return "Bye <NAME>!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Leave.txt";
        }
    },
    Totems()
    {
        @Override
        public String getDefaultMessage()
        {
            return "EZZ <NUMBER> Pop <NAME>!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Totems.txt";
        }
    },
    Death()
    {
        @Override
        public String getDefaultMessage()
        {
            return "Good fight <NAME>!";
        }

        @Override
        public String getFile()
        {
            return PATH + "Announcer_Death.txt";
        }
    },
    Miss()
            {
        @Override
        public String getDefaultMessage() {
            return "Nice shot <NAME>, try hitting next time!";
        }

        @Override
        public String getFile() {
            return PATH + "Announcer_Miss.txt";
        }
    };

    private static final String PATH = "earthhack/util/";

    public abstract String getDefaultMessage();

    public abstract String getFile();

}
