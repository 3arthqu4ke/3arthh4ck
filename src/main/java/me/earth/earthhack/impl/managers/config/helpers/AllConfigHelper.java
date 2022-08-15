package me.earth.earthhack.impl.managers.config.helpers;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;
import me.earth.earthhack.impl.util.misc.io.IORunnable;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AllConfigHelper implements ConfigHelper<Config> {
    private final Map<String, Config> allConfigs = new HashMap<>();
    private final Register<ConfigHelper<?>> manager;
    private boolean recursive = false;

    public AllConfigHelper(Register<ConfigHelper<?>> manager) {
        this.manager = manager;
    }

    @Override
    public void save() throws IOException {
        performAction(() -> {
            for (ConfigHelper<?> helper : manager.getRegistered()) {
                helper.save();
            }
        });
    }

    @Override
    public void refresh() throws IOException {
        performAction(() -> {
            for (ConfigHelper<?> helper : manager.getRegistered()) {
                helper.refresh();
            }
        });
    }

    @Override
    public void save(String name) throws IOException {
        performAction(() -> {
            for (ConfigHelper<?> helper : manager.getRegistered()) {
                helper.save(name);
            }
        });
    }

    @Override
    public void load(String name) throws IOException {
        performAction(() -> {
            for (ConfigHelper<?> helper : manager.getRegistered()) {
                helper.load(name);
            }
        });
    }

    @Override
    public void refresh(String name) throws IOException {
        performAction(() -> {
            for (ConfigHelper<?> helper : manager.getRegistered()) {
                helper.refresh(name);
            }
        });
    }

    @Override
    public void delete(String name) throws IOException, ConfigDeleteException {
        try {
            if (!recursive) {
                recursive = true;
                for (ConfigHelper<?> helper : manager.getRegistered()) {
                    helper.delete(name);
                }

                allConfigs.remove(name.toLowerCase());
            }
        } finally {
            recursive = false;
        }
    }

    @Override
    public Collection<Config> getConfigs() {
        for (ConfigHelper<?> helper : manager.getRegistered()) {
            if (helper instanceof AllConfigHelper) {
                continue;
            }

            for (Config config : helper.getConfigs()) {
                String lower = config.getName().toLowerCase();
                if (!allConfigs.containsKey(lower)) {
                    allConfigs.put(lower, new DummyConfig(config.getName()));
                }
            }
        }

        return allConfigs.values();
    }

    @Override
    public String getName() {
        return "all";
    }

    private void performAction(IORunnable runnable) throws IOException {
        try {
            if (!recursive) {
                recursive = true;
                runnable.run();
            }
        } finally {
            recursive = false;
        }
    }

    private static final class DummyConfig implements Config {
        private final String name;

        private DummyConfig(String name) {
            this.name = name;
        }

        @Override
        public void apply() {

        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DummyConfig)) return false;
            DummyConfig that = (DummyConfig) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

}
