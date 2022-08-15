package me.earth.earthhack.impl.util.minecraft;

public enum CooldownBypass {

    None() {
        @Override
        public void switchTo(int slot) {
            InventoryUtil.switchTo(slot);
        }

        @Override
        public void switchBack(int lastSlot, int from) {
            this.switchTo(lastSlot);
        }
    },
    Slot() {
        @Override
        public void switchTo(int slot) {
            InventoryUtil.switchToBypass(
                InventoryUtil.hotbarToInventory(slot));
        }
    },
    Swap() {
        @Override
        public void switchTo(int slot) {
            InventoryUtil.switchToBypassAlt(
                InventoryUtil.hotbarToInventory(slot));
        }
    },
    Pick() {
        @Override
        public void switchTo(int slot) {
            InventoryUtil.bypassSwitch(slot);
        }
    };

    public abstract void switchTo(int slot);

    /**
     * Switches back to the last slot before {@link #switchTo(int)} had been
     * called. When we bypass switch we instead need to switch to the slot
     * we switched to with {@link #switchTo(int)} again, the second argument.
     */
    public void switchBack(int lastSlot, int from) {
        this.switchTo(from);
    }

}
