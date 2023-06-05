package io.github.townyadvanced.townyprovinces.settings;

public enum ConfigNodes {
	
	VERSION_HEADER("version", "", ""),
	VERSION(
			"version.version",
			"",
			"# This is the current version.  Please do not edit."),
	LANGUAGE("language",
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

	ACTIVE_BORDER(
		"active_border",
		"",
		""),
	ACTIVE_BORDER_WEIGHT(
		"active_border.weight",
		"1",
		"",
		"# This value determines the weight of the border."),
	ACTIVE_BORDER_OPACITY(
		"active_border.opacity",
		"1",
		"",
		"# This value determines the opcacity of the border."),
	ACTIVE_BORDER_COLOUR(
		"active_border.color",
		"0",
		"",
		"# This value determines the color of the border."),
	DELETED_BORDER(
		"deleted_border",
			"",
			""),
	DELETED_BORDER_WEIGHT(
		"deleted_border.weight",
			"1",
			"",
			"# This value determines the weight of the border."),
	DELETED_BORDER_OPACITY(
		"deleted_border.opacity",
			"0.1",
			"",
			"# This value determines the opcacity of the border."),
	DELETED_BORDER_COLOUR(
		"deleted_border.color",
			"33FFFF",
			"",
			"# This value determines the color of the border.");
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
