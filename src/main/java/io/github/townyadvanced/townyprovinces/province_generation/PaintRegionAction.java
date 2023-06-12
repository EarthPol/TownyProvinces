package io.github.townyadvanced.townyprovinces.province_generation;

import com.palmergames.bukkit.towny.object.Coord;
import io.github.townyadvanced.townyprovinces.TownyProvinces;
import io.github.townyadvanced.townyprovinces.data.TownyProvincesDataHolder;
import io.github.townyadvanced.townyprovinces.objects.Province;
import io.github.townyadvanced.townyprovinces.objects.ProvinceClaimBrush;
import io.github.townyadvanced.townyprovinces.objects.TPCoord;
import io.github.townyadvanced.townyprovinces.objects.TPFinalCoord;
import io.github.townyadvanced.townyprovinces.objects.TPFreeCoord;
import io.github.townyadvanced.townyprovinces.settings.TownyProvincesSettings;
import io.github.townyadvanced.townyprovinces.util.TownyProvincesMathUtil;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class is used to paint a single region
 */
public class PaintRegionAction {
	private final String regionName;  //Name of real region (not "All")
	private final Map<TPCoord, TPCoord> unclaimedCoordsMap;
	private final int regionMinX;
	private final int regionMaxX;
	private final int regionMinZ;
	private final int regionMaxZ;
	private final int mapMinXCoord;
	private final int mapMaxXCoord;
	private final int mapMinZCoord;
	private final int mapMaxZCoord;
	private final int minBrushMoveAmount;
	private final int maxBrushMoveAmount;
	private final int brushSquareRadiusInChunks;
	private final int claimAreaLimitInSquareMetres;
	public final static double CHUNK_AREA_IN_SQUARE_METRES = Math.pow(TownyProvincesSettings.getChunkSideLength(), 2);
	public final TPFreeCoord searchCoord;
	private final int minAllowedDistanceBetweenProvinceHomeBlocksInChunks;
	private final Location topLeftRegionCorner;
	private final Location bottomRightRegionCorner;
	private final int provinceSizeEstimateForPopulatingInSquareMetres;
	private final int numberOfProvincePaintingCycles;
	private final double tpChunkSideLength;
	private final int newTownCost;
	private final int upkeepTownCost;
	private final double allowedVarianceBetweenIdealAndActualNumProvinces;

