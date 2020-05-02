package de.juserv.poe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.juserv.poe.hideout.model.DoodadInfo;
import de.juserv.poe.hideout.model.HideoutInfo;
import de.juserv.poe.hideout.model.MusicInfo;
import de.juserv.poe.jackson.IndexDeserializer;
import de.juserv.poe.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * Class which converts the output of PyPoE to our Format.
 */
@SuppressWarnings({"OptionalGetWithoutIsPresent", "ResultOfMethodCallIgnored"})
public class Loader {

    private static final String BASE_ITEMS_FILENAME = "BaseItemTypes.json";
    private static final String HIDEOUTS_FILENAME = "Hideouts.json";
    private static final String HIDEOUT_DOODADS_FILENAME = "HideoutDoodads.json";
    private static final String[] LANGUAGES = new String[]{"us", "de", "fr", "kr", "br", "ru", "sp", "th"};
    private static final String MUSIC_FILENAME = "Music.json";
    private static final String PETS_FILENAME = "Pet.json";
    private static final String WORLD_AREAS_FILENAME = "WorldAreas.json";

    private static final ObjectMapper mapper =
            new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static String DATA_VERSION;

    private HashMap<String, List<BaseItemTypeData>> baseItems = new HashMap<>();
    private List<HideoutDoodadsData> hideoutDoodads;
    private List<HideoutsData> hideouts;
    private HashMap<String, List<MusicData>> music = new HashMap<>();
    private List<PetData> pets;
    private HashMap<String, List<WorldAreasData>> worldAreas = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.load(Loader.class.getResourceAsStream("/application.properties"));
        DATA_VERSION = prop.getProperty("app.data.version");

        new File("./app/src/main/resources/data/" + DATA_VERSION).mkdirs();

