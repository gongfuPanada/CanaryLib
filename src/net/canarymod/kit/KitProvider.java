package net.canarymod.kit;

import java.util.ArrayList;

import net.canarymod.backbone.BackboneKits;

public class KitProvider {
    private ArrayList<Kit> kits;
    private BackboneKits backbone;

    public KitProvider() {
        backbone = new BackboneKits();
        kits = backbone.loadKits();
    }

    /**
     * Add new kit
     * 
     * @param kit
     */
    public void addKit(Kit kit) {
        backbone.addKit(kit);
        kits.add(kit);
    }

    /**
     * Remove a kit
     * 
     * @param kit
     */
    public void removeKit(Kit kit) {
        backbone.removeKit(kit);
        kits.remove(kit);
    }

    public void updateKit(Kit kit) {
        Kit k = getKit(kit.getName());
        if (k != null) {
            kits.remove(k);
        }
        kits.add(kit);
        backbone.updateKit(kit);
    }

    /**
     * Returns warp that has the given name or null if not exists
     * 
     * @param name
     * @return
     */
    public Kit getKit(String name) {
        for (Kit g : kits) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }
}