	public PaintRegionAction(String regionName, Map<TPCoord,TPCoord> unclaimedCoordsMap) {
		this.regionName = regionName;
		this.unclaimedCoordsMap = unclaimedCoordsMap;
		this.minBrushMoveAmount = TownyProvincesSettings.getProvinceCreatorBrushMinMoveInChunks(regionName);
		this.maxBrushMoveAmount= TownyProvincesSettings.getProvinceCreatorBrushMaxMoveInChunks(regionName);
		this.brushSquareRadiusInChunks = TownyProvincesSettings.getProvinceCreatorBrushSquareRadiusInChunks(regionName);
		this.claimAreaLimitInSquareMetres = TownyProvincesSettings.getProvinceCreatorBrushClaimLimitInSquareMetres(regionName);
		String nameOfFirstRegion = TownyProvincesSettings.getNameOfFirstRegion();
		this.mapMinXCoord = TownyProvincesSettings.getTopLeftCornerLocation(nameOfFirstRegion).getBlockX() / TownyProvincesSettings.getChunkSideLength();
		this.mapMaxXCoord = TownyProvincesSettings.getBottomRightCornerLocation(nameOfFirstRegion).getBlockX() / TownyProvincesSettings.getChunkSideLength();
		this.mapMinZCoord = TownyProvincesSettings.getTopLeftCornerLocation(nameOfFirstRegion).getBlockZ() / TownyProvincesSettings.getChunkSideLength();
		this.mapMaxZCoord = TownyProvincesSettings.getBottomRightCornerLocation(nameOfFirstRegion).getBlockZ() / TownyProvincesSettings.getChunkSideLength();
		this.searchCoord = new TPFreeCoord(0,0);
		int minAllowedDistanceBetweenProvinceHomeBlocksInMetres = TownyProvincesSettings.getMinAllowedDistanceBetweenProvinceHomeBlocks(regionName);
		this.minAllowedDistanceBetweenProvinceHomeBlocksInChunks = minAllowedDistanceBetweenProvinceHomeBlocksInMetres / TownyProvincesSettings.getChunkSideLength();
		this.regionMinX = TownyProvincesSettings.getTopLeftCornerLocation(regionName).getBlockX();
		this.regionMaxX = TownyProvincesSettings.getBottomRightCornerLocation(regionName).getBlockX();
		this.regionMinZ = TownyProvincesSettings.getTopLeftCornerLocation(regionName).getBlockZ();
		this.regionMaxZ = TownyProvincesSettings.getBottomRightCornerLocation(regionName).getBlockZ();
		this.topLeftRegionCorner = TownyProvincesSettings.getTopLeftCornerLocation(regionName);
		this.bottomRightRegionCorner = TownyProvincesSettings.getBottomRightCornerLocation(regionName);
		this.provinceSizeEstimateForPopulatingInSquareMetres = TownyProvincesSettings.getProvinceSizeEstimateForPopulatingInSquareMetres(regionName);
		this.numberOfProvincePaintingCycles= TownyProvincesSettings.getNumberOfProvincePaintingCycles(regionName);
		this.tpChunkSideLength = TownyProvincesSettings.getChunkSideLength();
		this.newTownCost = TownyProvincesSettings.getNewTownCost(regionName);
		this.upkeepTownCost = TownyProvincesSettings.getUpkeepTownCost(regionName);
		this.allowedVarianceBetweenIdealAndActualNumProvinces = TownyProvincesSettings.getMaxAllowedVarianceBetweenIdealAndActualNumProvinces(regionName);
	}
	
	boolean executeAction() {
		TownyProvinces.info("Now Painting Provinces In Region: " + regionName);
		//Clear provinces which are mostly in the given area
		deleteExistingProvincesWhichAreMostlyInSpecifiedArea(regionName);

		/*
		 * Create province objects - empty except for the homeblocks
		 * This is the start point for the paint brushes
		 */
		if(!generateProvinceObjects()) {
			return false;
		}

		//Execute chunk claim competition
		if(!executeChunkClaimCompetition()) {
			return false;
		}

		TownyProvinces.info("Finished Painting Provinces In Region: " + regionName);
		return true;
	}
	
	private static boolean deleteExistingProvincesWhichAreMostlyInSpecifiedArea(String regionName) {
		TownyProvinces.info("Now deleting provinces which are mostly in the specified area.");
		int numProvincesDeleted = 0;
		int minX = TownyProvincesSettings.getTopLeftCornerLocation(regionName).getBlockX() / TownyProvincesSettings.getChunkSideLength();
		int maxX  = TownyProvincesSettings.getBottomRightCornerLocation(regionName).getBlockX() / TownyProvincesSettings.getChunkSideLength();
		int minZ = TownyProvincesSettings.getTopLeftCornerLocation(regionName).getBlockZ() / TownyProvincesSettings.getChunkSideLength();
		int maxZ  = TownyProvincesSettings.getBottomRightCornerLocation(regionName).getBlockZ() / TownyProvincesSettings.getChunkSideLength();
		for(Province province: (new HashSet<>(TownyProvincesDataHolder.getInstance().getProvincesSet()))) {
			List<TPCoord> coordsInProvince = province.getCoordsInProvince();
			int numProvinceBlocksInSpecifiedArea = 0;
			for (TPCoord coordInProvince : coordsInProvince) {
				if (coordInProvince.getX() < minX)
					continue;
				else if (coordInProvince.getX() > maxX)
					continue;
				else if (coordInProvince.getZ() < minZ)
					continue;
				else if (coordInProvince.getZ() > maxZ)
					continue;
				numProvinceBlocksInSpecifiedArea++;
			}
			if(numProvinceBlocksInSpecifiedArea > (coordsInProvince.size() / 2)) {
				TownyProvincesDataHolder.getInstance().deleteProvince(province);
				numProvincesDeleted++;
			}
		}
		TownyProvinces.info("" + numProvincesDeleted + " provinces deleted.");
		return true;
	}

