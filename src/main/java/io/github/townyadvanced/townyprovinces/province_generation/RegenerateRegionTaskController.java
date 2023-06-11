package io.github.townyadvanced.townyprovinces.province_generation;

import com.palmergames.bukkit.towny.object.Translatable;
import io.github.townyadvanced.townyprovinces.TownyProvinces;
import io.github.townyadvanced.townyprovinces.messaging.Messaging;
import org.bukkit.command.CommandSender;

public class RegenerateRegionTaskController {

	private static RegenerateRegionTask regenerateRegionTask = null;
	
	public static boolean startTask(CommandSender sender, String givenregionName) {
		if (regenerateRegionTask != null) {
			Messaging.sendMsg(sender, Translatable.of("msg_err_regeneration_job_already_started"));
			return false;
		} else {
			TownyProvinces.info("Region Regeneration Job Starting");
			regenerateRegionTask = new RegenerateRegionTask(givenregionName);
			regenerateRegionTask.runTaskAsynchronously(TownyProvinces.getPlugin());
			Messaging.sendMsg(sender, Translatable.of("msg_region_regeneration_job_started", givenregionName));
			return true;
		}
	}

	public static void endTask() {
		if(regenerateRegionTask != null) {
			regenerateRegionTask.cancel();
			regenerateRegionTask = null;
		}
	}

}

 