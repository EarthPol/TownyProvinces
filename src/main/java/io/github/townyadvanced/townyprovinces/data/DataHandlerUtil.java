package io.github.townyadvanced.townyprovinces.data;

import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.util.FileMgmt;
import io.github.townyadvanced.townyprovinces.TownyProvinces;
import io.github.townyadvanced.townyprovinces.data.TownyProvincesDataHolder;
import io.github.townyadvanced.townyprovinces.objects.Province;
import io.github.townyadvanced.townyprovinces.objects.TPCoord;
import io.github.townyadvanced.townyprovinces.settings.TownyProvincesSettings;
import io.github.townyadvanced.townyprovinces.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataHandlerUtil {

	public static boolean loadAllData() {
		loadAllProvinces();
		return true;
	}

	public static boolean saveAllData() {
		saveAllProvinces();
		return true;
	}


	private static void saveAllProvinces() {
		TownyProvinces.info("Now Saving Provinces");
		//Delete existing files
		List<File> provinceFiles = FileUtil.readListOfFiles(FileUtil.PROVINCES_FOLDER_PATH);
		for (File file : provinceFiles) {
			file.delete();
		}
		//Save all provinces
		for (Province province : TownyProvincesDataHolder.getInstance().getProvincesSet()) {
			saveProvince(province);
		}
		TownyProvinces.info("All Provinces Saved");
	}

	public static void saveProvince(Province province) {
		String folderPath = TownyProvinces.getPlugin().getDataFolder().toPath().resolve(FileUtil.PROVINCES_FOLDER_PATH).toString();
		String fileName = folderPath + "/" + province.getId() + ".yml";
		Map<String, String> fileEntries = new HashMap<>();
		fileEntries.put("home_block", "" + province.getHomeBlock().getX() + "," + province.getHomeBlock().getZ());
		fileEntries.put("is_sea", "" + province.isSea());
		fileEntries.put("is_land_validation_requested", "" + province.isLandValidationRequested());
		fileEntries.put("new_town_cost", "" + province.getNewTownCost());
		fileEntries.put("upkeep_town_cost", "" + province.getUpkeepTownCost());
		fileEntries.put("coords", "" + getCoordsAsWriteableString(province));
		FileUtil.saveHashMapIntoFile(fileEntries, fileName);
	}

	/**
	 * 
	 * @param province province
	 * @return
	 */
	private static String getCoordsAsWriteableString(Province province) {
		StringBuilder result = new StringBuilder();
		boolean firstCoord = true;
		for(TPCoord coord: province.getCoordsInProvince()) {
			if(firstCoord) {
				firstCoord = false;
			} else {
				result.append("|");
			}
			result.append(coord.getX()).append(",").append(coord.getZ());
		}
		return  result.toString();
	}

	private static void loadAllProvinces() {
		TownyProvinces.info("Now Loading Provinces");
		List<File> provinceFiles = FileUtil.readListOfFiles(FileUtil.PROVINCES_FOLDER_PATH);
		for(File provinceFile: provinceFiles) {
			loadProvince(provinceFile);
		}
		TownyProvinces.info("All Provinces Loaded");
	}

	public static void loadProvince(File regionDefinitionFile) {
		//Read values from province file
		Map<String,String> fileEntries = FileMgmt.loadFileIntoHashMap(regionDefinitionFile);
		TPCoord homeBlock = unpackCoord(fileEntries.get("home_block"));
		boolean isSea = Boolean.parseBoolean(fileEntries.get("is_sea"));
		boolean isLandValidationRequested = false; 
		int newTownCost = 0;
		int upkeepTownCost = 0;
		if(fileEntries.containsKey("is_land_validation_requested")) {
			isLandValidationRequested = Boolean.parseBoolean(fileEntries.get("is_land_validation_requested"));
		}
		if(fileEntries.containsKey("new_town_cost")) {
			newTownCost = Integer.parseInt(fileEntries.get("new_town_cost"));
		}
		if(fileEntries.containsKey("upkeep_town_cost")) {
			upkeepTownCost = Integer.parseInt(fileEntries.get("upkeep_town_cost"));
		}
		//Create province
		Province province = new Province(homeBlock, isSea, isLandValidationRequested, newTownCost, upkeepTownCost);
		//Add province to provinces set
		TownyProvincesDataHolder.getInstance().addProvince(province);
		//Add coords to coord-province map
		Set<TPCoord> coords = unpackCoords(fileEntries.get("coords"));
		for(TPCoord coord: coords) {
			TownyProvincesDataHolder.getInstance().getCoordProvinceMap().put(coord, province);
		}
	}

	private static Set<TPCoord> unpackCoords(String allCoordsAsString) {
		Set<TPCoord> result = new HashSet<>();
		if(allCoordsAsString.length() > 0) {
			String[] allCoordsAsArray = allCoordsAsString.split("\\|");
			TPCoord coord;
			for (String coordAsString : allCoordsAsArray) {
				coord = unpackCoord(coordAsString);
				result.add(coord);
			}
		}
		return result;
	}
	
	private static TPCoord unpackCoord(String coordAsString) {
		String[] coordAsArray = coordAsString.split(",");
		int x = Integer.parseInt(coordAsArray[0]);
		int z = Integer.parseInt(coordAsArray[1]);
		return new TPCoord(x,z);
	}


}