	/**
	 * Generate province objects, including
	 * - Homeblocks
	 * - New town prices
	 * - Upkeep town prices
	 *
	 * @return false if we failed to create sufficient province objects
	 */
	private boolean generateProvinceObjects() {
		TownyProvinces.info("Now generating province objects");

		Province province;
		int idealNumberOfProvinces = calculateIdealNumberOfProvinces();
		for (int provinceIndex = 0; provinceIndex < idealNumberOfProvinces; provinceIndex++) {
			province = generateProvinceObject();
			if(province != null) {
				//Province object created successfully. Add to data holder
				TownyProvincesDataHolder.getInstance().addProvince(province);
			} else {
				//Could not generate a province homeblock. Ran out of space on the map
				double minimumAllowedNumProvinces = ((double) idealNumberOfProvinces) * (1 - allowedVarianceBetweenIdealAndActualNumProvinces);
				int actualNumProvinces = TownyProvincesDataHolder.getInstance().getNumProvinces();
				if (actualNumProvinces < minimumAllowedNumProvinces) {
					TownyProvinces.severe("ERROR: Could not create the minimum number of provinces objects. Required: " + minimumAllowedNumProvinces + ". Actual: " + actualNumProvinces);
					return false;
				} else {
					TownyProvinces.info("" + actualNumProvinces + " province objects created.");
					return true;
				}
			}
		}
		TownyProvinces.info("" + TownyProvincesDataHolder.getInstance().getNumProvinces() + " province objects created.");
		return true;
	}
	
	private int calculateIdealNumberOfProvinces() {
		double regionAreaSquareMetres = calculateRegionAreaSquareMetres();
		int idealNumberOfProvinces = (int)(regionAreaSquareMetres / provinceSizeEstimateForPopulatingInSquareMetres);
		TownyProvinces.info("Ideal num provinces: " + idealNumberOfProvinces);
		return idealNumberOfProvinces;
	}

	private double calculateRegionAreaSquareMetres() {
		double sideLengthX = Math.abs(topLeftRegionCorner.getX() - bottomRightRegionCorner.getX());
		double sideLengthZ = Math.abs(topLeftRegionCorner.getZ() - bottomRightRegionCorner.getZ());
		double worldAreaSquareMetres = sideLengthX * sideLengthZ;
		TownyProvinces.info("World Area square metres: " + worldAreaSquareMetres);
		return worldAreaSquareMetres;
	}
	
	/**
	 * Generate a single province object
	 *
	 * @return the province on success, or null if you fail (usually due to map being full)
	 */
	private @Nullable Province generateProvinceObject() {
		boolean isSea = false;
		boolean landValidationRequested = false;
		//Establish boundaries of where the homeblock might be placed
		double xLowest = regionMinX + brushSquareRadiusInChunks + 3;
		double xHighest = regionMaxX - brushSquareRadiusInChunks + 3;
		double zLowest = regionMinZ + brushSquareRadiusInChunks + 3;
		double zHighest = regionMaxZ - brushSquareRadiusInChunks + 3;
		//Try a few times to place the homeblock
		for(int i = 0; i < 100; i++) {
			//Pick a random location
			double x = xLowest + (Math.random() * (xHighest - xLowest));
			double z = zLowest + (Math.random() * (zHighest - zLowest));
			Coord coord = Coord.parseCoord((int)x,(int)z);
			int xCoord = coord.getX();
			int zCoord = coord.getZ();
			TPCoord homeBlockCoord = new TPFinalCoord(xCoord, zCoord);
			//Create province object
			Province province = new Province(homeBlockCoord, isSea, landValidationRequested, newTownCost, upkeepTownCost);
			//Validate province homeblock position
			if(validatePositionOfProvinceHomeBlock(province)) {
				return province;
			}
		}
		return null;
	}
	
