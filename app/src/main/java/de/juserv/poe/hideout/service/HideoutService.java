package de.juserv.poe.hideout.service;

import de.juserv.poe.hideout.gui.Messages;
import de.juserv.poe.hideout.model.*;
import lombok.Getter;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@SuppressWarnings("unchecked")
public class HideoutService {

    @Getter
    private static final HideoutService INSTANCE = new HideoutService();
    private List<DoodadInfo> doodadInfos;
    private List<HideoutInfo> hideoutInfos;
    private List<MusicInfo> musicInfos;

    private HideoutService() {
        try {
            loadResources();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, Messages.getString("message.errorMessage", e.getMessage()),
                    Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<DoodadTableInfo> asTableInfo(List<DoodadInfo> doodadInfoList, Language language) {
        List<DoodadTableInfo> result = new ArrayList<>();

        Map<DoodadInfo, Long> list = getDoodadAmounts(doodadInfoList);
        list.keySet().forEach(e -> {
            Long cost = e.getCost() != null && e.getCost() > 0L ? list.get(e) * e.getCost() : null;
            DoodadTableInfo info = new DoodadTableInfo();
            info.setAmount(list.get(e));
            info.setName(e.getName().get(language.getLocalCode()));
            info.setCost(cost);
            info.setMTX(e.isMTX());
            info.setLevel(e.getLevel());
            info.setOwned(false);
            result.add(info);
        });

        return result;
    }

    public List<DoodadTableInfo> asTableInfo(List<DoodadInfo> doodadInfoList, List<DoodadInfo> ownedDoodadInfoList,
                                             Language language) {
        List<DoodadTableInfo> result = new ArrayList<>();

        List<DoodadInfo> filteredList = new ArrayList<>(doodadInfoList);
        ownedDoodadInfoList.forEach(filteredList::remove);

        // UnownedItems
        Map<DoodadInfo, Long> listUnowned = getDoodadAmounts(filteredList);
        listUnowned.keySet().forEach(e -> {
            Long cost = e.getCost() != null && e.getCost() > 0L ? listUnowned.get(e) * e.getCost() : null;
            DoodadTableInfo info = new DoodadTableInfo();
            info.setAmount(listUnowned.get(e));
            info.setName(e.getName().get(language.getLocalCode()));
            info.setCost(cost);
            info.setMTX(e.isMTX());
            info.setLevel(e.getLevel());
            info.setOwned(false);
            result.add(info);
        });

        // Already Owned Items
        Map<DoodadInfo, Long> listOwned = getDoodadAmounts(ownedDoodadInfoList);
        listOwned.keySet().forEach(e -> {
            DoodadTableInfo info = new DoodadTableInfo();
            info.setAmount(listOwned.get(e));
            info.setName(e.getName().get(language.getLocalCode()));
            info.setCost(0L);
            info.setMTX(e.isMTX());
            info.setLevel(e.getLevel());
            info.setOwned(true);
            result.add(info);
        });

        return result;
    }

    public Optional<DoodadInfo> doodadByHashId(Long id) {
        return doodadInfos.stream().filter(e -> e.getHashId().equals(id)).findFirst();
    }

    public Map<DoodadInfo, Long> getDoodadAmounts(List<DoodadInfo> list) {
        return list.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    public Map<Master, List<DoodadInfo>> getUnownedDoodads(List<DoodadInfo> doodadInfoList,
                                                           List<DoodadInfo> ownedDoodadInfoList) {
        return getUnownedDoodads(doodadInfoList, ownedDoodadInfoList, false);
    }

    public Map<Master, List<DoodadInfo>> getUnownedDoodads(List<DoodadInfo> doodadInfoList,
                                                           List<DoodadInfo> ownedDoodadInfoList, boolean distinct) {
        List<DoodadInfo> filteredList = new ArrayList<>(doodadInfoList);
        ownedDoodadInfoList.forEach(filteredList::remove);

        if (distinct) {
            return filteredList.stream().distinct().collect(Collectors.groupingBy(e -> Master.byId(e.getMaster())));
        } else {
            return filteredList.stream().collect(Collectors.groupingBy(e -> Master.byId(e.getMaster())));
        }
    }

    public Optional<HideoutInfo> hideoutByHashId(Long id) {
        return hideoutInfos.stream().filter(e -> e.getHashId().equals(id)).findFirst();
    }

    private void loadResources() throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(
                new GZIPInputStream(getClass().getResourceAsStream("/data/1.0/hideouts.dat")))) {
            hideoutInfos = (List<HideoutInfo>) in.readObject();
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new GZIPInputStream(getClass().getResourceAsStream("/data/1.0/music.dat")))) {
            musicInfos = (List<MusicInfo>) in.readObject();
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new GZIPInputStream(getClass().getResourceAsStream("/data/1.0/decorations.dat")))) {
            doodadInfos = (List<DoodadInfo>) in.readObject();
        }
    }

    public Optional<MusicInfo> musicByHashId(Long id) {
        return musicInfos.stream().filter(e -> e.getHashId().equals(id)).findFirst();
    }

}
