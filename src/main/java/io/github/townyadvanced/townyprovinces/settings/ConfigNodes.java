package io.github.townyadvanced.townyprovinces.settings;

public enum ConfigNodes {
	
	VERSION_HEADER("version", "", ""),
	VERSION(
		"version.version",
		"",
		"# This is the current version.  Please do not edit."),
	LANGUAGE(
		"language",
		"english.yml",
		"# The language file you wish to use"),
	ENABLED(
		"enabled",
		"true",
		"",
		"# If true, the TownyProvinces plugin is enabled."),
	WORLD_NAME(
		"world_name",
		"world",
		"",
		"# The name of the world where TownyProvinces appplies.",
		"# TownyProvinces does not yet support multiple worlds"),
	BIOME_LOOKUP_BY_BLOCK(
		"biome_lookup_by_block",
		"false",
		"",
		"# SETTING: FALSE",
		"# Set this to false if the biome information in your world has been setup correctly.",
		"# Then the land-validation job will get biome information using calls to getBiome().",
		"# ",
		"# SETTING: TRUE",
		"# Set this to true if the biome information in your world has NOT been setup correctly.",
		"# Then the land-validation job will get biome information based on block material.",
		"# The job will run slower, but it will work properly.",
		"# ",
		"# NOTE: With either of these settings, the automatic biome-estimations will not be 100% accurate.",
		"# So expect to do a few manual runs of 'tpra province sea x,z' and 'tpra province land x,z'"),
	BIOME_COST_ADJUSTMENTS(
		"biome_cost_adjustments",
		"",
		"",
		"",
		"############################################################",
		"# +------------------------------------------------------+ #",
		"# |                 BIOME COST ADJUSTMENTS               | #",
		"# +------------------------------------------------------+ #",
		"############################################################",
		""),
	BIOME_COST_ADJUSTMENTS_ENABLED(
		"biome_cost_adjustments.enabled",
		"true",
		"",
		"# If this is true, then province costs are adjusted by the biomes contained in the province."),
	BIOME_COST_ADJUSTMENTS_WATER(
		"biome_cost_adjustments.water",
		"0.03",
		"",
		"# Assuming server doesn't allow modifying coastline, these chunks can only be settled by going underground."),
	BIOME_COST_ADJUSTMENTS_HOT_LAND(
		"biome_cost_adjustments.hot_land",
		"0.3",
		"",
		"# Desert. Hard to grow crops, can't find animals, and sand is easily griefable."),
	BIOME_COST_ADJUSTMENTS_COLD_LAND(
		"biome_cost_adjustments.cold_land",
		"0.1",
		"",
		"# Snow and ice. Very hard to live in."),
	PROVINCE_VISUALS(
		"province_visuals",
		"",
		"",
		"",
		"############################################################",
		"# +------------------------------------------------------+ #",
		"# |                   PROVINCE VISUALS                   | #",
		"# +------------------------------------------------------+ #",
		"############################################################",
		""),
	LAND_PROVINCE_BORDER(
		"province_visuals.land_province_border",
		"",
		""),
	LAND_PROVINCE_BORDER_WEIGHT(
		"province_visuals.land_province_border.weight",
		"1",
		"",
		"# This value determines the weight of the border."),
	LAND_PROVINCE_BORDER_OPACITY(
		"province_visuals.land_province_border.opacity",
		"1",
		"",
		"# This value determines the opacity of the border."),
	LAND_PROVINCE_BORDER_COLOUR(
		"province_visuals.land_province_border.color",
		"0",
		"",
		"# This value, in hex format, determines the color of the border."),
	SEA_PROVINCE_BORDER(
		"province_visuals.sea_province_border",
		"",
		""),
	SEA_PROVINCE_BORDER_WEIGHT(
		"province_visuals.sea_province_border.weight",
		"1",
		"",
		"# This value determines the weight of the border."),
	SEA_PROVINCE_BORDER_OPACITY(
		"province_visuals.sea_province_border.opacity",
		"0.1",
		"",
		"# This value determines the opcacity of the border."),
	SEA_PROVINCE_BORDER_COLOUR(
		"province_visuals.sea_province_border.color",
		"33FFFF",
		"",
		"# This value, in hex format, determines the color of the border."),
	TRAVEL(
		"travel",
		"",
		"",
		"",
		"############################################################",
		"# +------------------------------------------------------+ #",
		"# |                         TRAVEL                       | #",
		"# +------------------------------------------------------+ #",
		"############################################################",
		""),	
	ROADS(
		"travel.roads",
		"",
		"",
		"",
		"# +------------------------------------------------------+ #",
		"# |                         ROADS                        | #",
		"# +------------------------------------------------------+ #",
		""),
	ROADS_ENABLED(
		"travel.roads.enabled",
		"true",
		"",
		"# If this value is true, then roads are enabled."),
	ROADS_MAX_FAST_TRAVEL_RANGE(
		"travel.roads.fast_travel_range",
		"5",
		"",
		"# This value determines the fast-travel range of ports, in number-of-homeblocks-traversed"),
	PORTS(
		"travel.ports",
		"",
		"",
		"",
		"# +------------------------------------------------------+ #",
		"# |                         PORTS                        | #",
		"# +------------------------------------------------------+ #",
		""),
	PORTS_ENABLED(
		"travel.ports.enabled",
		"true",
		"",
		"# If this value is true, then ports are enabled."),
	PORTS_PURCHASE_PRICE(
		"travel.ports.purchase_cost",
		"50",
		"",
		"# The value determines the purchase price of a port plot."),
	PORTS_UPKEEP_COST(
		"travel.ports.upkeep_cost",
		"5",
		"",
		"# The value determines the upkeep cost of a port plot.",
		"# This is on top of any normal plot upkeep cost."),
	PORTS_MAX_FAST_TRAVEL_RANGE(
		"travel.ports.fast_travel_range",
		"3000",
		"",
		"# This value determines the fast-travel range of ports"),
	JUMP_NODES(
		"travel.jump_nodes",
		"",
		"",
		"",
		"# +------------------------------------------------------+ #",
		"# |                     JUMP NODES                       | #",
		"# +------------------------------------------------------+ #",
		""),
	JUMP_NODES_ENABLED(
		"travel.jump_nodes.enabled",
		"true",
		"",
		"# If this value is true, then jump nodes are enabled."),
	JUMP_NODES_PURCHASE_PRICE(
		"travel.jump_nodes.purchase_cost",
		"200",
		"",
		"# The value determines the purchase price of a jump node."),
	JUMP_NODES_UPKEEP_COST(
		"travel.jump_nodes.upkeep_cost",
		"20",
		"",
		"# The value determines the upkeep cost of a jump node plot.",
		"# This is on top of any normal plot upkeep cost.");

	private final String Root;
	private final String Default;
	private String[] comments;

	ConfigNodes(String root, String def, String... comments) {

		this.Root = root;
		this.Default = def;
		this.comments = comments;
	}

	/**
	 * Retrieves the root for a config option
	 *
	 * @return The root for a config option
	 */
	public String getRoot() {

		return Root;
	}

	/**
	 * Retrieves the default value for a config path
	 *
	 * @return The default value for a config path
	 */
	public String getDefault() {

		return Default;
	}

	/**
	 * Retrieves the comment for a config path
	 *
	 * @return The comments for a config path
	 */
	public String[] getComments() {

		if (comments != null) {
			return comments;
		}

		String[] comments = new String[1];
		comments[0] = "";
		return comments;
	}

}