	private boolean validatePositionOfProvinceHomeBlock(Province newProvince) {
		//Make sure it is far enough from other homeblocks
		TPCoord provinceHomeBlock = newProvince.getHomeBlock();
		for(Province province: TownyProvincesDataHolder.getInstance().getProvincesSet()) {
			if(TownyProvincesMathUtil.distance(provinceHomeBlock, province.getHomeBlock()) < minAllowedDistanceBetweenProvinceHomeBlocksInChunks) {
				return false;
			}
		}
		//Make sure that it is far enough from other provinces
		if(validateBrushPosition(provinceHomeBlock.getX(), provinceHomeBlock.getZ(), newProvince)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean executeChunkClaimCompetition() {
		TownyProvinces.info("Chunk Claim Competition Started");

		//Create claim-brush objects
		List<ProvinceClaimBrush> provinceClaimBrushes = new ArrayList<>();
		for(Province province: TownyProvincesDataHolder.getInstance().getProvincesSet()) {
			provinceClaimBrushes.add(new ProvinceClaimBrush(province, brushSquareRadiusInChunks));
		}
		
		/*
		 * First claim once around the homeblocks
		 * Note: We assume that the homeblocks have all been put in valid positoins
		 */
		for(ProvinceClaimBrush provinceClaimBrush: provinceClaimBrushes) {
			claimChunksCoveredByBrush(provinceClaimBrush);
		}

		//Execute province painting competition
		int moveDeltaX;
		int moveDeltaZ;
		int newX;
		int newZ;
		for(int i = 0; i < numberOfProvincePaintingCycles; i++) {
			TownyProvinces.info("Painting Cycle: " + i + " / " + numberOfProvincePaintingCycles);
			for(ProvinceClaimBrush provinceClaimBrush: provinceClaimBrushes) {
				//If inactive, do nothing
				if(!provinceClaimBrush.isActive())
					continue;
				//Generate random move delta
				moveDeltaX = TownyProvincesMathUtil.generateRandomInteger(-maxBrushMoveAmount, maxBrushMoveAmount);
				moveDeltaZ = TownyProvincesMathUtil.generateRandomInteger(-maxBrushMoveAmount, maxBrushMoveAmount);
				//Apply min move amount
				moveDeltaX = moveDeltaX > 0 ? Math.max(moveDeltaX,minBrushMoveAmount) : Math.min(moveDeltaX,-minBrushMoveAmount);
				moveDeltaZ = moveDeltaZ > 0 ? Math.max(moveDeltaZ,minBrushMoveAmount) : Math.min(moveDeltaZ,-minBrushMoveAmount);
				//Move brush if possible
				newX = provinceClaimBrush.getCurrentPosition().getX() + moveDeltaX;
				newZ = provinceClaimBrush.getCurrentPosition().getZ() + moveDeltaZ;
				boolean brushMoved = moveBrushIfPossible(provinceClaimBrush, newX, newZ);
				//Claim chunks
				if(brushMoved)
					claimChunksCoveredByBrush(provinceClaimBrush);
				//Deactivate if too many chunks have been claimed
				if(hasBrushHitClaimLimit(provinceClaimBrush)) {
					provinceClaimBrush.setActive(false);
				}
			}
		}
		TownyProvinces.info("Chunk Claim Competition Complete.");
		TownyProvinces.info("Num Chunks Claimed: " + TownyProvincesDataHolder.getInstance().getCoordProvinceMap().size());
		TownyProvinces.info("Num Chunks Unclaimed: " + unclaimedCoordsMap.size());
		return true;
	}

	/**
	 * Move the brush unless the new position would be:
	 *  - off the map 
	 *  - or too close to another province.
	 *
	 * @param brush given brush
	 */
	private boolean moveBrushIfPossible(ProvinceClaimBrush brush, int newX, int newZ) {
		if(validateBrushPosition(newX, newZ, brush.getProvince())) {
			brush.moveBrushTo(newX, newZ);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Validate that it is ok to put the brush at the given coord.
	 * It is not ok if:
	 * - It would paint off the map
	 * - It would paint on another province
	 *
	 * @return true if it's ok
	 */
	public boolean validateBrushPosition(int brushPositionCoordX, int brushPositionCoordZ, Province provinceBeingPainted) {
		int brushMinCoordX = brushPositionCoordX - brushSquareRadiusInChunks;
		int brushMaxCoordX = brushPositionCoordX + brushSquareRadiusInChunks;
		int brushMinCoordZ = brushPositionCoordZ - brushSquareRadiusInChunks;
		int brushMaxCoordZ = brushPositionCoordZ + brushSquareRadiusInChunks;
		Province province;
		for(int x = brushMinCoordX -3; x <= (brushMaxCoordX +3); x++) {
			for(int z = brushMinCoordZ -3; z <= (brushMaxCoordZ +3); z++) {
				//Fail if the target coord is off the map
				if (x < mapMinXCoord)
					continue;
				else if (x > mapMaxXCoord)
					continue;
				else if (z < mapMinZCoord)
					continue;
				else if (z > mapMaxZCoord)
					continue;
				//Fail if the target coord is owned by a different province
				province = TownyProvincesDataHolder.getInstance().getProvinceAt(x,z);
				if(province != null && province != provinceBeingPainted) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Claim chunks covered by brush
	 * Assume that all the checks related to other provinces
	 * and the edge of the map, have already been done
	 *
	 * @param brush the brush
	 */
	private void claimChunksCoveredByBrush(ProvinceClaimBrush brush) {
		int startX = brush.getCurrentPosition().getX() - brush.getSquareRadius();
		int endX = brush.getCurrentPosition().getX() + brush.getSquareRadius();
		int startZ = brush.getCurrentPosition().getZ() - brush.getSquareRadius();
		int endZ = brush.getCurrentPosition().getZ() + brush.getSquareRadius();
		for(int x = startX; x <= endX; x++) {
			for(int z = startZ; z <= endZ; z++) {
				//Don't claim if already claimed by the province
				searchCoord.setValues(x,z);
				if(!unclaimedCoordsMap.containsKey(searchCoord))
					return;
				//Claim chunk
				TownyProvincesDataHolder.getInstance().claimCoordForProvince(unclaimedCoordsMap.get(searchCoord), brush.getProvince());
				brush.registerChunkClaimed();
				unclaimedCoordsMap.remove(searchCoord);
			}
		}
	}

	/**
	 * Claim the given chunk, unless another province has already claimed it
	 * Or near edge of map, or near other province,
	 */
	private void claimUnclaimedChunk(int coordX, int coordZ, ProvinceClaimBrush brush) {
		//Don't claim if already claimed
		searchCoord.setValues(coordX,coordZ);
		if(!unclaimedCoordsMap.containsKey(searchCoord))
			return;

		//Don't claim near other provinces
		Province province = brush.getProvince();
		Province adjacentProvince;
		int[] x = new int[]{-1,0,1,-1,1,-1,0,1};
		int[] z = new int[]{-1,-1,-1,0,0,1,1,1};
		for(int i = 0; i < 8; i++) {
			adjacentProvince = TownyProvincesDataHolder.getInstance().getProvinceAt(coordX + x[i], coordZ + z[i]);
			if(adjacentProvince != null && adjacentProvince != province) {
				return;
			}
		}

		//Claim chunk
		TownyProvincesDataHolder.getInstance().claimCoordForProvince(unclaimedCoordsMap.get(searchCoord), province);
		brush.registerChunkClaimed();
		unclaimedCoordsMap.remove(searchCoord);
	}


	private boolean hasBrushHitClaimLimit(ProvinceClaimBrush provinceClaimBrush) {
		double currentClaimArea = provinceClaimBrush.getNumChunksClaimed() * CHUNK_AREA_IN_SQUARE_METRES;
		return currentClaimArea > claimAreaLimitInSquareMetres;
	}



}