        Loader l = new Loader();
        l.loadAll(new File("./parser/src/main/resources/input/"));
        l.mapDoodads();
        l.mapMusic();
        l.mapHideouts();
    }

    /**
     * Load an Map all Files from PyPoE.
     *
     * @param base Path where all files are.
     * @throws Exception Generic Exception
     */
    private void loadAll(File base) throws Exception {
        System.out.println("Parsing files");
        hideouts = loadExport(new File(base, HIDEOUTS_FILENAME), HideoutsData.class);
        hideoutDoodads = loadExport(new File(base, HIDEOUT_DOODADS_FILENAME), HideoutDoodadsData.class);
        pets = loadExport(new File(base, PETS_FILENAME), PetData.class);

        for (String lang : LANGUAGES) {
            File langFolder = new File(base, lang);
            baseItems.put(lang, loadExport(new File(langFolder, BASE_ITEMS_FILENAME), BaseItemTypeData.class));
            music.put(lang, loadExport(new File(langFolder, MUSIC_FILENAME), MusicData.class));
            worldAreas.put(lang, loadExport(new File(langFolder, WORLD_AREAS_FILENAME), WorldAreasData.class));
        }
    }

    /**
     * Loads an PyPoE Export file and converts it to T.
     *
     * @param file      The PyPoE file.
     * @param dataClass TargetClass.
     * @param <T>       Type of TargetClass.
     * @return List of TargetClass.
     * @throws Exception Generic Exception
     */
    private <T> List<T> loadExport(File file, Class<T> dataClass) throws Exception {
        TypeReference<List<ExportFile<T>>> type = new TypeReference<List<ExportFile<T>>>() {
        };

        List<ExportFile<T>> value =
                mapper.readerFor(type).withAttribute(IndexDeserializer.JAVA_TYPE, dataClass).readValue(file);
        return value.get(0).getData();
    }

    /**
     * Load all MTX information about Hideouts from the PoE Shop.
     *
     * @param itemList List with all {@link DoodadInfo}'S
     * @throws Exception Generic Exception
     */
    private void loadMtxInfoHideout(List<DoodadInfo> itemList) throws Exception {
        System.out.println("Loading MTX");
        // ShopNames and DecorationNames differ. Loading DE and US seams to match 99% of all Decorations
        Document doc = Jsoup.connect("https://de.pathofexile.com/shop/category/hideout-decorations#").get();

        Elements list = doc.select(".shopItem");
        for (Element element : list) {
            String name = element.select("a.name").text();
            String cost = element.select(".totalCost").text();

            Optional<DoodadInfo> item = itemList.stream().filter(e -> name.equals(e.getName().get("de"))).findFirst();

            if (item.isPresent()) {
                item.get().setMTX(true);
                item.get().setCost(Long.valueOf(cost));
            }
        }

        doc = Jsoup.connect("https://www.pathofexile.com/shop/category/hideout-decorations#").get();

        list = doc.select(".shopItem");
        for (Element element : list) {
            String name = element.select("a.name").text();
            String cost = element.select(".totalCost").text();

            Optional<DoodadInfo> item = itemList.stream().filter(e -> name.equals(e.getName().get("us"))).findFirst();

            if (item.isPresent()) {
                item.get().setMTX(true);
                item.get().setCost(Long.valueOf(cost));
            }

            // Manual Mappings because GGG is good at messing up names
            if ("Whale Skeleton Decoration".equals(name)) {
                item = itemList.stream().filter(e -> e.getHashId() == 1497649374L).findFirst();
                item.get().setMTX(true);
                item.get().setCost(Long.valueOf(cost));
            }
            if ("Malachai Heart Decoration".equals(name)) {
                item = itemList.stream().filter(e -> e.getHashId() == 3541647204L).findFirst();
                item.get().setMTX(true);
                item.get().setCost(Long.valueOf(cost));
            }
        }
    }

    /**
     * Load all MTX information about Pets from the PoE Shop.
     *
     * @param itemList List with all {@link DoodadInfo}'S
     * @throws Exception Generic Exception
     */
    private void loadMtxInfoPets(List<DoodadInfo> itemList) throws Exception {
        System.out.println("Loading Pets");
        // ShopNames and DecorationNames differ. Loading DE and US seams to match 99% of all Decorations
        Document doc = Jsoup.connect("https://de.pathofexile.com/shop/category/pets#").get();

        Elements list = doc.select(".shopItem");
        for (Element element : list) {
            String name = element.select("a.name").text();
            String cost = element.select(".totalCost").text();

            Optional<DoodadInfo> item = itemList.stream().filter(e -> name.equals(e.getName().get("de"))).findFirst();

            if (item.isPresent()) {
                item.get().setMTX(true);
                item.get().setCost(Long.valueOf(cost));
            }
        }

        doc = Jsoup.connect("https://www.pathofexile.com/shop/category/pets#").get();

        list = doc.select(".shopItem");
        for (Element element : list) {
            String name = element.select("a.name").text();
            String cost = element.select(".totalCost").text();

            Optional<DoodadInfo> item = itemList.stream().filter(e -> name.equals(e.getName().get("us"))).findFirst();

            if (item.isPresent()) {
                item.get().setMTX(true);
                item.get().setCost(Long.valueOf(cost));
            }
        }
    }

    /**
     * Maps all Doodads to our Format.
     *
     * @throws Exception Generic Exception
     */
    private void mapDoodads() throws Exception {
        System.out.println("Map Doodads");
        List<DoodadInfo> itemList = new ArrayList<>();

        // Normal Doodads
        for (HideoutDoodadsData d : hideoutDoodads) {
            DoodadInfo i = new DoodadInfo();
            i.setCost(d.getCost());
            i.setMaster(d.getMaster() != null ? d.getMaster() : 0);
            i.setLevel(d.getLevel());
            i.setHashId(baseItems.get("us").get(Math.toIntExact(d.getBaseItemId())).getHashId());

            for (String lang : LANGUAGES) {
                i.getName().put(lang, baseItems.get(lang).get(Math.toIntExact(d.getBaseItemId())).getName());
            }

            itemList.add(i);
        }

        // Pets are also Doodads
        for (PetData pet : pets) {
            DoodadInfo i = new DoodadInfo();
            i.setMTX(true);
            i.setMaster(0L);
            i.setHashId(baseItems.get("us").get(Math.toIntExact(pet.getBaseItemId())).getHashId());

            for (String lang : LANGUAGES) {
                i.getName().put(lang, baseItems.get(lang).get(Math.toIntExact(pet.getBaseItemId())).getName());
            }

            itemList.add(i);
        }

        loadMtxInfoHideout(itemList);
        loadMtxInfoPets(itemList);

        GZIPOutputStream out = new GZIPOutputStream(
                new FileOutputStream(new File("./app/src/main/resources/data/" + DATA_VERSION + "/decorations.dat")));
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(itemList);
        oos.flush();
        oos.close();
    }

    /**
     * Maps all HideoutNames to our Format.
     *
     * @throws Exception Generic Exception
     */
    private void mapHideouts() throws Exception {
        System.out.println("Map Hideouts");
        List<HideoutInfo> itemList = new ArrayList<>();

        for (HideoutsData d : hideouts) {
            HideoutInfo h = new HideoutInfo();
            h.setHashId(d.getHashId());

            // Manual Mapping necessary for those, dunno why
            Long searchId = h.getHashId();
            if (searchId == 18782L) {
                searchId = 19161L;
            } else if (searchId == 23230L) {
                searchId = 13588L;
            }
            Long finalSearchId = searchId;

            for (String lang : LANGUAGES) {
                h.getName().put(lang,
                        worldAreas.get(lang).stream().filter(e -> e.getHashId().equals(finalSearchId)).findFirst().get()
                                .getName());
            }

            itemList.add(h);
        }

        GZIPOutputStream out = new GZIPOutputStream(
                new FileOutputStream(new File("./app/src/main/resources/data/" + DATA_VERSION + "/hideouts.dat")));
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(itemList);
        oos.flush();
        oos.close();
    }

    /**
     * Maps all MusicNames to our Format.
     *
     * @throws Exception Generic Exception
     */
    private void mapMusic() throws Exception {
        System.out.println("Map Music");
        List<MusicInfo> itemList = new ArrayList<>();

        for (MusicData d : music.get("us")) {
            if (d.getForHideout()) {
                MusicInfo m = new MusicInfo();
                m.setHashId(d.getHashId());

                for (String lang : LANGUAGES) {
                    m.getName().put(lang, music.get(lang).get(d.getId()).getName());
                }

                itemList.add(m);
            }
        }

        GZIPOutputStream out = new GZIPOutputStream(
                new FileOutputStream(new File("./app/src/main/resources/data/" + DATA_VERSION + "/music.dat")));
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(itemList);
        oos.flush();
        oos.close();
    }
}
