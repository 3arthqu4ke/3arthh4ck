package me.earth.earthhack.installer.srg2notch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Mapping
{
    private final Map<String, String> classes;
    private final Map<String, String> fields;
    private final Map<String, List<MethodMapping>> methods;
    private final Map<String, String> constants;

    public Mapping(Map<String, String> classes,
                   Map<String, String> fields,
                   Map<String, List<MethodMapping>> methods,
                   Map<String, String> constants)
    {
        this.classes   = classes;
        this.fields    = fields;
        this.methods   = methods;
        this.constants = constants;
    }

    public Map<String, String> getClasses()
    {
        return classes;
    }

    public Map<String, String> getFields()
    {
        return fields;
    }

    public Map<String, List<MethodMapping>> getMethods()
    {
        return methods;
    }

    public Map<String, String> getConstants()
    {
        return constants;
    }

    public static Mapping fromResource(String name) throws IOException
    {
        Map<String, List<MethodMapping>> methods = new HashMap<>();
        Map<String, String> classes   = new HashMap<>();
        Map<String, String> fields    = new HashMap<>();
        Map<String, String> constants = new HashMap<>();

        try (BufferedReader br =
             new BufferedReader(
                 new InputStreamReader(
                     Objects.requireNonNull(
                         Mapping
                             .class
                             .getClassLoader()
                             .getResourceAsStream(name)
                     )
                 )
             ))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] mapping = line.split(",");
                switch (mapping[0])
                {
                    case "class":
                        classes.put(mapping[2], mapping[1]);
                        break;
                    case "field":
                        if (!mapping[2].startsWith("field"))
                        {
                            String[] notch = MappingUtil.splitField(mapping[1]);
                            String ownerConstant = notch[0] + "/" + mapping[2];
                            constants.put(ownerConstant, notch[1]);
                            break;
                        }

                        String fn = MappingUtil.splitField(mapping[1])[1];
                        fields.put(mapping[2], fn);
                        break;
                    case "func":
                        // notch mapping
                        String[] mnn = MappingUtil.splitMethod(mapping[1]);
                        // searge mapping
                        String[] mns = MappingUtil.splitMethod(mapping[2]);

                        if (!mns[1].startsWith("func"))
                        {
                            // access$000 and server methods,
                            // not important for us anyways
                            break;
                        }

                        methods.computeIfAbsent(mns[1], v -> new ArrayList<>(1))
                               .add(new MethodMapping(mnn[0], mnn[1], mnn[2]));
                        break;
                    default:
                }
            }
        }

        return new Mapping(classes, fields, methods, constants);
    }

}
