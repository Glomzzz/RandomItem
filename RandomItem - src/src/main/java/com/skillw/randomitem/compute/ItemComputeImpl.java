package com.skillw.randomitem.compute;

import com.skillw.randomitem.api.object.ItemCompute;
import com.skillw.randomitem.api.object.SubString;
import com.skillw.randomitem.utils.RandomItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.RandomItemUtils.doReplace;
import static com.skillw.randomitem.utils.RandomItemUtils.format;

/**
 * @author zhaoshaotian
 */
public class ItemComputeImpl implements ItemCompute {
    private ConcurrentHashMap<String, String> computeMap;
    private ConcurrentHashMap<String, String> fixedMap;
    private ConcurrentHashMap<String, String> maxMap;
    private ConcurrentHashMap<String, String> numberMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<SubString>> subStringMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<String>> alreadyStringMap = new ConcurrentHashMap<>();

    public ItemComputeImpl() {
        this.computeMap = new ConcurrentHashMap<>();
        this.maxMap = new ConcurrentHashMap<>();
        this.fixedMap = new ConcurrentHashMap<>();
    }

    @Override
    public ConcurrentHashMap<String, String> getFixedMap() {
        return this.fixedMap;
    }

    @Override
    public void setFixedMap(ConcurrentHashMap<String, String> fixedMap) {
        this.fixedMap = fixedMap;
    }

    @Override
    public ConcurrentHashMap<String, List<String>> getAlreadyStringMap() {
        return this.alreadyStringMap;
    }

    @Override
    public void setAlreadyStringMap(ConcurrentHashMap<String, List<String>> alreadyStringMap) {
        this.alreadyStringMap = alreadyStringMap;
    }

    @Override
    public void addComputeFromSection(ConfigurationSection section) {
        String formula = (section.getString("formula"));
        String max = (section.getString("max") != null) ? section.getString("max") : "-1";
        String fixed = (section.getString("fixed") != null) ? section.getString("fixed") : "0";
        this.getComputeMap().put(section.getName(), formula);
        this.getMaxMap().put(section.getName(), max);
        this.getFixedMap().put(section.getName(), fixed);
    }

    @Override
    public ConcurrentHashMap<String, String> getMaxMap() {
        return this.maxMap;
    }

    @Override
    public void setMaxMap(ConcurrentHashMap<String, String> maxMap) {
        this.maxMap = maxMap;
    }

    @Override
    public ConcurrentHashMap<String, String> getMaxMapClone() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (String key : this.maxMap.keySet()) {
            String value = this.maxMap.get(key);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public ConcurrentHashMap<String, String> getComputeMap() {
        return this.computeMap;
    }

    @Override
    public void setComputeMap(ConcurrentHashMap<String, String> computeMap) {
        this.computeMap = computeMap;
    }

    @Override
    public ConcurrentHashMap<String, String> getComputeMapClone() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (String key : this.computeMap.keySet()) {
            String value = this.computeMap.get(key);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public ConcurrentHashMap<String, String> getFixedMapClone() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (String key : this.fixedMap.keySet()) {
            String value = this.fixedMap.get(key);
            map.put(key, value);
        }
        return map;
    }

    private ConcurrentHashMap<String, String> calculateFixed(UUID uuid) {
        ConcurrentHashMap<String, String> fixedMap = this.getFixedMapClone();
        return this.replaceMap(uuid, fixedMap);
    }

    @NotNull
    private ConcurrentHashMap<String, String> replaceMap(UUID uuid, ConcurrentHashMap<String, String> map) {
        for (String computeKey : map.keySet()) {
            this.handleCompute(map, computeKey);
            String compute = map.get(computeKey);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                compute = doReplace(compute, this.subStringMap, this.computeMap, this.numberMap, this.alreadyStringMap, player);
                map.put(computeKey, String.valueOf(RandomItemUtils.getResult(compute)));
            }
        }
        return map;
    }

    private ConcurrentHashMap<String, String> calculateMax(UUID uuid) {
        ConcurrentHashMap<String, String> maxMap = this.getMaxMapClone();
        return this.replaceMap(uuid, maxMap);
    }

    @Override
    public ConcurrentHashMap<String, String> calculateCompute(UUID uuid) {
        ConcurrentHashMap<String, String> computesMap = this.getComputeMapClone();
        ConcurrentHashMap<String, String> maxMap = this.calculateMax(uuid);
        ConcurrentHashMap<String, String> fixedMap = this.calculateFixed(uuid);
        for (String computeKey : computesMap.keySet()) {
            this.handleCompute(computesMap, computeKey);
            String compute = computesMap.get(computeKey);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                compute = doReplace(compute, this.subStringMap, this.computeMap, this.numberMap, this.alreadyStringMap, player);
                double max = Double.parseDouble(maxMap.get(computeKey));
                int fixed = (int) Double.parseDouble(fixedMap.get(computeKey));
                computesMap.put(computeKey, String.valueOf(format((max == -1) ? RandomItemUtils.getResult(compute) : Math.min(RandomItemUtils.getResult(compute), max), fixed)));
            }
        }
        return computesMap;
    }

    private void handleCompute(ConcurrentHashMap<String, String> computesMap, String computeKey) {
        String compute = computesMap.get(computeKey);
        for (String computesKey2 : computesMap.keySet()) {
            if (computesKey2.equals(computeKey)) {
                break;
            }
            if (computesMap.get(computesKey2) != null) {
                compute = compute.replace("{" + computesKey2 + "}", computesMap.get(computesKey2));
                computesMap.put(computeKey, compute);
            }
        }
    }

    @Override
    public ItemCompute clone() {
        ItemComputeImpl itemCompute = new ItemComputeImpl();
        itemCompute.setComputeMap(this.getComputeMapClone());
        itemCompute.setMaxMap(this.getMaxMapClone());
        itemCompute.setFixedMap(this.getFixedMapClone());
        return itemCompute;
    }

    @Override
    public ConcurrentHashMap<String, String> getNumberMap() {
        return this.numberMap;
    }

    @Override
    public void setNumberMap(ConcurrentHashMap<String, String> numberMap) {
        this.numberMap = numberMap;
    }

    @Override
    public ConcurrentHashMap<String, List<SubString>> getSubStringMap() {
        return this.subStringMap;
    }

    @Override
    public void setSubStringMap(ConcurrentHashMap<String, List<SubString>> subStringMap) {
        this.subStringMap = subStringMap;
    }
}
