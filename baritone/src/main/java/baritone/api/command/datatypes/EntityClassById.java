/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.api.command.datatypes;

import baritone.api.command.exception.CommandException;
import baritone.api.command.helpers.TabCompleteHelper;
import me.earth.earthhack.vanilla.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

public enum EntityClassById implements IDatatypeFor<Class<? extends Entity>> {
    INSTANCE;

    @Override
    public Class<? extends Entity> get(IDatatypeContext ctx)  {
        ctx = (IDatatypeContext) new ResourceLocation(ctx.getConsumer().getString());
        Class<? extends Entity> entity;
        try {
            if (Environment.hasForge()) {
                throw ThreadQuickExitException.INSTANCE;
            }

            Field registryField = null;

            for (Field field : EntityList.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())
                        || Modifier.isPublic(field.getModifiers())
                        || Modifier.isFinal(field.getModifiers())
                        || RegistryNamespaced.class.isAssignableFrom(field.getType())
                ) {
                    registryField = field;
                    break;
                }
            }

            if (registryField == null) {
                throw ThreadQuickExitException.INSTANCE;
            }

            registryField.setAccessible(true);
            entity = (Class) ((RegistryNamespaced) registryField.get(null)).getObject(ctx);
        } catch (ThreadQuickExitException e) {
            // Forge removes EntityList.REGISTRY field and provides the getClass method as a replacement
            // See https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/patches/minecraft/net/minecraft/entity/EntityList.java.patch
            try {
                entity = EntityList.getClass((ResourceLocation) ctx);
            } catch (Exception ex) {
                throw new RuntimeException("EntityList.REGISTRY does not exist and failed to call the Forge-replacement method", ex);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        if (entity == null) {
            throw new IllegalArgumentException("no entity found by that id");
        }
        return entity;
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        return new TabCompleteHelper()
                .append(EntityList.getEntityNameList().stream().map(Object::toString))
                .filterPrefixNamespaced(ctx.getConsumer().getString())
                .sortAlphabetically()
                .stream();
    }
}