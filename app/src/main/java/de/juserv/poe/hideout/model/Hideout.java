package de.juserv.poe.hideout.model;

import de.juserv.poe.hideout.service.HideoutService;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Hideout with all infomations.
 */
@Getter
public class Hideout {

    private static final Pattern DOODAD_PATTERN = Pattern.compile(".*\\s?=\\s?\\{.*Hash\\s?=\\s?(\\d+)\\s?,.*}");
    private static final Pattern HIDEOUT_PATTERN = Pattern.compile("Hideout Hash\\s?=\\s?(\\d+)");

    /**
     * Contains Hashes for "Default"-Doodads
     */
    private static final List<Long> ID_FILTER_LIST = new ArrayList<>(Arrays.asList(
            3230065491L, // Stash
            139228481L, // Guild Stash
            1224707366L, // Waypoint
            2059629901L, // Crafting Bench
            2306038834L, // Map Device
            10623884L, // Sister Cassia
            693228958L, // Navali
            2684274993L, // Einhar
            2115859440L, // Alva
            845403974L, // Helena
            2906227343L, // Niko
            3992724805L, // Jun
            3506797600L, // Zana
            4285419720L, // Tane Octavius
            2913423765L // Kirac
    ));
    private static final Pattern MUSIC_PATTERN = Pattern.compile("Music Hash\\s?=\\s?(\\w+)");
    private final List<Long> doodadIds = new ArrayList<>();
    private List<DoodadInfo> doodadList = new ArrayList<>();

    /**
     * Container parser errors or is null if parse is complete
     */
    private Exception error;
    private HideoutInfo hideout;
    private String hideoutFile;
    private Long hideoutHashId;
    private Long musicHashId;
    private MusicInfo musicInfo;

    /**
     * Constructor which tries to parse PoE's .hideout file.
     *
     * @param hideoutFile Path to the .hideout file.
     */
    public Hideout(String hideoutFile) {
        this.hideoutFile = hideoutFile;
        parseFile();
    }

    /**
     * Get the combined favor cost of all doodads (without MTX).
     *
     * @return Long with the favor costs.
     */
    public Long getCost() {
        return doodadList.stream().filter(e -> !e.isMTX()).map(DoodadInfo::getCost).filter(Objects::nonNull)
                .reduce(0L, Long::sum);
    }

    /**
     * Get the favor cost of all doodads from the defined master (without MTX).
     *
     * @param master {@link Master}
     * @return Long with the favor costs for master.
     */
    public Long getCost(Master master) {
        return doodadList.stream().filter(e -> Objects.equals(master.getMasterId(), e.getMaster()))
                .map(DoodadInfo::getCost).filter(Objects::nonNull).reduce(0L, Long::sum);
    }

    /**
     * Get all doodads for one master.
     *
     * @param master {@link Master}
     * @return List with all doodads for master.
     */
    public List<DoodadInfo> getDoodadsByMaster(Master master) {
        return doodadList.stream().filter(e -> e.getMaster().equals(master.getMasterId())).collect(Collectors.toList());
    }

    /**
     * Get the required level for the hideout for one master.
     *
     * @param master {@link Master}
     * @return Long - required level or null of no required level.
     */
    public Long getRequiredMasterLevel(Master master) {
        return doodadList.stream().filter(e -> Objects.equals(master.getMasterId(), e.getMaster()))
                .map(DoodadInfo::getLevel).max(Long::compareTo).orElse(null);
    }

    /**
     * Load all information's based on the hash-ids from the hideout-file.
     */
    private void loadData() {
        HideoutService service = HideoutService.getINSTANCE();

        service.musicByHashId(musicHashId).ifPresent(e -> musicInfo = e);
        service.hideoutByHashId(hideoutHashId).ifPresent(e -> hideout = e);

        for (Long id : doodadIds) {
            if (!ID_FILTER_LIST.contains(id)) {
                Optional<DoodadInfo> doodad = service.doodadByHashId(id);
                if (doodad.isPresent()) {
                    doodadList.add(doodad.get());
                } else {
                    System.out.println("Doodad id " + id + " not found");
                }
            }
        }
    }

    /**
     * Load the file contents
     */
    private void parseFile() {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(hideoutFile), StandardCharsets.UTF_16LE));
            List<String> lines = br.lines().collect(Collectors.toList());
            br.close();
            for (String line : lines) {
                Matcher hideoutMatcher = HIDEOUT_PATTERN.matcher(line);
                Matcher musicMatcher = MUSIC_PATTERN.matcher(line);
                Matcher doodadMatcher = DOODAD_PATTERN.matcher(line);

                if (hideoutMatcher.matches()) {
                    hideoutHashId = Long.valueOf(hideoutMatcher.group(1));
                } else if (musicMatcher.matches()) {
                    musicHashId = Long.valueOf(musicMatcher.group(1));
                } else if (doodadMatcher.matches()) {
                    doodadIds.add(Long.valueOf(doodadMatcher.group(1)));
                }
            }
        } catch (Exception e) {
            error = e;
        }
        loadData();
    }

    /**
     * Read the file again
     */
    public void reload() {
        error = null;
        hideout = null;
        hideoutHashId = null;
        musicInfo = null;
        musicHashId = null;
        doodadList.clear();
        doodadIds.clear();
        parseFile();
    }

    /**
     * @return true, if this hideout uses MTX.
     */
    public boolean usesMTX() {
        return doodadList.stream().anyMatch(DoodadInfo::isMTX);
    }
}
