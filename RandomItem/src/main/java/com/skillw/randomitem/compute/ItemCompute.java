package com.skillw.randomitem.compute;

import com.skillw.randomitem.utils.RandomItemUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 */
public class ItemCompute {
    private final ConcurrentHashMap<String, String> computeMap;
    private ConcurrentHashMap<String, String> randomMap = new ConcurrentHashMap<>();

    public ItemCompute(ConcurrentHashMap<String, String> computeMap) {
        this.computeMap = computeMap;
    }

    public static ItemCompute loadConfig(ConfigurationSection section) {
        ConcurrentHashMap<String, String> computeMap = new ConcurrentHashMap<>();
        for (String computeKey : section.getKeys(false)) {
            computeMap.put(computeKey, section.getString(computeKey));
        }
        return new ItemCompute(computeMap);
    }

    public ConcurrentHashMap<String, String> getComputeMap() {
        return computeMap;
    }

    public ConcurrentHashMap<String, String> getRandomMap() {
        return randomMap;
    }

    public void setRandomMap(ConcurrentHashMap<String, String> randomMap) {
        this.randomMap = randomMap;
    }

    public ConcurrentHashMap<String, String> getComputeMapClone() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (String key : computeMap.keySet()) {
            String value = computeMap.get(key);
            map.put(key, value);
        }
        return map;
    }

    public ConcurrentHashMap<String, String> calculateCompute(UUID uuid) {
        ConcurrentHashMap<String, String> computesMap = getComputeMapClone();
        for (String computeKey : computesMap.keySet()) {
            String compute = computesMap.get(computeKey);
            for (String computesKey2 : computesMap.keySet()) {
                if (computesKey2.equals(computeKey)) {
                    break;
                }
                if (computesMap.get(computesKey2) != null) {
                    compute = compute.replace("{" + computesKey2 + "}", computesMap.get(computesKey2));
                }
            }

            {
                Player p = (Player) RandomItemUtils.getLivingEntityByUuid(uuid);
                compute = RandomItemUtils.doReplace(compute, p.getUniqueId());
                for (String key : getRandomMap().keySet()) {
                    String value = getRandomMap().get(key);
                    compute = compute.replace("<" + key + ">", value);
                }
                computesMap.put(computeKey, String.valueOf(RandomItemUtils.getResult(PlaceholderAPI.setPlaceholders(p, compute))));
            }
        }
        return computesMap;
    }
}
