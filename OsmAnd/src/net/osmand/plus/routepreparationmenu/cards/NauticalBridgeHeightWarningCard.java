package net.osmand.plus.routepreparationmenu.cards;

import androidx.annotation.NonNull;

import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.settings.fragments.BaseSettingsFragment;

public class NauticalBridgeHeightWarningCard extends WarningCard {

	public NauticalBridgeHeightWarningCard(@NonNull MapActivity mapActivity) {
		super(mapActivity);
		imageId = R.drawable.ic_action_sail_boat_dark;
		title = mapActivity.getString(R.string.vessel_height_warning);
		linkText = mapActivity.getString(R.string.vessel_height_warning_link);
	}

	@Override
	protected void onLinkClicked() {
		BaseSettingsFragment.showInstance(mapActivity, BaseSettingsFragment.SettingsScreenType.VEHICLE_PARAMETERS,
				mapActivity.getRoutingHelper().getAppMode());
	}
}
