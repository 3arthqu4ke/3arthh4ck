package me.earth.earthhack.installer.srg2notch;

import me.earth.earthhack.impl.util.misc.collections.ArrayUtil;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappingUtil
{
    /**
     * Attempts to retrieve a new method name for the given
     * method (owner, name, desc) from the given mapping.
     * Returns the given name if no matching name has been found.
     *
     * @param owner the owner of the method. (notch).
     * @param name the name of the method. (searge)
     * @param desc the desc of the method. (notch).
     * @param mapping mapping to apply.
     * @return the name if no mapping has been found or a new remapped one.
     */
    public static String map(String owner,
                             String name,
                             String desc,
                             Mapping mapping)
    {
        String methodMapping = getMethodMapping(owner, name, desc, mapping);
        return methodMapping == null ? name : methodMapping;
    }

    /**
     * Attempts to retrieve a new method name for the given
     * method (owner, name, desc) from the given mapping.
     * Returns null if no matching name has been found.
     *
     * @param owner the owner of the method. (notch).
     * @param name the name of the method. (searge)
     * @param desc the desc of the method. (notch).
     * @param mapping mapping to apply.
     * @return new name for the method, might be null.
     */
    private static String getMethodMapping(String owner,
                                           String name,
                                           String desc,
                                           Mapping mapping)
    {
        List<MethodMapping> mappings = mapping.getMethods().get(name);
        if (mappings == null)
        {
            return null;
        }

        if (mappings.size() == 1)
        {
            return mappings.get(0).getName();
        }

        double bestFactor = 0.0;
        MethodMapping best = null;
        for (MethodMapping m : mappings)
        {
            double matchFactor = 0.0;
            if (m.getDesc().equals(desc))
            {
                matchFactor++;
            }

            if (m.getOwner().equals(owner))
            {
                matchFactor++;
            }

            if (name.contains("_" + m.getName()))
            {
                matchFactor += 0.5;
            }

            if (best == null || matchFactor > bestFactor)
            {
                bestFactor = matchFactor;
                best = m;
            }
        }

        return best == null ? null : best.getName();
    }

    /**
     * Remaps the local variables from a StackFrame.
     *
     * @param objects the locals/stack from the Frame.
     * @param mapping the mapping.
     * @return a new List containing all of the objects from the old list,
     *         remapped.
     */
    public static List<Object> map(List<Object> objects, Mapping mapping)
    {
        if (objects == null)
        {
            return null;
        }

        List<Object> local = new ArrayList<>(objects.size());
        for (Object o : objects)
        {
            if (o instanceof String)
            {
                String s = (String) o;
                if (s.startsWith("["))
                {
                    o = mapDescription(s, mapping);
                }
                else
                {
                    o = mapping.getClasses().getOrDefault(s, s);
                }
            }

            local.add(o);
        }

        return local;
    }

    /**
     * Remaps a handle.
     *
     * @param h the handle to remap.
     * @param mapping the mappings with which to remap.
     * @return a new Handle, remapped from the old one.
     */
    public static Handle map(Handle h, Mapping mapping)
    {
        String name  = h.getName();
        String owner = h.getOwner();
        String desc  = h.getDesc();

        name = getMethodMapping(owner, name, desc, mapping);
        if (name == null)
        {
            name = mapping.getFields().getOrDefault(h.getName(), h.getName());
        }

        owner = mapping.getClasses().getOrDefault(owner, owner);
        desc  = mapDescription(desc, mapping);
        return new Handle(h.getTag(), owner, name, desc, h.isInterface());
    }

    /**
     * Remaps the given Types descriptor to a new Type.
     *
     * @param type the type to remap.
     * @param mapping the mapping to use.
     * @return a new type, remapped.
     */
    public static Type map(Type type, Mapping mapping)
    {
        return Type.getType(
                MappingUtil.mapDescription(type.getDescriptor(), mapping));
    }

    /**
     * Splits a field declared like this owner/field
     * into { owner, field }.
     *
     * @param field the field to split.
     * @return an array of size 2, index 0 = owner, index 1 = name.
     */
    public static String[] splitField(String field)
    {
        int i          = field.lastIndexOf("/");
        String owner   = field.substring(0, i);
        String name    = field.substring(i + 1);

        return new String[] { owner, name };
    }

    /**
     * Splits a full method name (owner/name(desc)) like:
     * <p>net/minecraft/world/Explosion/func_180343_e()Ljava/util/List;
     * <p>into 3 strings: owner, name and description.
     *
     * @param method the full method name to split.
     * @return an array of owner, name and description.
     */
    public static String[] splitMethod(String method)
    {
        String[] split = method.split("(\\()");
        int i          = split[0].lastIndexOf("/");
        String owner   = split[0].substring(0, i);
        String name    = split[0].substring(i + 1);
        String desc    = "(" + split[1];

        return new String[] { owner, name, desc };
    }

    /**
     * Maps all classes in the given description
     * according to the given mapping.
     *
     * @param desc the description.
     * @param mapping the mapping.
     * @return the remapped description, or null if nothing has changed.
     */
    public static String mapDescription(String desc, Mapping mapping)
    {
        Set<String> classes = matchClasses(desc, ';');
        return map(desc, mapping, classes);
    }

    /**
     * Maps all classes in the given signature
     * according to the given mapping.
     *
     * @param signature the signature.
     * @param mapping the mapping.
     * @return the remapped signature, or null if nothing has changed.
     */
    public static String mapSignature(String signature, Mapping mapping)
    {
        Set<String> classes = matchClasses(signature, '<', ';');
        return map(signature, mapping, classes);
    }

    /**
     * Filters all classes from a Method Description or Signature.
     *
     * @param s the description/signature to match.
     * @param separators the separators between classes
     * @return a list of all classes in the signature.
     */
    private static Set<String> matchClasses(String s, char...separators)
    {
        boolean collect = false;
        Set<String> matched = new HashSet<>();
        StringBuilder current = new StringBuilder();
        for(int i = 0 ; i < s.length() ; i++)
        {
            char c = s.charAt(i);
            if (collect && ArrayUtil.contains(c, separators))
            {
                current.append(c);
                matched.add(current.toString());
                current = new StringBuilder();
                collect = false;
            }
            else if (c == 'L' && !collect)
            {
                collect = true;
                continue;
            }

            if (collect)
            {
                current.append(c);
            }
        }

        return matched;
    }

    /**
     * Remaps all classnames in the given set in the given string.
     * Class names can be retrieved from descriptions and signatures
     * via {@link MappingUtil#matchClasses(String, char...)}.
     * This requires all class names in the string to end with
     * one of the separator chars, which was used in the matchClasses
     * method.
     *
     * @param s the string to remap.
     * @param mapping the mapping to use.
     * @param classes the classnames contained in the string.
     * @return a new renamed string.
     */
    private static String map(String s, Mapping mapping, Set<String> classes)
    {
        String result = s;
        for (String name : classes)
        {
            String clazz     = name.substring(0, name.length() - 1);
            String separator = name.substring(name.length() - 1);
            String replace = mapping.getClasses().get(clazz);
            if (replace != null)
            {
                result = result.replace(name, replace + separator);
            }
        }

        return result;
    }

}